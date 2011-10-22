package shop

import org.junit._
import Assert._

class CookBookTest {
  val sla1 = new Ingredient("groente", "sla")
  val tomaten1 = new Ingredient("groente", "tomaten")
  val salad1 = new Recipe("salad", List(sla1, tomaten1))
  val cookbook1 = CookBook(List(salad1))
  val sla2 = new Ingredient("groente", "sla")
  val tomaten2 = new Ingredient("groente", "tomaten")
  val salad2 = new Recipe("salad", List(sla2, tomaten2))
  val cookbook2 = CookBook(List(salad2))
  val sla3 = new Ingredient("groente", "sla")
  val tomaten3 = new Ingredient("groente", "potatoes")
  val salad3 = new Recipe("salad", List(sla3, tomaten3))
  val cookbook3 = CookBook(List(salad3))

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
}
