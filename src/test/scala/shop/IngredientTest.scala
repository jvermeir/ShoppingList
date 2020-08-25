package shop

import org.junit.Assert._
import org.scalatest.flatspec.AnyFlatSpec

class IngredientTest extends AnyFlatSpec {

  CategoryService.config("data/test/categoryDatabase.csv")

  "Ingredients" should "be sorted by according to categories" in {
    val bier = Ingredient.apply("vis", "v")
    val zeep = Ingredient.apply("soep", "r")
    assertTrue(bier < zeep)
    assertFalse(zeep < bier)
  }

  it should "Null Category Is Less Than Any Other Category" in {
    val bier = Ingredient.apply("dranken", "bier")
    assert(bier > null)
  }

  it should "Create an ingredient using the companion object" in {
    val bier = Ingredient.readFromLine("vis:bier")
    assertNotNull(bier)
    val dummy = Ingredient.readFromLine("categoryDoesNotExist:sla")
    assertEquals(Ingredient.apply("dummy", "sla"), dummy)
  }

  it should "Equals Returns False If Ingredient name is different" in {
    assertFalse(Ingredient.apply("vis", "d1").equals(Ingredient("vis", "s1")))
  }

  it should "Equals Returns False If Ingredient category is different" in {
    assertFalse(Ingredient.apply("soep", "d1").equals(Ingredient("vis", "d1")))
  }

  it should "Equals Returns True If Ingredients have the same category and name" in {
    assertTrue(Ingredient.apply("vis", "d1").equals(Ingredient.apply("vis", "d1")))
  }

}
