## Test curls and queries

see [request.http](.//src/main/resources/request.http)

or use

```
curl -v --location localhost:8080/api/categories
curl -v --location localhost:8080/api/recipes
curl -v --location localhost:8080/api/category/2cf577fa-9c96-4aa7-8b63-ea79c3723adc
curl -v --location 'localhost:8080/api/category?name=groenteman'
curl -v --location 'localhost:8080/api/recipe?name=Risotto%20met%20winterpeen%20en%20paddenstoelspekjes'
curl -v --location 'localhost:8080/api/recipe-ingredient?recipeId=07d9e9a7-23b4-4fcd-acd0-f1960365166f'

# Note, id is generated if left empty, this will trigger an insert
# if id is specified, an update is assumed by the save method in the database access layer

curl -v --location "http://127.0.0.1:8080/api/category" \
-H "Content-Type: application/json" \
-d "{\"name\": \"groente\", \"shopOrder\": 1 }"

curl -v -X DELETE --location "http://127.0.0.1:8080/api/category/eb71ce92-2058-4188-9f63-44892172e3a1"

curl -v -X PUT --location "http://127.0.0.1:8080/api/category" \
-H "Content-Type: application/json" \
-d "{\"id\": \"2cf577fa-9c96-4aa7-8b63-ea79c3723adc\",\"name\": \"groente\", \"shopOrder\": 2 }"

curl -v -X POST --location "http://127.0.0.1:8080/api/menu" \
-H "Content-Type: application/json" \
-d "{\"firstDay\": \"2023-10-21\"}"

curl -v -X POST --location "http://127.0.0.1:8080/api/menu-item" \
-H "Content-Type: application/json" \
-d "{\"menuId\": \"c1ec3773-2f04-4e0a-be14-fa287969a9fa\", \"recipeId\": \"dbf0b2ac-70c5-4af9-866b-f8dfa5b9082e\", \"theDay\":\"2023-10-21\"}"

curl -v -X POST --location "http://127.0.0.1:8080/api/menu-item" \
-H "Content-Type: application/json" \
-d "{\"menuId\": \"c1ec3773-2f04-4e0a-be14-fa287969a9fa\", \"recipeId\": \"6c52ea19-a773-449a-abe2-12737e34d432\", \"theDay\":\"2023-10-22\"}"

curl -v --location "http://127.0.0.1:8080/api/menu?firstDay=2023-10-21"

curl -v --location "http://127.0.0.1:8080/api/menu/details/firstDay/2023-10-21"

```

```
curl -v -X POST --location "http://127.0.0.1:8080/api/converters/categories/categoryDatabase_v2.csv" \
-H "Content-Type: application/json" \
-d "{}"
```

```
curl -v -X POST --location "http://127.0.0.1:8080/api/converters/cookbook/cookbook_v2.txt" \
-H "Content-Type: application/json" \
-d "{}"
```

```
curl -v -X POST --location "http://127.0.0.1:8080/api/cleanup" \
-H "Content-Type: application/json" \
-d "{}"
```

curl -v --location "http://localhost:8080/api/recipe-ingredients/Pompoensoep'

curl -v --location 'localhost:8080/api/ingredient?name=flammmkuchendeeg'
```
SELECT * FROM recipes WHERE name = 'Pompoensoep';

select i.NAME, i.id, c.NAME from recipe_ingredients ri, ingredients i, CATEGORIES c, recipes r
where ri.recipe_id = r.id
and r.name = 'Pompoensoep'
and c.id = i.category_id
and ri.ingredient_id = i.id;

select mi.*, r.*, i.*, c.* from MENUS m, MENU_ITEMS mi, RECIPES r, RECIPE_INGREDIENTS ri, INGREDIENTS i, CATEGORIES c
where m.FIRST_DAY='2023-10-21'
and m.id = mi.menu_id
and mi.recipe_id = r.id
and r.id = ri.recipe_id
and ri.ingredient_id = i.id
and i.category_id = c.id
;

```
