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
    val recipe = shop.Recipe(recipeText)
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
      shop.Recipe(recipeText)
    }
  }

  it should "Load Recipe Fails On Invalid Ingredient" in {
    val recipeText =
      """name:Test
        |ingredientCategoryDoesNotExist:d1
        |schoonmaak:d2
      """.stripMargin
    val recipe = shop.Recipe(recipeText)
    val dummyIngredients = recipe.ingredients.filter(_.category.equals(shop.DummyCategory))
    assertEquals(dummyIngredients.size,1)
  }

  it should "Equals Returns False If Recipe name is different" in {
    val r1 = Recipe("R1", List(Ingredient.applyFromText("dranken", "d1"), Ingredient.applyFromText("schoonmaak", "s1")))
    val r2 = Recipe("R2", List(Ingredient.applyFromText("dranken", "d1"), Ingredient.applyFromText("schoonmaak", "s1")))
    assertFalse(r1.equals(r2))
  }

  it should "Equals Returns False If Recipe ingredients are different" in {
    val r1 = Recipe("R1", List(Ingredient.applyFromText("dranken", "d1"), Ingredient.applyFromText("schoonmaak", "s1")))
    val r2 = Recipe("R1", List(Ingredient.applyFromText("dranken", "d2"), Ingredient.applyFromText("schoonmaak", "s2")))
    assertFalse(r1.equals(r2))
  }

  it should "Equals Returns True If Recipes are the same" in {
    val r1 = Recipe("R1", List(Ingredient.applyFromText("dranken", "d1"), Ingredient.applyFromText("schoonmaak", "s1")))
    val r2 = Recipe("R1", List(Ingredient.applyFromText("dranken", "d1"), Ingredient.applyFromText("schoonmaak", "s1")))
    assertTrue(r1.equals(r2))
  }

}
