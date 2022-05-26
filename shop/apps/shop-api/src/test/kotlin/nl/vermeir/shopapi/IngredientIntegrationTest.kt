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
data class Ingredient(@Id val id: String? = null, val name: String, val categoryId: String)

class IngredientIntegrationTest {
  @BeforeEach
  fun init() {
    cleanUpDatabase()
  }

  @Test
  fun `an ingredient without id and all properties set is saved correctly and can be loaded`() {
    val ing1saved = save(ing1, path = "ingredient")
    ing1.name shouldMatch ing1saved.name
    ing1.categoryId shouldMatch ing1saved.categoryId
    ing1saved.id shouldNotBe null
  }

  @Test
  fun `a ingredient should be updated`() {
    val ing1saved = save(ing1, path = "ingredient")
    val ing1savedAgain = save(ing1saved.copy(categoryId = "cat2"), path = "ingredient")
    ing1savedAgain.categoryId shouldBe "cat2"
  }

  @Test
  fun `GET ingredient should return 404 when ingredient not found by id`() {
    val (_, response, _) = "${baseUrl}/ingredient/doesNotExist".httpGet().response()
    response.statusCode shouldBe 404
  }

  @Test
  fun `GET ingredient should return 404 when ingredient not found by name`() {
    val (_, response, _) = "${baseUrl}/ingredient".httpGet(listOf(Pair("name", "doesNotExist"))).response()
    response.statusCode shouldBe 404
  }

  @Test
  fun `a list of ingredients should be returned`() {
    val ing1saved = save(ing1, path = "ingredient")
    val ing2saved = save(ing2, path = "ingredient")
    val (_, _, result) = "${baseUrl}/ingredients".httpGet().responseString()
    val ingredients = Json.decodeFromString<List<Ingredient>>(result.get())
    ingredients shouldBe listOf(ing1saved, ing2saved)
  }

  @Test
  fun `a ingredient should be returned by findById`() {
    val ing1 = save(ing1, path = "ingredient")
    val ing1ById: Ingredient = load("ingredient/${ing1.id}", listOf())
    ing1ById shouldBe ing1
  }

  @Test
  fun `a ingredient should be returned by findByName`() {
    val ing1 = save(ing1, path = "ingredient")
    val ing1ByName: Ingredient = load("ingredient", listOf(Pair("name", ing1.name)))
    ing1ByName shouldBe ing1
  }

  companion object {
    private val ing1 = Ingredient(name = "ing1", categoryId = "cat1")
    private val ing2 = Ingredient(name = "ing2",  categoryId = "cat2")
  }
}
