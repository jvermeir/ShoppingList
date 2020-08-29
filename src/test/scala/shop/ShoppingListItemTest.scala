package shop

import org.joda.time.DateTime
import org.junit.Assert._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec

class ShoppingListItemTest extends AnyFlatSpec with BeforeAndAfterAll {

  CategoryService.config("data/test/categoryDatabase.csv")
  CookBookService.config("data/test/cookBookForReadFromFileScenario.txt")

  "ShoppingListItems" should "be based on category" in {
    val time = new DateTime(2011, 10, 9, 0, 0)
    val zeep = ShoppingListItem(Ingredient.applyFromText("schoonmaak", "zeep"), time)
    val bier = ShoppingListItem(Ingredient.applyFromText("dranken", "Duvel"), time)
    assertTrue(zeep > bier)
  }

  it should "Sort by name if ingredients Belong To Same Category" in {
    val time = new DateTime(2011, 10, 9, 0, 0)
    val duvel = ShoppingListItem(Ingredient.applyFromText("dranken", "Duvel"), time)
    val chouffe = ShoppingListItem(Ingredient.applyFromText("dranken", "Chouffe"), time)
    assertTrue(duvel > chouffe)
  }

  it should "Sort a List" in {
    val time = new DateTime(2011, 10, 9, 0, 0)
    val duvel = ShoppingListItem(Ingredient.applyFromText("dranken", "Duvel"), time)
    val chouffe = ShoppingListItem(Ingredient.applyFromText("dranken", "Chouffe"), time)
    val karmeliet = ShoppingListItem(Ingredient.applyFromText("dranken", "Karmeliet"), time)
    val result = List(chouffe, duvel, karmeliet)
    val testList = List(duvel, karmeliet, chouffe).sortWith(_ < _)
    assertEquals(result, testList)
  }

  it should "translate names Of Days Are Mapped To Correct Dates" in {
    val menuAsString = """Zaterdag valt op:08102011
      	|zondag:R1
      	|maandag:R2
      |""".stripMargin
    val menu = Menu(menuAsString: String)
    val list = new ShoppingList(menu, List())
    assertEquals(list.nameOfDayToDateMap("zondag"), new DateTime(2011, 10, 9, 0, 0))
  }

}
