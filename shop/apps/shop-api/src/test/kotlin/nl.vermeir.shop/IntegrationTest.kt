import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import nl.vermeir.shop.cleanUpDatabase
import nl.vermeir.shop.load
import nl.vermeir.shop.save
import nl.vermeir.shopapi.Category

class IntegrationTest : ShouldSpec({
  println("cleanup")
  cleanUpDatabase()
  println(load<List<Category>>("categories"))

  should("a category without id and all properties set is saved correctly and can be loaded") {
    val cat1 = Category(id = null, name = "cat1", shopOrder = 10)
    val cat1saved = save(cat1, path = "category")
    cat1.name shouldMatch cat1saved.name
    cat1.shopOrder shouldBeExactly cat1saved.shopOrder
    cat1saved.id shouldNotBe null
  }

  should("a category with id and all properties set is saved correctly and can be loaded") {
    val cat1 = Category(id = null, name = "cat-id1", shopOrder = 10)
    val cat1saved = save(cat1, path = "category")
    val cat1savedAgain = save(cat1.copy(shopOrder = 11), path = "category")
    cat1.name shouldMatch cat1savedAgain.name
    11 shouldBeExactly cat1savedAgain.shopOrder
    cat1saved.id shouldMatch cat1savedAgain.id.orEmpty()
  }
})


