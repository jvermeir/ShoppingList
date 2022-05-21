import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import nl.vermeir.shop.cleanUpDatabase
import nl.vermeir.shop.save
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.Id

@kotlinx.serialization.Serializable
data class Category(@Id val id: String? = null, val name: String, val shopOrder: Int)

class IntegrationTest {
  val cat1 = Category(name = "cat1", shopOrder = 10)

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
  fun `a category with id and all properties set is saved correctly and can be loaded`() {
    val cat1saved = save(cat1, path = "category")
    val cat1savedAgain = save(cat1saved.copy(shopOrder = 11), path = "category")
    cat1.name shouldMatch cat1savedAgain.name
    11 shouldBeExactly cat1savedAgain.shopOrder
    cat1saved.id shouldMatch cat1savedAgain.id.orEmpty()
  }
}
