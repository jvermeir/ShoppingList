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
data class Ingredient(@Id val id: String? = null, val name: String, val categoryId: String)

class IngredientIntegrationTest {
  @Test
  fun `an ingredient without id and all properties set is saved correctly and can be loaded`() {
    val ing1 = createAnIngredient("cat1")
    val ing1saved = save(ing1, path = INGREDIENT)
    ing1.name shouldMatch ing1saved.name
    ing1.categoryId shouldMatch ing1saved.categoryId
    ing1saved.id shouldNotBe null
  }

  @Test
  fun `a ingredient should be updated`() {
    val ing1saved = save(createAnIngredient("cat1"), path = INGREDIENT)
    val ing1savedAgain = save(ing1saved.copy(categoryId = "cat2"), path = INGREDIENT)
    ing1savedAgain.categoryId shouldBe "cat2"
  }

  @Test
  fun `GET ingredient should return 404 when ingredient not found by id`() {
    val (_, response, _) = "${baseUrl}/${INGREDIENT}/doesNotExist".httpGet().response()
    response.statusCode shouldBe 404
  }

  @Test
  fun `GET ingredient should return 404 when ingredient not found by name`() {
    val (_, response, _) = "${baseUrl}/${INGREDIENT}".httpGet(listOf(Pair("name", "doesNotExist"))).response()
    response.statusCode shouldBe 404
  }

  @Test
  fun `a list of ingredients should be returned`() {
    val ing1saved = save(createAnIngredient("cat1"), path = INGREDIENT)
    val ing2saved = save(createAnIngredient("cat2"), path = INGREDIENT)
    val (_, _, result) = "${baseUrl}/ingredients".httpGet().responseString()
    val ingredients = Json.decodeFromString<List<Ingredient>>(result.get())
    ingredients shouldContainAll listOf(ing1saved, ing2saved)
  }

  @Test
  fun `a ingredient should be returned by findById`() {
    val ing1Saved = save(createAnIngredient("cat1"), path = INGREDIENT)
    val ing1ById: Ingredient = load("ingredient/${ing1Saved.id}", listOf())
    ing1ById shouldBe ing1Saved
  }

  @Test
  fun `a ingredient should be returned by findByName`() {
    val ing1 = createAnIngredient("cat1")
    val ing1Saved = save(ing1, path = INGREDIENT)
    val ing1ByName: Ingredient = load(INGREDIENT, listOf(Pair("name", ing1.name)))
    ing1ByName shouldBe ing1Saved
  }

  companion object {
    private const val INGREDIENT = "ingredient"
  }
}
