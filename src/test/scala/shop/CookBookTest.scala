package shop

import org.junit._
import Assert._

class CookBookTest {
  Ingredient.categoryClient = new CategoryClient(LargeCategoryTestConfig)
  val sla1 = new Ingredient("groente", "sla")
  val tomaten1 = new Ingredient("groente", "tomaten")
  val salad1 = new Recipe("salad", List(sla1, tomaten1))
  val cookbook1 = CookBook(Map("salad" -> salad1))
  val sla2 = new Ingredient("groente", "sla")
  val tomaten2 = new Ingredient("groente", "tomaten")
  val salad2 = new Recipe("salad", List(sla2, tomaten2))
  val cookbook2 = CookBook(Map("salad" -> salad2))
  val sla3 = new Ingredient("groente", "sla")
  val tomaten3 = new Ingredient("groente", "potatoes")
  val salad3 = new Recipe("salad", List(sla3, tomaten3))
  val cookbook3 = CookBook(Map("salad" -> salad3))

  @Test
  def testEqualsReturnsTrueIfCookBookContainsTheSameRecipesFromDifferentObjects {
    assertEquals(cookbook1, cookbook2)
    assertTrue(cookbook1.equals(cookbook2))
  }

  @Test
  def testEqualsReturnsFalseIfCookBooksContainDifferentRecipes = {
    val x = cookbook1.equals(cookbook3)
    assertFalse(cookbook1.equals(cookbook3))
  }

  @Test
  def testSplit = {
    val stringWithDoubleBackslashN = "part1\n\npart2"
    val splitOnDoubleBackslashN = stringWithDoubleBackslashN.split("\n\n")
    assertEquals(2, splitOnDoubleBackslashN.length)
    val stringWithDoubleBackslashNAndSomeWhitespace = "part1     \n\t  \npart2"
    val splitOnDoubleBackslahNAndSomeWhitespace = stringWithDoubleBackslashN.replaceAll("""^\s+""", "").split("\n\n")
    assertEquals(2, splitOnDoubleBackslahNAndSomeWhitespace.length)
  }

  @Test
  def testSplit2 = {
    val stringWithDoubleBackslashNAndSomeWhitespace = "part1     \n\t  \n part2"
    val cleanedUpString = CookBook.cleanUpCookBookText(stringWithDoubleBackslashNAndSomeWhitespace)
    assertEquals(2, cleanedUpString.split("\n\n").length)
  }
  
  @Test
  def testReloadCookBookFromFile ={
    val cookbookClient = new CookBookClient(CookBookConfig)
    CookBookConfig.reload("data/test/cookBookForReadFromFileScenario.txt")
    val recipe1=cookbookClient.getRecipeByName("Recipe1")
    assertEquals(recipe1.ingredients, List(Ingredient("vlees:kipfilet plakjes")))
  }
}
