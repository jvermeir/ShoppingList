package shop

import org.junit.Assert._
import org.scalatest._


class MenuTest extends Spec {

  implicit object InMemoryCategoryConfig extends Config {
    lazy val cookBookStore = new InMemoryCookbookStore
    lazy val categoryStore = new InMemoryCategoryStore
  }

  val cookBook = new CookBook

  def `test Menu Can Be Loaded From Text` {
    val menu = Menu.apply( """Zaterdag valt op:05112011
                             |zaterdag:R1
                             |zondag:R2
                             | """.stripMargin, cookBook)
    assertEquals(2, menu.listOfRecipes.length)
    val recipeDay1 = menu.listOfRecipes(0)
    assertEquals("R1", recipeDay1._2)
    val recipeDay2 = menu.listOfRecipes(1)
    assertEquals("R2", recipeDay2._2)
  }

  def `A menu can be read from a file` {
    val menu = Menu.readFromFile("data/test/menuForReadFromFileScenario.txt", cookBook)
    assertEquals(2, menu.listOfRecipes.length)
    assertEquals(menu.recipes(0)._2.name, "R1")
    assertEquals(menu.recipes(1)._2.name, "R2")
  }
}
