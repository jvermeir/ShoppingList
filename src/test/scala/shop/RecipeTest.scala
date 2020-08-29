package shop

import org.junit.Assert._
import org.scalatest.flatspec.AnyFlatSpec

class RecipeTest extends AnyFlatSpec {

  CategoryService.config("data/test/categoryDatabase.csv")
  CookBookService.config("data/test/cookBookForReadFromFileScenario.txt")

  "Recipe" should "be loaded from a string" in {
    val recipeText =
      """name:Test
        |dranken:d1
        |schoonmaak:d2
      """.stripMargin
    val recipe = RecipeO(recipeText)
    assertEquals("Test", recipe.name)
    assertEquals(2, recipe.ingredients.size)
  }

  it should "Load Recipe Fails If Name Not Specified" in {
    val recipeText =
      """nameColonMissingTest
        |dranken:d1
        |schoonmaak:d2
      """.stripMargin
    intercept[java.lang.ArrayIndexOutOfBoundsException] {
      val recipe = RecipeO(recipeText)
    }
  }

  it should "Load Recipe Fails On Invalid Ingredient" in {
    val recipeText =
      """name:Test
        |ingredientCategoryDoesNotExist:d1
        |schoonmaak:d2
      """.stripMargin
      val recipe = RecipeO(recipeText)
  }

  it should "Equals Returns False If Recipe name is different" in {
    val r1 = Recipe("R1", List(IngredientO("dranken", "d1"), IngredientO("schoonmaak", "s1")))
    val r2 = Recipe("R2", List(IngredientO("dranken", "d1"), IngredientO("schoonmaak", "s1")))
    assertFalse(r1.equals(r2))
  }

  it should "Equals Returns False If Recipe ingredients are different" in {
    val r1 = Recipe("R1", List(IngredientO("dranken", "d1"), IngredientO("schoonmaak", "s1")))
    val r2 = Recipe("R1", List(IngredientO("dranken", "d2"), IngredientO("schoonmaak", "s2")))
    assertFalse(r1.equals(r2))
  }

  it should "Equals Returns True If Recipes are the same" in {
    val r1 = Recipe("R1", List(IngredientO("dranken", "d1"), IngredientO("schoonmaak", "s1")))
    val r2 = Recipe("R1", List(IngredientO("dranken", "d1"), IngredientO("schoonmaak", "s1")))
    assertTrue(r1.equals(r2))
  }

}
