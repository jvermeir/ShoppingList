package shop

import org.junit.Assert._
import org.scalatest._

class IngredientTest extends Spec {

  implicit object InMemoryCategoryConfig extends Config {
    lazy val cookBookStore = new InMemoryCookbookStore
    lazy val categoryStore = new InMemoryCategoryStore
  }

  def `sort order of ingredients is according to categories`  {
    val bier = new Ingredient("dranken", "bier ")
    val zeep = new Ingredient("schoonmaak", "zeep")
    assertTrue(bier < zeep)
    assertFalse(zeep < bier)
  }

  def `Null Category Is Less Than Any Other Category` {
    val bier = new Ingredient("dranken", "bier")
    assert(bier > null)
  }

  def `Create an ingredient using the companion object`: Unit = {
    val bier = Ingredient.readFromLine("dranken:bier")
    assertNotNull(bier)
    intercept[PanicException] {
      Ingredient.readFromLine("categoryDoesNotExist:sla")
    }
  }

  def `Equals Returns False If Ingredient name is different` = {
    assertFalse(Ingredient("dranken", "d1").equals(Ingredient("dranken", "s1")))
  }

  def `Equals Returns False If Ingredient category is different` = {
    assertFalse(Ingredient("dranken", "d1").equals(Ingredient("schoonmaak", "d1")))
  }

  def `Equals Returns True If Ingredients have the same category and name` = {
    assertTrue(Ingredient("dranken", "d1").equals(Ingredient("dranken", "d1")))
  }

}


