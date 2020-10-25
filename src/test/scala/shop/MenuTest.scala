package shop

import org.junit.Assert._
import org.scalatest.flatspec.AnyFlatSpec
import shop.Dates.dateToIsoString
import spray.json.enrichAny

class MenuTest extends AnyFlatSpec {

  CategoryService.config("data/test/categoryDatabase.csv")
  CookBookService.config("data/test/cookBookForReadFromFileScenario.txt")

  "A menu" should "be loaded from text" in {
    val menu = Menu.apply(
      """Zaterdag valt op:05112011
        |zaterdag:dish1
        |zondag:dish2
        | """.stripMargin)
    assertEquals(2, menu.menuItems.length)
    val recipeDay1 = menu.menuItems.head
    assertEquals("dish1", recipeDay1.recipe)
    val recipeDay2 = menu.menuItems(1)
    assertEquals("dish2", recipeDay2.recipe)
  }

  it should "A menu can be read from a file" in {
    val menu = Menu.readFromFile("data/test/menuForReadFromFileScenario.txt")
    assertEquals(2, menu.menuItems.length)
    val expectedRecipes = List("dish1", "dish2")
    assertEquals(expectedRecipes, menu.recipes.map(recipe => recipe._2.name))
  }

  it should "Date for R1 is 01022014 and date for R2 is 02022014" in {
    val menu = Menu.readFromFile("data/test/menuFiles/0101.txt")
    assertEquals("2014-02-01T00:00:00.000Z", dateToIsoString(menu.menuItems.head.date))
    assertEquals("2014-02-02T00:00:00.000Z", dateToIsoString(menu.menuItems(1).date))
  }

  it should "Week may start on a Wednesday" in {
    val menu = Menu.readFromFile("data/test/menuFiles/somemenu.txt")
    assertEquals("2014-02-08T00:00:00.000Z", dateToIsoString(menu.menuItems(3).date))
    assertEquals("2014-02-09T00:00:00.000Z", dateToIsoString(menu.menuItems(4).date))
  }

  it should "Load a menu from a json file" in {
    val menu = Menu.readFromFile("data/test/menuFiles/somemenu.txt")
    val menuAsJson = menu.toJson
    val res = Menu.fromJson(menuAsJson.toString)
    assertEquals(menu, res)
  }
}
