package shop

import org.junit.Assert._
import org.scalatest.flatspec.AnyFlatSpec

class BasicCookBookTest extends AnyFlatSpec {

  implicit object InMemoryCookbookConfig extends Config {
    lazy val cookBookStore = new InMemoryCookbookStore
    lazy val categoryStore = new InMemoryCategoryStore
  }

  "Cookbook database" should "parse a cookbook database from text" in {
    val cookbook = new CookBook(InMemoryCookbookConfig)
    val R1 = cookbook.getRecipeByName("R1")
    val expectedRecipe = Recipe("R1", List(Ingredient("dranken", "d1"), Ingredient("schoonmaak", "s1")))
    assertEquals(expectedRecipe, R1)
    val R2 = cookbook.getRecipeByName("R2")
    val expectedRecipe2 = Recipe("R2", List(Ingredient("dranken", "d2"), Ingredient("schoonmaak", "s2")))
    assertEquals(expectedRecipe2, R2)
    assertEquals(7, cookbook.recipes.size)
  }

  it should "always return True for equals because there is only one cookbook" in {
    val c1 = CookBook(InMemoryCookbookConfig)
    c1.add(Recipe("Some recipe", List(Ingredient("schoonmaak", "s1"))))
    val c2 = CookBook(InMemoryCookbookConfig)
    c2.add(Recipe("some other recipe", List(Ingredient("dranken", "d1"))))
    assertTrue(c1.equals(c2))
  }

  it should "properly split tekst with multiple whitespace chareacters" in {
    val stringWithDoubleBackslashN = "part1\n\npart2"
    val splitOnDoubleBackslashN = stringWithDoubleBackslashN.split("\n\n")
    assertEquals(2, splitOnDoubleBackslashN.length)
    val stringWithDoubleBackslashNAndSomeWhitespace = "part1     \n\t  \npart2"
    val splitOnDoubleBackslahNAndSomeWhitespace = stringWithDoubleBackslashN.replaceAll("""^\s+""", "").split("\n\n")
    assertEquals(2, splitOnDoubleBackslahNAndSomeWhitespace.length)

    val cleanedUpString = InMemoryCookbookConfig.cookBookStore.cleanUpCookBookText(stringWithDoubleBackslashNAndSomeWhitespace)
    assertEquals(2, cleanedUpString.split("\n\n").length)
  }
}
