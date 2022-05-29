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
data class Recipe(@Id val id: String? = null, val name: String, val favorite: Boolean)

class RecipeIntegrationTest {
  @Test
  fun `a recipe without id and all properties set is saved correctly and can be loaded`() {
    val r1 = createARecipe(true)
    val r1saved = save(r1, path = RECIPE)
    r1.name shouldMatch r1saved.name
    r1.favorite shouldBe r1saved.favorite
    r1saved.id shouldNotBe null
  }

  @Test
  fun `a recipe should be updated`() {
    val r1saved = save(createARecipe(true), path = RECIPE)
    val r1savedAgain = save(r1saved.copy(favorite = false), path = RECIPE)
    r1savedAgain.favorite shouldBe false
  }

  @Test
  fun `GET recipe should return 404 when recipe not found by id`() {
    val (_, response, _) = "${baseUrl}/${RECIPE}/doesNotExist".httpGet().response()
    response.statusCode shouldBe 404
  }

  @Test
  fun `GET recipe should return 404 when recipe not found by name`() {
    val (_, response, _) = "${baseUrl}/${RECIPE}".httpGet(listOf(Pair("name", "doesNotExist"))).response()
    response.statusCode shouldBe 404
  }

  @Test
  fun `a list of recipes should be returned`() {
    val r1saved = save(createARecipe(true), path = RECIPE)
    val r2saved = save(createARecipe(false), path = RECIPE)
    val (_, _, result) = "${baseUrl}/recipes".httpGet().responseString()
    val recipes = Json.decodeFromString<List<Recipe>>(result.get())
    recipes shouldContainAll listOf(r1saved, r2saved)
  }

  @Test
  fun `a recipe should be returned by findById`() {
    val r1Saved = save(createARecipe(true), path = RECIPE)
    val r1ById: Recipe = load("${RECIPE}/${r1Saved.id}", listOf())
    r1ById shouldBe r1Saved
  }

  @Test
  fun `a recipe should be returned by findByName`() {
    val r1Saved = save(createARecipe(true), path = RECIPE)
    val r1ByName: Recipe = load(RECIPE, listOf(Pair("name", r1Saved.name)))
    r1ByName shouldBe r1Saved
  }

  companion object {
    private const val RECIPE = "recipe"
  }
}
