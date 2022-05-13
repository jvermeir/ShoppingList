import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.vermeir.shopapi.*

const val baseUrl = "http://localhost:8080/api"
var logEnabled = false

fun main(args: Array<String>) {
  if (args.contains("debug")) logEnabled = true
//  cleanUpDatabase()
  loadTestData()
  readTestData()
}

fun log(theObject: Any) {
  if (logEnabled) {
    println(theObject)
  }
}

var recipe1: Recipe = Recipe("", "dummy", false)

fun readTestData() {
//  println(load<List<Category>>("categories"))
//  println(load<List<Ingredient>>("ingredients"))
//  println(load<List<Recipe>>("recipes"))
//  println(load<List<RecipeIngredient>>("recipe-ingredients"))
//  println(load<RecipeDetails>("recipe-details", listOf(Pair("id", recipe1.id))))
}

fun loadTestData() {
  val cat1 = save(Category(id = null, name = "cat1", shopOrder = 10), path = "category")
  println(cat1)
//  val cat2 = save(Category(id = null, name = "cat2", shopOrder = 20), path = "category")
//  val ing1 = save(Ingredient(id = null, name = "cat1ing1", cat1.id.orEmpty()), path = "ingredient")
//  val ing2 = save(Ingredient(id = null, name = "cat1ing2", cat1.id.orEmpty()), path = "ingredient")
//  val ing3 = save(Ingredient(id = null, name = "cat2ing1", cat2.id.orEmpty()), path = "ingredient")
//  val ing4 = save(Ingredient(id = null, name = "cat2ing2", cat2.id.orEmpty()), path = "ingredient")
//  recipe1 = save(Recipe(id = null, name = "recipe1", false), path = "recipe")
//  val favoriteRecipe = save(Recipe(id = null, name = "favorite recipe", true), path = "recipe")
//  save(RecipeIngredient(id = null, recipe1.id.orEmpty(), ing1.id.orEmpty()), path = "recipe-ingredient")
//  save(RecipeIngredient(id = null, recipe1.id.orEmpty(), ing2.id.orEmpty()), path = "recipe-ingredient")
//  save(RecipeIngredient(id = null, favoriteRecipe.id.orEmpty(), ing3.id.orEmpty()), path = "recipe-ingredient")
//  save(RecipeIngredient(id = null, favoriteRecipe.id.orEmpty(), ing4.id.orEmpty()), path = "recipe-ingredient")
//
//  val newRecipe = """
//  {
//  "recipe": {
//    "name": "recipe1",
//    "favorite": false
//  },
//  "ingredients": [
//    {
//      "ingredientName": "cat1ing1",
//      "categoryId": "${cat1.id}",
//      "categoryName": "cat1"
//    },
//    {
//      "ingredientName": "cat1ing2",
//      "categoryId": "${cat1.id}",
//      "categoryName": "cat1"
//    }
//  ]
//}
//""".trimIndent()
//
//  val rd: RecipeDetails= RecipeDetails(Recipe(id=null, name="r1", true), listOf(IngredientDetails(recipeIngredientId = null, ingredientId = null, "newIng1","${cat1.id}", categoryName = "${cat1.name}" )) )
//  val encoded = Json.encodeToString(rd)
//  val (_, _, result) = "${baseUrl}/recipe-details".httpPost().jsonBody(Json.encodeToString(encoded))
//    .responseString()
}

inline fun <reified T> save(data: T, path: String): T {
  val (_, _, result) = "${baseUrl}/${path}".httpPost().jsonBody(Json.encodeToString(data))
    .responseString()
  return Json.decodeFromString<T>(result.get())
}

inline fun <reified T> load(path: String, params: Parameters = listOf()): T {
  val (_, _, result) = "${baseUrl}/${path}".httpGet(params).responseString()
  log(result.get())
  return Json.decodeFromString<T>(result.get())
}

fun cleanUpDatabase() {
  val (_, _, result) = "${baseUrl}/cleanup".httpPost()
    .responseString()
  println(result)
}
