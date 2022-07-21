# Log book

This file is a history of the experiments I've done and what I learned along the way.

## 20220715

I wanted to have a menu in the top bar of the application. To implement this, I need a router, `BrowserRouter` from `react-router-dom` seems easy enough: 

```
    <BrowserRouter>
      <App/>
    </BrowserRouter>
```

and then I defined the main routing options like this in `App`:

```
    <Routes>
      <Route path="/" element={<HelloPage />} />
      <Route path="/categories" element={<CategoriesPage />} />
      <Route path="/ingredients" element={<IngredientsPage />} />
    </Routes>
```

So now we can navigate to categories and ingredients, but still need a menu with options to click on. I've made this with a `Navigation` component. Navigation defines
an `<AppBar position="static">` with a nice icon to click on, a `Menu` and a list of `MuiMenuItem`s.

## 20220710

Adding code to change and add ingredients, adds a new feature: `ingredient`s have a reference to a `category`, but I wouldn't want to show UUIDs, I want to display the name of the category instead.
To fix that I introduced a new query in `Ingredient.kt`:

```
  @Query("SELECT i.id, i.name, i.category_id, c.name as category_name FROM ingredients i left outer join categories c on i.category_id = c.id")
  fun ingredientsView(): List<IngredientView>
```

This is named `*View` because it's intention is to show data rather than manipulate data. So I added a data class to represent an enhanced ingredient and a query/endpoint to retrieve the list. 

The code to manipulate ingredients looks a lot like the code to manipulate categories. In fact, all code in `/ingredient/` started out as a copy of `/category/`. This seems like a bit of a code smell, but 
of course there are differences, like the lookup query to show a dropdown of possible category values when editing an ingredient. Now the idea of just generating this kind of code has settled in 
my brain and a small voice keeps whispering how easy it was to create this kind of data entry code way-back-when I was still programming Oracle Forms. 

There are still problems with this code. One example is that I need to pass the list of categories from the ingredients-page all the way to the add-ingredient pop-up. This seems like a waste:
wouldn't it be easier to just store the category list in some kind of global cache? 

And related to that cache idea: if the ingredients-page is opened and a category is added, the new category doesn't show up in the drop-down on the ingredients page. It would be nice to have a 
subscription to changes such that categories are reloaded automatically.

Finally, when using this UI on a laptop, it would be nice to filter the list of categories by typing instead of having to scroll down a list. This won't be a problem for categories but it will

## 20220707

I've started on a proper UI, and it's been a bit of a struggle. First I thought to use SCSS because that's what I used for a training on CSS last year. Then I thought I'd be 
mainly building forms and lists and maybe a framework would give me some guidance. We've been using [Material UI](https://mui.com/) for a customer to build a support site for their customer care department and 
that seemed useful. In the sense that a lot of decisions are taken out of your hands which is a good thing at my current level of proficiency. 

So I started with a maintenance page for managing categories. This is a simple enough table like structure showing all current categories. There would be a button to add a new 
category and each category line would have a delete and edit icon. The general structure that worked for me is to start with a `categories.tsx` file in `apps/shop/src/app/pages`.
This page loads the categories from the api and defines a table along with a button to add a category and some handlers to show
a message while loading data or in case of an error. The lines in the table are handled by a 
separate component. This avoids cluttering the categories.tsx file. So all details to render a category in the list are in  `apps/shop/src/app/components/category.tsx`. 
Category.tsx is relatively simple. It shows one record in the category table. I've also added the logic to handle the delete function because it seems to small for its own file. 
Edit and Add are in separate files as well. This bugs me still because there's a lot of overlap in the two files. 

One piece of code that still gives me mixed feelings is this (from edit-category.tsx):

```
    submitApiRequest({id, name, shopOrder})
      .then((response) => checkResponse(response))      
      .then(() => cleanUp())
      .then(() => onCompleted())
      .catch(handleError)
      .finally(() => setShowConfirmation(true));
```

It stores the updated category and makes sure errors are handled correctly. It's easy to read in my opinion, but if something fails it's
harder to debug like I would do in Kotlin by setting break points.

This was all fine, but then I wanted a responsive UI, just like Jonas Schmedtmann demonstrates so well in his CSS training. And that is where 
I got lost in bleeding edge code and examples that are intended for the previous version of a framework or are incomplete because readers are supposed to have some more
background than I have. Finally, I ended up copying parts of the [Material UI examples](https://github.com/mui/material-ui), and in particular the
[React example](https://github.com/mui/material-ui/tree/master/examples/create-react-app-with-typescript). Copying this example to
[Code sandbox](https://codesandbox.io/) facilitated experimentation. At least now the font size of the Categories header on my 
page will respond to changes in screen size. Huray! 

The `bleeding edge` part of my doubts comes from this fragment in package.json:

```
    "@emotion/react": "latest",
    "@emotion/styled": "latest",
    "@mui/material": "latest"
```

This is equivalent to depending on snapshot in the Java world, which would definitely raise some eyebrows. I'm hoping to improve
this later when I better understand how it works. 

## 20220611

I've replaced the integration tests that rely on a running server by tests using WebMvcTest. This is of course way friendlier for tests running on a build server. I've mocked the database using
`MockkBean`:

```
@MockkBean
lateinit var recipeIngredientRepository: RecipeIngredientRepository
```

This allows more elegant mocks than Mockito would: 

```
every { recipeIngredientRepository.save(recipeIngredient1) } returns recipeIngredient1
```

Add this dependency to build.gradle.kts:

```
testImplementation ("com.ninja-squad:springmockk:3.1.1")
```

One pitfall I struggled with for while, is that when using WebMvcTest, you need to wire all dependencies needed by the test like this:

```
@WebMvcTest(value = [RecipeIngredientResource::class, RecipeIngredientService::class])
```

So in this case only the RecipeIngredientRepository is mocked but the service and rest layers are the real thing. 

## 20220529

Today I had to refactor my test code. The problem was that the tests are run in parallel and each tests starts by deleting all data from the database. This is a problem of running tests against a 
live server, of course, but since I have hardly any code at all except annotated methods, I thought it's better to run this kind of integration level testing. To make the parallel test work I first tried
to make the run sequentially by adding `maxParallelForks = 1` to `tasks.withType<Test> { ... }`. Either this syntax is not correct or Gradle ignores the directive. To make my tests more robust
I generate random UUIDs for test data. This makes each test run unique and leaves a lot of junk behind, but it is way more robust. 

Now I need to find a way to test this code in a meaningful way without starting servers. 

MockMvc might help: https://stackabuse.com/guide-to-unit-testing-spring-boot-rest-apis/
This would allow me to test the REST endpoints and service code, while mocking the database. 
Or how about https://springframework.guru/testing-spring-boot-restful-services/ ?
Or https://betterprogramming.pub/how-to-write-human-readable-tests-in-kotlin-with-kotest-and-mockk-1b614da32148

## 20220528

After a long struggle with different date formats, I've ditched Kotlin date and used `java.time.LocalDate`, but not in test code. This decision makes handling dates in REST apis and 
database calls way easier, because you don't need any explicit transformations at the repository layer anymore. 
You need to be specific about the format for a date at the api level. I've chosen to ignore the time part (this will probably hurt later when I'm building a UI, but we'll see). Date parameters are handled like this:

```
fun findByFirstDay(@RequestParam(name = "firstDay") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) firstDay: LocalDate) =
```

in the api layer. From that point on we've got a LocalDate instance which can be handled automatically by the repository code, i.e. the automatically created save and findById methods, but also
custom queries like this:

```
@Query("SELECT * FROM menus WHERE first_day  = :first_day")
fun findByFirstDay(first_day: LocalDate): Optional<Menu>
```

The only date-related problem left is when converting to and from JSON. To handle that case we need a serializer and a deserializer. 
Jackson allows this kind of code to translate to and from LocalDate:

```
object DateConversions {
  object Serializer : JsonSerializer<LocalDate>() {
    override fun serialize(value: LocalDate, gen: JsonGenerator, serializers: SerializerProvider) {
      with(gen) {
        writeString(value.toString())
      }
    }
  }

  object Deserializer : JsonDeserializer<LocalDate>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDate {
      val node = p.readValueAsTree<JsonNode>()
      return LocalDate.parse(node.textValue())
    }
  }
}
```

Kotlin's date handling still lives in test code. The test client defines its own domain classes, so there's a version of Menu in the test code:

```
@kotlinx.serialization.Serializable
data class Menu(@Id val id: String? = null, val firstDay: LocalDate)
```

where LocalDate is defined in `import kotlinx.datetime.*`. This allows elegant code like this to create a date instance: 

```
private val march10th = "2022-03-10".toLocalDate()
```

which is an extension method on String: 

```
public fun String.toLocalDate(): LocalDate = LocalDate.parse(this)
```


## 20220526

I've standardized Category, Recipe, Ingredient and RecipeIngredient classes and removed code I don't need yet. Also, I've added tests for each class using its REST endpoints. 
This means the tests are integration tests that require a running database (in memory for know) and application. This is not ideal, but on the other hand there isn't much code
to test anyway. I'll investigate TestContainers to see if that might help somehow. Thought I don't yet see how that would work on a CI/CD server.

Then I've added Menu and MenuItem, which is when I landed in Date hell. I now have 3 different date implementations: 
- a `kotlinx.datetime.LocalDate` for use in the Kotlin code
- `java.sql.Date` in queries and 
- a standard ISO date in the REST interface. 

- To make the LocalDate/REST date thingy work I've added a `JsonSerializer` and `JsonDeserializer`. JsonSerializer needs this

```
writeStringField("firstDay", value.toString())
```

which seems too specific because it is attached to the `firstDay` field like this:

```
@Table("MENUS")
data class Menu(@Id val id: String?,
                @JsonSerialize(using = DateConversions.Serializer::class)
                @JsonDeserialize(using = DateConversions.Deserializer::class)
                val firstDay: LocalDate)
```

To convert to java.sql.Date I've implemented a `@Converter`. For now Spring ignores this code. It's probably some kind of type issue. Maybe I'd be better of explicitly converting the date 
in the service layer?

## 20220521

I wanted to be able to post a category without specifying a value for id, so it can be auto generated by the repository layer. But I would also like to reuse the Category data class in test code. 
I added the @Serializable annotation on Category:

```
@Serializable
@Table("CATEGORIES")
data class Category(@Id val id: String?, val name: String, val shopOrder: Int)
```

With the annotation, each field of Category must have at least a value, even if its null:

```
message=Could not read JSON: Field 'id' is required for type with serial name 'nl.vermeir.shopapi.Category', but it was missing; nested exception is kotlinx.serialization.MissingFieldException: Field 'id' is required for type with serial name 'nl.vermeir.shopapi.Category', but it was missing, details=uri=/api/category)
```

Without @Serializable the auto generated Id field can be omitted. 

But if I remove @Serializable, my test code complains that it's missing a serializer. Argh! To solve this problem 
I copied the Category class to the test code and annotated it with @Serializable. It seems less than ideal to me, but not too bad, right? 

I've also set a default for the Id field:

```
data class Category(@Id val id: String? = null, val name: String, val shopOrder: Int)
```

This allows using Category like so:

```
val cat1 = Category(name = "cat1", shopOrder = 10)
```

## 20220520 

Switching to Kotest (kotest.io) turns out to be painful. Finding examples of code is easy, but finding a working setup with all dependencies in place was hard. 
The gradle build file now has these dependencies:

```
  testImplementation("io.kotest:kotest-framework-engine:5.2.2")
  testImplementation ("io.kotest:kotest-assertions-core:5.2.2")
```

and it needs a task with `useJUnitPlatform()`:

```
tasks.withType<Test> {
  useJUnitPlatform()
  testLogging.showStandardStreams = true

  testLogging {
    events ;"PASSED"; "FAILED"; "SKIPPED"; "STANDARD_OUT"; "STANDARD_ERROR"
  }
}
```

and the secret to get this to actually work is to add a `gradle.properties` file in the root of the Kotlin project. For now, it contains a single line:

```
kotlin-coroutines.version=1.6.0
```

Note the version says `1.6.0` and not `1.6.21` like I would expect based on the Kotlin version.

I've removed outdated tests and started on an integration level test that calls the api over http. Not too pretty, but given the limited business logic it sort of makes sense to me.

## 20220519 

I got stuck on the semantics of `save()`. I was assuming that if I would pass a value for the `id` field, it would be used in an insert. This is not the case, however. If a value for id is 
supplied, Spring assumes an update. Right. I've made a start with a test against a running service. This is not ideal but on the other hand, there is so little logic to test that 
any other kind of test seems useless. Maybe I should use https://www.testcontainers.org/ (as suggested today by James Ward in his talk on Kotlin Dev Day)?

Lots of inspiring ideas I'd like to try:
- Kotlin.runCatching ... onFailure ... onSuccess ...
- XX?.let {}.also {}
- suspend fun
- companion object {} 
- fun xx() = SomeClass().apply{ variable1 = "xx" ...}
- kotlin.incremental.useClasspathSnapshot=true
- Quarkus
- Kotlin native vs python or node service


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

If findById succeeds we know It's ok to continue, if not we give up by throwing an exception. Now the compiler complains that I should implement a Supplier. Here's my next attempts:

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

Other than that, it feels like some structure is evolving. As an experiment I grouped all `Category` related code in `Category.kt`. This helps me keep track better than having a file per layer, but it's still early, so we'll see.  

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
Now `findById` may throw `NoSuchElementException`, so I've added an exception handler in `ErrorHandler.kt`.  

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

Based on the kotlin/spring-boot tutorial (https://spring.io/guides/tutorials/spring-boot-kotlin/), I've added a category table and REST endpoints to add and list categories.
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
