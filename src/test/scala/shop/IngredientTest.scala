package shop

import org.junit.Assert._
import org.scalatest.flatspec.AnyFlatSpec

class IngredientTest extends AnyFlatSpec {

  CategoryService.config("data/test/categoryDatabase.csv")

  "Ingredients" should "be sorted by according to categories" in {
    val bier = Ingredient.applyFromText("vis", "v")
    val zeep = Ingredient.applyFromText("soep", "r")
    assertTrue(bier < zeep)
    assertFalse(zeep < bier)
  }

  it should "Null Category Is Less Than Any Other Category" in {
    val bier = Ingredient.applyFromText("dranken", "bier")
    assert(bier > null)
  }

  it should "Create an ingredient using the companion object" in {
    val bier = Ingredient.readFromLine("vis:bier")
    assertNotNull(bier)
    val dummy = Ingredient.readFromLine("categoryDoesNotExist:sla")
    assertEquals(Ingredient.applyFromText("dummy", "sla"), dummy)
  }

  it should "Equals Returns False If Ingredient name is different" in {
    assertFalse(Ingredient.applyFromText("vis", "d1").equals(Ingredient.applyFromText("vis", "s1")))
  }

  it should "Equals Returns False If Ingredient category is different" in {
    assertFalse(Ingredient.applyFromText("soep", "d1").equals(Ingredient.applyFromText("vis", "d1")))
  }

  it should "Equals Returns True If Ingredients have the same category and name" in {
    assertTrue(Ingredient.applyFromText("vis", "d1").equals(Ingredient.applyFromText("vis", "d1")))
  }

}
