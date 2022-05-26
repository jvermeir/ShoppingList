package nl.vermeir.shopapi

import com.github.kittinunf.fuel.httpGet
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.Id

@kotlinx.serialization.Serializable
data class RecipeIngredient(@Id val id: String? = null, val recipeId: String, val ingredientId: String)

class RecipeIngredientIntegrationTest {

  @BeforeEach
  fun init() {
    cleanUpDatabase()
  }

  @Test
  fun `a RecipeIngredient without id and all properties set is saved correctly and can be loaded`() {
    val r1saved = save(ri1, path = "recipe-ingredient")
    ri1.recipeId shouldMatch r1saved.recipeId
    ri1.ingredientId shouldBe r1saved.ingredientId
    r1saved.id shouldNotBe null
  }

  @Test
  fun `a RecipeIngredient should be updated`() {
    val r1saved = save(ri1, path = "recipe-ingredient")
    val r1savedAgain = save(r1saved.copy(ingredientId = ing2.id ?: throw Exception("id should not be empty")), path = "recipe-ingredient")
    r1savedAgain.ingredientId shouldBe ing2.id
  }

  @Test
  fun `GET RecipeIngredient should return 404 when RecipeIngredient not found by id`() {
    val (_, response, _) = "${baseUrl}/recipe-ingredient/doesNotExist".httpGet().response()
    response.statusCode shouldBe 404
  }

  @Test
  fun `a list of RecipeIngredients should be returned`() {
    val ri2saved = save(ri2, path = "recipe-ingredient")
    val ri3saved = save(ri3, path = "recipe-ingredient")
    val (_, _, result) = "${baseUrl}/recipe-ingredients".httpGet(listOf(Pair("recipeId", r2.id))).responseString()
    val recipeIngredients = Json.decodeFromString<List<RecipeIngredient>>(result.get())
    recipeIngredients shouldBe listOf(ri2saved, ri3saved)
  }

  @Test
  fun `a RecipeIngredient should be returned by findById`() {
    val r1 = save(ri1, path = "recipe-ingredient")
    val r1ById: RecipeIngredient = load("recipe-ingredient/${r1.id}", listOf())
    r1ById shouldBe r1
  }

  companion object {
    private val r1 = save(Recipe(name = "r1", favorite = true), path = "recipe")
    private val r2 = save(Recipe(name = "r2", favorite = false), path = "recipe")
    private val ing1 = save(Ingredient(name = "ing1", categoryId = "cat1"), path = "ingredient")
    private val ing2 = save(Ingredient(name = "ing2",  categoryId = "cat2"), path = "ingredient")
    private val ing3 = save(Ingredient(name = "ing3",  categoryId = "cat2"), path = "ingredient")

    private val ri1 = RecipeIngredient(recipeId = r1.id ?: throw Exception("id should not be empty"), ingredientId = ing1.id ?: throw Exception("id should not be empty"))
    private val ri2 = RecipeIngredient(recipeId = r2.id ?: throw Exception("id should not be empty"), ingredientId = ing2.id ?: throw Exception("id should not be empty"))
    private val ri3 = RecipeIngredient(recipeId = r2.id ?: throw Exception("id should not be empty"), ingredientId = ing3.id ?: throw Exception("id should not be empty"))
  }
}
