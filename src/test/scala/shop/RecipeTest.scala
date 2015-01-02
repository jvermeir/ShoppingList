package shop

import org.junit.Assert._
import org.scalatest.Spec

class RecipeTest extends Spec {

  implicit object InMemoryCategoryConfig extends Config {
    lazy val cookBookStore = new InMemoryCookbookStore
    lazy val categoryStore = new InMemoryCategoryStore
  }

  def `Recipe Loaded From String` {
    val recipeText =
      """name:Test
        |dranken:d1
        |schoonmaak:d2
      """.stripMargin
    val recipe = Recipe(recipeText)
    assertEquals("Test", recipe.name)
    assertEquals(2, recipe.ingredients.size)
  }

  def `Load Recipe Fails If Name Not Specified` {
    val recipeText =
      """nameColonMissingTest
        |dranken:d1
        |schoonmaak:d2
      """.stripMargin
    intercept[java.lang.ArrayIndexOutOfBoundsException] {
      val recipe = Recipe(recipeText)
    }
  }

  def `Load Recipe Fails On Invalid Ingredient` {
    val recipeText =
      """name:Test
        |ingredientCategoryDoesNotExist:d1
        |schoonmaak:d2
      """.stripMargin
    intercept[PanicException] {
      val recipe = Recipe(recipeText)
    }
  }

  def `Equals Returns False If Recipe name is different` = {
    val r1 = Recipe("R1", List(Ingredient("dranken", "d1"), Ingredient("schoonmaak", "s1")))
    val r2 = Recipe("R2", List(Ingredient("dranken", "d1"), Ingredient("schoonmaak", "s1")))
    assertFalse(r1.equals(r2))
  }

  def `Equals Returns False If Recipe ingredients are different` = {
    val r1 = Recipe("R1", List(Ingredient("dranken", "d1"), Ingredient("schoonmaak", "s1")))
    val r2 = Recipe("R1", List(Ingredient("dranken", "d2"), Ingredient("schoonmaak", "s2")))
    assertFalse(r1.equals(r2))
  }

  def `Equals Returns True If Recipes are the same` = {
    val r1 = Recipe("R1", List(Ingredient("dranken", "d1"), Ingredient("schoonmaak", "s1")))
    val r2 = Recipe("R1", List(Ingredient("dranken", "d1"), Ingredient("schoonmaak", "s1")))
    assertTrue(r1.equals(r2))
  }

}
