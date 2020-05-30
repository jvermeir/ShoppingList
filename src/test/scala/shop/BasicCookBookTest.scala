package shop

import org.junit._
import Assert._
import org.scalatest.Spec

class BasicCookBookTest extends Spec {
  implicit object InMemoryCookbookConfig extends Config {
    lazy val cookBookStore = new InMemoryCookbookStore
    lazy val categoryStore = new InMemoryCategoryStore
  }

  def `Cookbook Can Be Parsed From Text` {
    val cookbook = new CookBook
    val R1 = cookbook.getRecipeByName("R1")
    val expectedRecipe = Recipe("R1", List(Ingredient("dranken", "d1"), Ingredient("schoonmaak", "s1")))
    assertEquals(expectedRecipe, R1)
    val R2 = cookbook.getRecipeByName("R2")
    val expectedRecipe2 = Recipe("R2", List(Ingredient("dranken", "d2"), Ingredient("schoonmaak", "s2")))
    assertEquals(expectedRecipe2, R2)
    assertEquals(7, cookbook.recipes.size)
  }

  def `Equals always Returns True because there is only one cookbook` {
    val c1 = new CookBook
    c1.add(Recipe("Some recipe", List(Ingredient("schoonmaak", "s1"))))
    val c2 = new CookBook
    c2.add(Recipe("some other recipe", List(Ingredient("dranken", "d1"))))
    assertTrue(c1.equals(c2))
  }

  def `test Split` = {
    val stringWithDoubleBackslashN = "part1\n\npart2"
    val splitOnDoubleBackslashN = stringWithDoubleBackslashN.split("\n\n")
    assertEquals(2, splitOnDoubleBackslashN.length)
    val stringWithDoubleBackslashNAndSomeWhitespace = "part1     \n\t  \npart2"
    val splitOnDoubleBackslahNAndSomeWhitespace = stringWithDoubleBackslashN.replaceAll("""^\s+""", "").split("\n\n")
    assertEquals(2, splitOnDoubleBackslahNAndSomeWhitespace.length)
  }

  @Test
  def `test Split2` = {
    val stringWithDoubleBackslashNAndSomeWhitespace = "part1     \n\t  \n part2"
    val cleanedUpString = InMemoryCookbookConfig.cookBookStore.cleanUpCookBookText(stringWithDoubleBackslashNAndSomeWhitespace)
    assertEquals(2, cleanedUpString.split("\n\n").length)
  }
}

