package shop

import org.junit.Assert._
import org.scalatest.flatspec.AnyFlatSpec

class IngredientTest extends AnyFlatSpec {

  implicit object InMemoryCategoryConfig extends Config {
    lazy val cookBookStore = new InMemoryCookbookStore
    lazy val categoryStore = new InMemoryCategoryStore
  }

  "Ingredients" should "be sorted by according to categories" in {
    val bier = new Ingredient("dranken", "bier ")
    val zeep = new Ingredient("schoonmaak", "zeep")
    assertTrue(bier < zeep)
    assertFalse(zeep < bier)
  }

  it should "Null Category Is Less Than Any Other Category" in {
    val bier = new Ingredient("dranken", "bier")
    assert(bier > null)
  }

  it should "Create an ingredient using the companion object" in {
    val bier = Ingredient.readFromLine("dranken:bier")
    assertNotNull(bier)
    intercept[PanicException] {
      Ingredient.readFromLine("categoryDoesNotExist:sla")
    }
  }

  it should "Equals Returns False If Ingredient name is different" in {
    assertFalse(Ingredient("dranken", "d1").equals(Ingredient("dranken", "s1")))
  }

  it should "Equals Returns False If Ingredient category is different" in {
    assertFalse(Ingredient("dranken", "d1").equals(Ingredient("schoonmaak", "d1")))
  }

  it should "Equals Returns True If Ingredients have the same category and name" in {
    assertTrue(Ingredient("dranken", "d1").equals(Ingredient("dranken", "d1")))
  }

}
