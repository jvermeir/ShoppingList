# Shop api

REST interface to store and retrieve data related to recipes, menus and shopping lists.

## Test curls

see [request.http](.//src/main/resources/request.http)

or use

```
curl -v --location localhost:8080/api/categories
curl -v --location localhost:8080/api/category/2cf577fa-9c96-4aa7-8b63-ea79c3723adc

# Note, id is generated if left empty, this will trigger an insert
# if id is specified, an update is assumed by the save method in the database access layer

curl -v --location "http://127.0.0.1:8080/api/category" \
-H "Content-Type: application/json" \
-d "{\"name\": \"groente\", \"shopOrder\": 1 }"

curl -v -X DELETE --location "http://127.0.0.1:8080/api/category/eb71ce92-2058-4188-9f63-44892172e3a1"

curl -v -X PUT --location "http://127.0.0.1:8080/api/category" \
-H "Content-Type: application/json" \
-d "{\"id\": \"2cf577fa-9c96-4aa7-8b63-ea79c3723adc\",\"name\": \"groente\", \"shopOrder\": 2 }"
```

```
curl -v -X POST --location "http://127.0.0.1:8080/api/converters/testCategoryDatabase.csv" \
-H "Content-Type: application/json" \
-d "{}"
```

```
curl -v -X POST --location "http://127.0.0.1:8080/api/cleanup" \
-H "Content-Type: application/json" \
-d "{}"
```
