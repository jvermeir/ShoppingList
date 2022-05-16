# Log book

This file is a history of the experiments I've done and what I learned along the way.

## 20220516 

There isn't much logic we can test, but Recipe does complex things that may fail (we probably won't use the service like this, but for now I'm leaving it in).
I've using Mockito to mock all database calls. This leads to rather large tests, see 

``` 
  @Test
  fun `recipeDetails are saved in recipe, recipeDetails and ingredients tables`() {
```

for an example.

It might be better to just extend `IntegrationTest.kt`?

## 20220515 

Last week I added a comment to `RecipeService.post()` saying that I should test if a category exists for a given ingredient in a recipe. This is a consequence of a bold statement I made earlier, 
saying `For now, I gave up on foreign keys and will just put the logic in Kotlin code`. Ha! So now I'm trying to find a nice way to implement that logic in the Recipe class. 
One solution would look like this:

```
      val category = categoryDb.findById(it.categoryId)
      if (category == null)
        throw ResourceNotFoundException("category with id ${it.categoryId} or name ${it.categoryName} not found")
```

Classic, works just fine. But how can I do this in a more Kotlinish way? I thought something like this should work:

```
      categoryDb.findById(it.categoryId).orElseThrow(
          ResourceNotFoundException("category with id ${it.categoryId} or name ${it.categoryName} not found"))
```

If findById succeeds we know It's ok to continue, if not we give up by throwing an exception. Now the compiler complains that I should impolement a Supplier. Here's my next attempts:

```
      categoryDb
        .findById(it.categoryId)
        .orElseThrow(Supplier { throw ResourceNotFoundException("category with id ${it.categoryId} or name ${it.categoryName} not found") })
```

This is ok, but can be improved (as suggested by IntelliJ), because the `Supplier` classname is redundant. The final version looks like this:

```
      categoryDb
        .findById(it.categoryId)
        .orElseThrow({ throw ResourceNotFoundException("category with id ${it.categoryId} or name ${it.categoryName} not found") })
```



## 20220514 

Updating an existing record has its challenges. I wanted an insert-or-update method that creates a record if it doesn't exist and updates if it does. `save()` does exactly that, but only 
based on the key. I may have complicated things by 
introducing an `id` field that works as a primary key and a `name` that should be unique. This complicates the behavior of 
`save()`. If a category with a non-empty id is received, we can assume an update, so a regular `save()` call will work in that case. 
If the id field is empty, but the name field isn't, we might be updating an existing record (setting its shopOrder field), so I first look for the category by name and update if it exists. 
This update works with a neat Kotlin feature called `copy()` that copies all fields of a class and overrides values passed as an argument: `return db.save(cat.copy(shopOrder=category.shopOrder))`.
The code in the category service looks like this: 

```
  fun post(category: Category): Category {
    if (category.id != null) {
      return db.save(category)
    } else {
      val cat = db.findByName(category.name)
      if (cat!=null) {
        return db.save(cat.copy(shopOrder=category.shopOrder))
      }
      return db.save(category)
    }
  }
```

It seems there's a difference between doing this in the RestController: 

```
  @PostMapping("/recipe")
  fun post(@RequestBody recipe: Recipe) = ResponseEntity(recipeService.post(recipe),  HttpStatus.CREATED)
```

and this 

```
@PostMapping("/recipe")
fun post(@RequestBody recipe: Recipe) { ResponseEntity(recipeService.post(recipe),  HttpStatus.CREATED)}
```

The latter returns the result of recipeService.post(recipe) but as text and with an empty response body, rather than `Content-Type : application/json` and the JSON version of the new recipe. 
This led me to set a rule that methods in the RestControllers can only be one-liners, which is probably a good idea anyway. But still, it would be nice to know what happens exactly. 

## 20220508 

I'm struggling with defaults in json. Calling a function like `fun post(category: Category) = db.save(category)` through the web api like this:

```
POST http://localhost:8080/api/category
Content-Type: application/json

{"name": "gebak", "shopOrder": "30"}
```

so without a value for the `id` field, works just fine. But then I was writing a utility that inserts some data using the web api. I've built this as a Kotlin program:

```
inline fun <reified T> save(data: T, path: String): T {
  val (_, _, result) = "${baseUrl}/${path}".httpPost().jsonBody(Json.encodeToString(data))
    .responseString()
  return Json.decodeFromString<T>(result.get())
}
```

This is a little complex, but I wanted the generics because I have four domain concepts and didn't want to repeat code. 

To call this I use:

```
save(Category(id = null, name = "cat1", shopOrder = 10), path = "category")
```

So with an explicit null value for `id` because without it the code doesn't compile. Maybe I should use a default value of null in the `Category` data class?
Or I could write a serializer? Or is this problem caused by reusing code that lives in a service? 

All data classes now have a `@Serialization` annotation. This isn't necessary for Spring because it will automatically set `Content-Type: application/json`. 
It is necessary if you want to use `httpPost().jsonBody(Json.encodeToString(data))` as shown above. 

Other than that, it feels like some structure is evolving. As an experiment I grouped all `Category` related code in `Category.kt`. This helps me keeping track better than having a file per layer, but it's still early, so we'll see.  

The http client is fuel (https://github.com/kittinunf/Fuel) which works well enough. See `IngegrationTest.kt` for details.

## 20220506 

I wanted to implement a `findById` method on the category service. This finder uses the default `findById` from the category repository. 
That method returns an Optional which can't be used as the result of a findById method in CategoryService. 

```
@Table("CATEGORIES")
data class Category(@Id val id: String?, val name: String, val shopOrder: Int)
interface CategoryRepository : CrudRepository<Category, String> {
...
```
and 
```
@Service
class CategoryService(val db: CategoryRepository) {
...
  fun findById(id: String):Optional<Category> = db.findById(id)
}
```
and finally 
```
@RestController
class CategoryResource(val categoryService: CategoryService) {
...
  @GetMapping("/category/{id}")
  fun getCategoryById(@PathVariable(name = "id") id:String): ResponseEntity<Category> {
    return ResponseEntity.ok(categoryService.findById(id).get());
  }
```
Now `findById` may throw `NoSuchElementException`, so I've added a exception handler in `ErrorHandler.kt`.  

## 20220505 

Using a Java based example in https://www.javaguides.net/2021/10/spring-boot-exception-handling-example.html, I've implemented a form of error handling and 
custom error responses for the category controller. It's nice to see how 40+ lines of Java code are condensed into 2 oneliners of Kotlin. I was feeling dubious about 
the aspect thingy to handle exceptions, but after trying it out it does make sense. Using the `@ControllerAdvice` annotation makes aspects painless. 

One aspect where Java and Spring come close to the Kotlin implementation is illustrated by the service implementation. 

```
    @Override
    public PostDto getPostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return mapToDTO(post);
    }
```    
becomes 

```
    fun getCategoryByName(name: String):Category =
        db.findByName(name) ?: throw ResourceNotFoundException("Category '${name}' not found")
```
in Kotlin. If the record isn't found in the database (`db.findByName(name)` returns null), the `?:` operator makes sure the exception is thrown. 
Much like `.orElseThrow(()...` in the Java version. 


## 20220504 

TODO: 
- test
  - https://kotlinlang.org/api/latest/kotlin.test/
- dependency injection 
  - https://blog.kotlin-academy.com/dependency-injection-the-pattern-without-the-framework-33cfa9d5f312
  - https://code.imaginesoftware.it/kotlin-and-the-simplest-dependency-injection-tutorial-ever-b437d8c338fe
  - https://developer.android.com/training/dependency-injection
  - https://auth0.com/blog/dependency-injection-with-kotlin-and-koin/
  - https://ktor.io/
- persistence 
  - https://www.baeldung.com/kotlin/exposed-persistence, https://github.com/JetBrains/Exposed/wiki
  - https://www.ktorm.org/
  - https://github.com/leszko/springboot-caching (use caching config)


## 20220430

Based on the kotlin/spring-boot tutorial, I've added a category table and REST endpoints to add and list categories.
There's also a sort of datamodel in apps/shop-api/doc. 

It turns out that table names in `@Table` annotations are case-sensitive, so you should use

```
@Table("CATEGORIES")
```

and not its lowercase version. 
Experiments with entity and references from one table to another failed for the time being. Since I've been working on a DynamoDB project for a customer, where foreign keys are all in the eye of the beholder, 
I don't really mind much. Maybe decades of conditioning with 3rd normal form will catch up with me later. For now, I gave up on
foreign keys and will just put the logic in Kotlin code. 

## 20220427

Copying code from the NX tutorial, chapter 4 (https://nx.dev/react-tutorial/04-connect-to-api). 
I've used a menu item with a name instead of todos. The tutorial code goes into `apps/shop/src/app/app.tsx`.
Instead of a node service, I'll use a Kotlin/SpringBoot service. To create a skeleton I've used a NX plugin called nx rocks (https://www.npmjs.com/package/@nxrocks/nx-spring-boot). 

```
npm install @nxrocks/nx-spring-boot --save-dev
nx g @nxrocks/nx-spring-boot:new shop-api
```

Answer the questions like this:

```
nx g @nxrocks/nx-spring-boot:new shop-api
✔ What kind of project are you generating? · application
✔ Which build system would you like to use? · gradle-project
✔ Which packaging would you like to use? · jar
✔ Which version of Java would you like to use? · 11
✔ Which language would you like to use? · kotlin
✔ What groupId would you like to use? · nl.vermeir
✔ What artifactId would you like to use? · shop-api
✔ What package name would you like to use? · nl.vermeir.shop
✔ What is the project about? · shop till you drop
✔ What dependencies would you like to use (comma separated)?
```

This creates a folder named `shop/apps/shop-api` and adds a line to `shop/workspace.json`: 

```
"shop-api": "apps/shop-api"
```

I'll replace the files in shop-api by a starter app generated on the spring boot website, except `project.json`. This file defines the build targets 
needed for the Kotlin service, so I'll keep it. 
To generate a shop-api Kotlin service, I've used this tutorial https://kotlinlang.org/docs/jvm-spring-boot-restful.html. This first creates a starter application and a zip file. 
I've downloaded the zip file and used it to replace the contents of `shop/apps/shop-api`, but I've KEPT THE PROJECT.JSON FILE as it was generated earlier.
The kotlin/spring-boot tutorial shows how to create web services and a database. Note that the table name needs to be uppercase in: 

```
@Table("MENUITEMS")
data class MenuItem(@Id val id: String?, val name: String)
```

Back to the nx tutorial here: https://nx.dev/react-tutorial/06-proxy. Since I didn't complete the step before (where a node service was created), I've just created `shop/proxy.conf.json` like this:

```
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false
  }
}
```

and also `nx g @nxrocks/nx-spring-boot:new shop-api` creates an api, but it doesn't register the proxy in `shop/project.json`. To fix that problem, add `proxyConfig...` at the end of the `serve` property:

```
    "serve": {
      "executor": "@nrwl/web:dev-server",
      "defaultConfiguration": "development",
      "options": {
        "buildTarget": "shop:build",
        "hmr": true,
        "proxyConfig": "apps/shop/proxy.conf.json"
      },
```

Also note that the Kotlin service defines paths like this: `  @GetMapping("/api/menuitems")`, so that includes `/api`. This prefix is the same as the prefix used in proxy.conf.json (see above).

To start the application we need to run two processes from the root folder of the shop project: 

```
# run the front end and the proxy to backend services
nx serve shop

# run the backend service 
nx serve shop-api 
```

I've tested the result using the `request.http` file in apps/shop-api. 

## 20220426

### Reboot of the shopping list project using NX

I've decided to build this new release centred on Typescript and React, with a Kotlin service to store data. Using NX it seems easiest to start with a React/Typescript starter and add a Kotlin service later.

The NX tutorial can be found here: https://nx.dev/react-tutorial/01-create-application

```
$ npx create-nx-workspace@latest

npx: installed 58 in 4.48s
✔ Workspace name (e.g., org name)     · shop
✔ What to create in the new workspace · react
✔ Application name                    · shop
✔ Default stylesheet format           · scss
/Users/jan/.npm/_npx/36286/lib/node_modules/create-nx-workspace/bin
✔ Use Nx Cloud? (It's free and doesn't require registration.) · No

 >  NX   Nx is creating your v14.0.3 workspace.

```

I've chosen react and scss (because I completed a course by Jonas Schmedtmann, see https://www.udemy.com/course/advanced-css-and-sass/. It takes a bit, but I think it was well worth my time and excellent value for money).

Start the app:

```
cd shop
nx serve shop
```

