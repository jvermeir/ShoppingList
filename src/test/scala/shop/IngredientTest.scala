package shop

import org.junit.Assert._
import org.scalatest.flatspec.AnyFlatSpec

class IngredientTest extends AnyFlatSpec {

  CategoryService.config("data/test/categoryDatabase.csv")

  "Ingredients" should "be sorted by according to categories" in {
    val bier = IngredientO.apply("vis", "v")
    val zeep = IngredientO.apply("soep", "r")
    assertTrue(bier < zeep)
    assertFalse(zeep < bier)
  }

  it should "Null Category Is Less Than Any Other Category" in {
    val bier = IngredientO.apply("dranken", "bier")
    assert(bier > null)
  }

  it should "Create an ingredient using the companion object" in {
    val bier = IngredientO.readFromLine("vis:bier")
    assertNotNull(bier)
    val dummy = IngredientO.readFromLine("categoryDoesNotExist:sla")
    assertEquals(IngredientO.apply("dummy", "sla"), dummy)
  }

  it should "Equals Returns False If Ingredient name is different" in {
    assertFalse(IngredientO.apply("vis", "d1").equals(IngredientO("vis", "s1")))
  }

  it should "Equals Returns False If Ingredient category is different" in {
    assertFalse(IngredientO.apply("soep", "d1").equals(IngredientO("vis", "d1")))
  }

  it should "Equals Returns True If Ingredients have the same category and name" in {
    assertTrue(IngredientO.apply("vis", "d1").equals(IngredientO.apply("vis", "d1")))
  }

}
