package shop

import org.joda.time.format.DateTimeFormat
import org.junit.Assert._
import org.scalatest.flatspec.AnyFlatSpec

class MenuTest extends AnyFlatSpec {

  CategoryService.config("data/test/categoryDatabase.csv")
  CookBookService.config("data/test/cookBookForReadFromFileScenario.txt")

  "A menu" should "be loaded from text" in {
    val menu = Menu.apply(
      """Zaterdag valt op:05112011
        |zaterdag:R1
        |zondag:R2
        | """.stripMargin)
    assertEquals(2, menu.menuItems.length)
    val recipeDay1 = menu.menuItems.head
    assertEquals("R1", recipeDay1.recipe)
    val recipeDay2 = menu.menuItems(1)
    assertEquals("R2", recipeDay2.recipe)
  }

  it should "A menu can be read from a file" in {
    val menu = Menu.readFromFile("data/test/menuForReadFromFileScenario.txt")
    assertEquals(2, menu.menuItems.length)
    assertEquals("R1", menu.recipes.head._2.name)
    assertEquals("R2", menu.recipes(1)._2.name)
  }

  it should "Date for R1 is 01022014 and date for R2 is 02022014" in {
    val fmt = DateTimeFormat.forPattern("ddMMyyyy")
    val menu = Menu.readFromFile("data/test/menus/0101.txt")
    assertEquals("01022014", fmt.print(menu.menuItems.head.date))
    assertEquals("02022014", fmt.print(menu.menuItems(1).date))
  }

  it should "Week may start on a Wednesday" in {
    val fmt = DateTimeFormat.forPattern("ddMMyyyy")
    val menu = Menu.readFromFile("data/test/menus/somemenu.txt")
    assertEquals("08022014", fmt.print(menu.menuItems(3).date))
    assertEquals("09022014", fmt.print(menu.menuItems(4).date))
  }

}
