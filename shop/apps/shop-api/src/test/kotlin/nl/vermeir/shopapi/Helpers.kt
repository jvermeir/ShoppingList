package nl.vermeir.shopapi

import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.datetime.LocalDate
import java.util.*

const val baseUrl = "http://localhost:8080/api"

var logEnabled = false
fun log(theObject: Any) {
  if (logEnabled) {
    println(theObject)
  }
}

inline fun <reified T> save(data: T, path: String): T {
  val (_, _, result) = "${baseUrl}/${path}".httpPost().jsonBody(Json.encodeToString(data))
    .responseString()
  return Json.decodeFromString(result.get())
}

inline fun <reified T> saveJson(data: String, path: String): T {
  val (_, _, result) = "${baseUrl}/${path}".httpPost().jsonBody(data)
    .responseString()
  return Json.decodeFromString(result.get())
}

inline fun <reified T> load(path: String, params: Parameters = listOf()): T {
  val (_, _, result) = "${baseUrl}/${path}".httpGet(params).responseString()
  log(result.get())
  return Json.decodeFromString(result.get())
}

fun cleanUpDatabase() {
  val (_, _, result) = "${baseUrl}/cleanup".httpPost()
    .responseString()
  println(result)
}

fun createAnIngredient(categoryId: String): Ingredient =
  Ingredient(name = UUID.randomUUID().toString(), categoryId = categoryId)

fun createACategory(shopOrder: Int): Category = Category(name = UUID.randomUUID().toString(), shopOrder = shopOrder)
fun createARecipe(favorite: Boolean): Recipe = Recipe(name = UUID.randomUUID().toString(), favorite = favorite)
fun createAMenu(firstDay: LocalDate): Menu = Menu(firstDay = firstDay)

fun createRecipeIngredient(): RecipeIngredient {
  val c1 = save(createACategory(10), "category")
  val r1 = save(createARecipe(true), path = "recipe")
  val i1 = save(createAnIngredient(c1.id ?: throw Exception("category id should not be empty")), path = "ingredient")
  return RecipeIngredient(
    recipeId = r1.id ?: throw Exception("recipe id should not be empty"),
    ingredientId = i1.id ?: throw Exception("ingredient id should not be empty")
  )
}

fun createRecipeIngredient(recipeId: String?, ingredientId: String?): RecipeIngredient =
  RecipeIngredient(
    recipeId = recipeId ?: throw Exception("recipe id should not be empty"),
    ingredientId = ingredientId ?: throw Exception("ingredient id should not be empty")
  )
