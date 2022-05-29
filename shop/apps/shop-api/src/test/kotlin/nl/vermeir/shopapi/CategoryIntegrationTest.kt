package nl.vermeir.shopapi

import com.github.kittinunf.fuel.httpGet
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.Id

@kotlinx.serialization.Serializable
data class Category(@Id val id: String? = null, val name: String, val shopOrder: Int)

class CategoryIntegrationTest {
  @Test
  fun `a category without id and all properties set is saved correctly and can be loaded`() {
    val cat1 = createACategory(10)
    val cat1saved = save(cat1, path = CATEGORY)
    cat1.name shouldMatch cat1saved.name
    cat1.shopOrder shouldBeExactly cat1saved.shopOrder
    cat1saved.id shouldNotBe null
  }

  @Test
  fun `a category should be updated`() {
    val cat1saved = save(createACategory(10), path = CATEGORY)
    val cat1savedAgain = save(cat1saved, path = CATEGORY)
    cat1saved shouldBe cat1savedAgain
  }

  @Test
  fun `shopOrder should be updated`() {
    val cat1saved = save(createACategory(10), path = CATEGORY)
    val cat1savedAgain = save(cat1saved.copy(shopOrder = 11), path = CATEGORY)
    cat1savedAgain.shopOrder shouldBe 11
  }

  @Test
  fun `GET category should return 404 when category not found by id`() {
    val (_, response, _) = "${baseUrl}/category/doesNotExist".httpGet().response()
    response.statusCode shouldBe 404
  }

  @Test
  fun `GET category should return 404 when category not found by name`() {
    val (_, response, _) = "${baseUrl}/category".httpGet(listOf(Pair("name", "doesNotExist"))).response()
    response.statusCode shouldBe 404
  }

  @Test
  fun `a list of categories should be returned`() {
    val cat1saved = save(createACategory(10), path = CATEGORY)
    val cat2saved = save(createACategory(11), path = CATEGORY)
    val (_, _, result) = "${baseUrl}/categories".httpGet().responseString()
    val categories = Json.decodeFromString<List<Category>>(result.get())
    categories shouldContainAll listOf(cat1saved, cat2saved)
  }

  @Test
  fun `a category should be returned by findById`() {
    val cat1Saved = save(createACategory(10), path = CATEGORY)
    val cat1ById:Category = load("category/${cat1Saved.id}",listOf())
    cat1ById shouldBe cat1Saved
  }

  @Test
  fun `a category should be returned by getCategoryByName`() {
    val cat1Saved = save(createACategory(10), path = CATEGORY)
    val cat1ByName:Category = load("category",listOf(Pair("name",cat1Saved.name)))
    cat1ByName shouldBe cat1Saved
  }

  companion object {
    private const val CATEGORY = "category"
  }
}
