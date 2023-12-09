package integration

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import nl.vermeir.shopapi.*
import java.time.LocalDate

const val BASE_URL = "http://localhost:8080/api"

suspend inline fun <reified T> addIt(it: T, client: HttpClient): T {
  val typeName = T::class.simpleName?.lowercase()

  val response: HttpResponse = client.post("$BASE_URL/$typeName") {
    contentType(ContentType.Application.Json)
    setBody(it)
  }
  if (response.status != HttpStatusCode.Created) {
    throw Exception("Failed to add $it of type ${T::class}")
  }
  return response.body<T>()
}

suspend fun cleanup(client: HttpClient) {
  val response = client.post("$BASE_URL/cleanup")
  if (response.status != HttpStatusCode.OK) {
    throw Exception("Failed to cleanup")
  }
}

suspend fun main() {
  val client = HttpClient(CIO) {
    install(ContentNegotiation) {
      json()
    }
  }

  cleanup(client)

  val category1 = addIt(Category(name = "category1", shopOrder = 1), client)
  val category2 = addIt(Category(name = "category2", shopOrder = 2), client)
  val ingredient1 = addIt(Ingredient(name = "ingredient1", categoryId = category1.id!!), client)
  val ingredient2 = addIt(Ingredient(name = "ingredient2", categoryId = category2.id!!), client)
  val recipe1 = addIt(Recipe(name = "recipe1", favorite = true), client)
  val recipeIngredient1 = addIt(
    RecipeIngredient(recipeId = recipe1.id!!, ingredientId = ingredient1.id!!, amount = 1.0f, unit = "kg"),
    client
  )
  val recipeIngredient2 = addIt(
    RecipeIngredient(recipeId = recipe1.id!!, ingredientId = ingredient2.id!!, amount = 1.0f, unit = "kg"),
    client
  )
  val recipe2 = addIt(Recipe(name = "recipe2", favorite = true), client)
  val recipeIngredient3 = addIt(
    RecipeIngredient(recipeId = recipe2.id!!, ingredientId = ingredient1.id!!, amount = 1.0f, unit = "kg"),
    client
  )
  val menu1 = addIt(Menu(firstDay = LocalDate.now()), client)
  val menuItem1 = addIt(MenuItem(menuId = menu1.id!!, recipeId = recipe1.id!!, theDay = LocalDate.now()), client)
  val menuItem2 =
    addIt(MenuItem(menuId = menu1.id!!, recipeId = recipe2.id!!, theDay = LocalDate.now().plusDays(1)), client)

  val response: HttpResponse = client.get("$BASE_URL/menu/details/firstDay/${LocalDate.now()}")
  println(response.body<String>())
  client.close()
}
