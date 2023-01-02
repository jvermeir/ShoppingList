# Dynamo schema design

See [The DynamoDB Book](https://www.dynamodbbook.com/) for a description of the method.

## Entity Chart

| Entity            | PK  | SK  |
|-------------------|-----|-----|
| Menus             |     |     |
| MenuItems         |     |     |
| Recipes           |     |     |
| RecipeIngredients |     |     |
| Ingredients       |     |     |
| Categories        |     |     |

## Access Patterns

- CRUD on all entities
- Get a Menu and all its MenuItems
- Get a MenuItem and the details of its Recipe
- Get a Recipe and its list of Ingredients
- Get an Ingredient and its Category

The DynamoDB Book claims on page 306:

> We have a few patterns of "Fetch parent and all related items"
> (e.g. Fetch Customer and Orders for Customer). This indicates we’ll
> need to "pre-join" our data by locating the parent item in the same
> item collection as the related items.

This pattern also exists in my use case, so composite keys would seem appropriate.

## Categories

Categories have a unique `name` and an `id`. The id would never change during the lifetime of a category. A name may
change over time, but only rarely.
Both must be unique, so a category looks a lot like the Customer in the book. On page 307 the book suggests to create
two types. Translated to categories this would yield:

Category

PK: CATEGORY#<id>
SK: CATEGORY#<id>

CategoryName

PK: CATEGORYNAME#<name>
SK: CATEGORYNAME#<name>

| Partition Key           | Sort Key                | id  | name       | shopOrder |
|-------------------------|-------------------------|-----|------------|-----------|
| CATEGORY#1              | CATEGORY#1              | 1   | vegetables | 1         |
| CATEGORY#2              | CATEGORY#2              | 2   | pasta      | 2         |
| CATEGORYNAME#vegetables | CATEGORYNAME#vegetables | 1   | vegetables |           |
| CATEGORYNAME#pasta      | CATEGORYNAME#pasta      | 2   | pasta      |           |

### Updated entity chart

| Entity            | PK                  | SK                  |
|-------------------|---------------------|---------------------|
| Menus             |                     |                     |
| MenuItems         |                     |                     |
| Recipes           |                     |                     |
| RecipeIngredients |                     |                     |
| Ingredients       |                     |                     |
| Categories        | CATEGORY#<id>       | CATEGORY#<id>       |
| CategoryNames     | CATEGORYNAME#<name> | CATEGORYNAME#<name> |

## Ingredients

Ingredients are like categories, but they refer to a Category by its id. How can we query Ingredients by Category.id?
Ingredients are more like orders (see page 311): a customer has a list of orders === a category has a list of
ingredients.
So, we get a PK of categoryID and a SK of ingredientID, right?

Ingredient
PK: CATEGORY#<id>
SK: INGREDIENT#<id>

But, it would also be useful to query an ingredient by its name. So we need a second PK/SK pair:

IngredientName

PK: INGREDIENTNAME#<name>
SK: INGREDIENTNAME#<name>

Can't we fix that with a GSI? So instead of this

| Partition Key            | Sort Key                 | id  | name      | categoryId |
|--------------------------|--------------------------|-----|-----------|------------|
| CATEGORY#1               | INGREDIENT#1             | 1   | tomatoes  | 1          |
| CATEGORY#1               | INGREDIENT#3             | 3   | mushrooms | 1          |
| CATEGORY#2               | INGREDIENT#2             | 2   | spaghetti | 2          |
| INGREDIENTNAME#tomatoes  | INGREDIENTNAME#tomatoes  | 1   | tomatoes  |            |
| INGREDIENTNAME#mushrooms | INGREDIENTNAME#mushrooms | 3   | mushrooms |            |
| INGREDIENTNAME#spaghetti | INGREDIENTNAME#spaghetti | 2   | spaghetti |            |

The data would look like this:

| Partition Key | Sort Key     | id  | name      | categoryId | GSI1PK                   | GSI1SK                   |
|---------------|--------------|-----|-----------|------------|--------------------------|--------------------------|
| CATEGORY#1    | INGREDIENT#1 | 1   | tomatoes  | 1          | INGREDIENTNAME#tomatoes  | INGREDIENTNAME#tomatoes  |
| CATEGORY#1    | INGREDIENT#3 | 3   | mushrooms | 1          | INGREDIENTNAME#mushrooms | INGREDIENTNAME#mushrooms |
| CATEGORY#2    | INGREDIENT#2 | 2   | spaghetti | 2          | INGREDIENTNAME#spaghetti | INGREDIENTNAME#spaghetti |

This works indeed as expected. Using projection the GSI* columns can be removed from the query result.
Doing this however, allows duplicate names. hmpf.
So we need the extra records with the ingredientname anyway.
`one-to-many-option1.py` shows what is required if we need to query an ingredient by name without knowing its category.

#### Second attempt, starting on page 307 of the book.

Category and Ingredient have a one-to-many relationship. Categories are unique in their ID and their Name, as are
Ingredients.

Category:

PK: Category#<id>
SK: Category#<id>

CategoryName:

PK:CategoryName#<name>
SK:CategoryName#<name>

Ingredient: (SK prefixed with # so when we retrieve a category and its ingredients we can use the
`ScanIndexForward=False, Limit=11` trick to read only the first batch).

PK:Category#<id>
SK:#Ingredient#<id>

IngredientName:

PK:IngredientName#<name>
SK:IngredientName#<name>

While this works (see `one-to-many-option2.py`) we can't query by ingredientName anymore.

So we need GSI's. In one-to-many-option1.py:

```
  'Put': {
      'TableName': table_name,
      'Item': {
          'PK': {'S': 'CATEGORY#1'},
          'SK': {'S': 'INGREDIENT#1'},
          'id': {'S': '1'},
          'name': {'S': 'tomatoes'},
          'categoryId': {'S': '1'},
          'GSI1PK': {'S': 'INGREDIENT#1'},
          'GSI1SK': {'S': 'INGREDIENT#1'},
          'GSI2PK': {'S': 'INGREDIENTNAME#tomatoes'},
          'GSI2SK': {'S': 'INGREDIENTNAME#tomatoes'},
      },
      'ConditionExpression': 'attribute_not_exists(PK)'
  },
```

Inserts an ingredient. This allows three access patterns:

1. query by category id: retrieve all ingredients for a category
2. query by ingredient id: retrieve an ingredient by its id, using GSI1
3. query by ingredient name: retrieve an ingredient by its name, using GSI2

To make sure ingredient names are unique, we need a second record:

```
  'Put': {
      'TableName': table_name,
      'Item': {
          'PK': {'S': 'INGREDIENTNAME#tomatoes'},
          'SK': {'S': 'INGREDIENTNAME#tomatoes'},
          'id': {'S': '1'},
          'name': {'S': 'tomatoes'},
      },
      'ConditionExpression': 'attribute_not_exists(PK)'
  }
```

This ensures ingredient names will be unique. The code will fail if we try to insert a second ingredient with the same
name.
Without the ConditionExpression, any subsequent inserts would overwrite the first record. This may or may not be what you would expect in 
a specific case. 

What I don't like about this approach is that it's based on convention. PK needs to contain the value for name:

```
  'PK': {'S': 'INGREDIENTNAME#tomatoes'},
    ...
  'name': {'S': 'tomatoes'},
```

So, `tomatoes` in name is part of the PK ans SK values.

## Performance test

I had a test named `insert-a-lot-of-data.py` which inserts a large number of categories and ingredients and then 
tries to retrieve one by PK/SK. The idea was to find out if this is fast. The test is not very useful, because 
Dynamo will not allow queries without using a key. The test was iffy anyway because the real infrastructure for Dynamo 
is not comparable to a local Docker based setup.