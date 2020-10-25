package shop

import java.time.LocalDateTime

import org.junit.Assert._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec

class ShoppingListItemTest extends AnyFlatSpec with BeforeAndAfterAll {

  CategoryService.config("data/test/categoryDatabase.csv")
  CookBookService.config("data/test/cookBookForReadFromFileScenario.txt")
  val date = LocalDateTime.of(2011, 10, 9, 0, 0, 0)

  "ShoppingListItems" should "be based on category" in {
    val zeep = ShoppingListItem(Ingredient.applyFromText("schoonmaak", "zeep"), date)
    val bier = ShoppingListItem(Ingredient.applyFromText("dranken", "Duvel"), date)
    assertTrue(zeep > bier)
  }

  it should "Sort by name if ingredients Belong To Same Category" in {
    val duvel = ShoppingListItem(Ingredient.applyFromText("dranken", "Duvel"), date)
    val chouffe = ShoppingListItem(Ingredient.applyFromText("dranken", "Chouffe"), date)
    assertTrue(duvel > chouffe)
  }

  it should "Sort a List" in {
    val duvel = ShoppingListItem(Ingredient.applyFromText("dranken", "Duvel"), date)
    val chouffe = ShoppingListItem(Ingredient.applyFromText("dranken", "Chouffe"), date)
    val karmeliet = ShoppingListItem(Ingredient.applyFromText("dranken", "Karmeliet"), date)
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
    assertEquals(list.nameOfDayToDateMap("zondag"), date)
  }

}
