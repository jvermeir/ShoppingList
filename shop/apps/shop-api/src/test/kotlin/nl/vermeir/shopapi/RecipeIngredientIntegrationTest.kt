package nl.vermeir.shopapi

import com.github.kittinunf.fuel.httpGet
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.Id

@kotlinx.serialization.Serializable
data class RecipeIngredient(@Id val id: String? = null, val recipeId: String, val ingredientId: String)

class RecipeIngredientIntegrationTest {
  @Test
  fun `a RecipeIngredient without id and all properties set is saved correctly and can be loaded`() {
    val ri1 = createRecipeIngredient()
    val r1saved = save(ri1, path = RECIPE_INGREDIENT)
    ri1.recipeId shouldMatch r1saved.recipeId
    ri1.ingredientId shouldBe r1saved.ingredientId
    r1saved.id shouldNotBe null
  }

  @Test
  fun `a RecipeIngredient should be updated`() {
    val ri1saved = save(createRecipeIngredient(), path = RECIPE_INGREDIENT)
    val ing2 = save(createAnIngredient("cat1"), path = "ingredient")
    val ri1savedAgain = save(ri1saved.copy(ingredientId = ing2.id ?: throw Exception("ingredient id should not be empty")), path = RECIPE_INGREDIENT)
    ri1savedAgain.ingredientId shouldBe ing2.id
  }

  @Test
  fun `GET RecipeIngredient should return 404 when RecipeIngredient not found by id`() {
    val (_, response, _) = "${baseUrl}/${RECIPE_INGREDIENT}/doesNotExist".httpGet().response()
    response.statusCode shouldBe 404
  }

  @Test
  fun `a list of RecipeIngredients should be returned`() {
    val ri2 = createRecipeIngredient()
    val i1 = createAnIngredient("cat1")
    val i1saved = save(i1, path = "ingredient")
    val ri3 = createRecipeIngredient(ri2.recipeId, i1saved.id)
    val ri2saved = save(ri2, path = RECIPE_INGREDIENT)
    val ri3saved = save(ri3, path = RECIPE_INGREDIENT)
    val (_, _, result) = "${baseUrl}/recipe-ingredients".httpGet(listOf(Pair("recipeId", ri2.recipeId))).responseString()
    val recipeIngredients = Json.decodeFromString<List<RecipeIngredient>>(result.get())
    recipeIngredients shouldContainAll listOf(ri2saved, ri3saved)
  }

  @Test
  fun `a RecipeIngredient should be returned by findById`() {
    val ri1saved = save(createRecipeIngredient(), path = RECIPE_INGREDIENT)
    val ri1ById: RecipeIngredient = load("${RECIPE_INGREDIENT}/${ri1saved.id}", listOf())
    ri1ById shouldBe ri1saved
  }

  companion object {
    private const val RECIPE_INGREDIENT = "recipe-ingredient"
  }
}
