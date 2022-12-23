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
Both must be unique, so a category looks a lot like the Customer in the book. on page 307 the book suggests to create
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


