package nl.vermeir.shopapi

import com.github.kittinunf.fuel.httpGet
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.Id

@kotlinx.serialization.Serializable
data class Category(@Id val id: String? = null, val name: String, val shopOrder: Int)

class CategoryIntegrationTest {
  @BeforeEach
  fun init() {
    cleanUpDatabase()
  }

  @Test
  fun `a category without id and all properties set is saved correctly and can be loaded`() {
    val cat1saved = save(cat1, path = "category")
    cat1.name shouldMatch cat1saved.name
    cat1.shopOrder shouldBeExactly cat1saved.shopOrder
    cat1saved.id shouldNotBe null
  }

  @Test
  fun `a category should be updated`() {
    val cat1saved = save(cat1, path = "category")
    val cat1savedAgain = save(cat1saved, path = "category")
    cat1saved shouldBe cat1savedAgain
  }

  @Test
  fun `shopOrder should be updated`() {
    val cat1saved = save(cat1, path = "category")
    val cat1savedAgain = save(cat1saved.copy(shopOrder = 11), path = "category")
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
    val cat1saved = save(cat1, path = "category")
    val cat2saved = save(cat2, path = "category")
    val (_, _, result) = "${baseUrl}/categories".httpGet().responseString()
    val categories = Json.decodeFromString<List<Category>>(result.get())
    categories shouldBe listOf(cat1saved, cat2saved)
  }

  @Test
  fun `a category should be returned by findById`() {
    val cat1 = save(cat1, path = "category")
    val cat1ById:Category = load("category/${cat1.id}",listOf())
    cat1ById shouldBe cat1
  }

  @Test
  fun `a category should be returned by getCategoryByName`() {
    val cat1 = save(cat1, path = "category")
    val cat1ByName:Category = load("category",listOf(Pair("name",cat1.name)))
    cat1ByName shouldBe cat1
  }

  companion object {
    private val cat1 = Category(name = "cat1", shopOrder = 10)
    private val cat2 = Category(name = "cat2", shopOrder = 20)
  }
}
