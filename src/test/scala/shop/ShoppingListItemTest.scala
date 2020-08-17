package shop

import org.joda.time.DateTime
import org.junit.Assert._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec

class ShoppingListItemTest extends AnyFlatSpec with BeforeAndAfterAll {

  implicit object InMemoryCategoryConfig extends Config {
    lazy val cookBookStore = new InMemoryCookbookStore
    lazy val categoryStore = new InMemoryCategoryStore
  }

  "ShoppingListItems" should "be based on category" in {
    val time = new DateTime(2011, 10, 9, 0, 0)
    val zeep = ShoppingListItem(new Ingredient("schoonmaak", "zeep"), time)
    val bier = ShoppingListItem(new Ingredient("dranken", "Duvel"), time)
    assertTrue(zeep > bier)
  }

  it should "Sort by name if ingredients Belong To Same Category" in {
    val time = new DateTime(2011, 10, 9, 0, 0)
    val duvel = ShoppingListItem(new Ingredient("dranken", "Duvel"), time)
    val chouffe = ShoppingListItem(new Ingredient("dranken", "Chouffe"), time)
    assertTrue(duvel > chouffe)
  }

  it should "Sort a List" in {
    val time = new DateTime(2011, 10, 9, 0, 0)
    val duvel = ShoppingListItem(new Ingredient("dranken", "Duvel"), time)
    val chouffe = ShoppingListItem(new Ingredient("dranken", "Chouffe"), time)
    val karmeliet = ShoppingListItem(new Ingredient("dranken", "Karmeliet"), time)
    val result = List(chouffe, duvel, karmeliet)
    val testList = List(duvel, karmeliet, chouffe).sortWith(_ < _)
    assertEquals(result, testList)
  }

  it should "translate names Of Days Are Mapped To Correct Dates" in {
    val menuAsString = """Zaterdag valt op:08102011
      	|zondag:R1
      	|maandag:R2
      |""".stripMargin
    val menu = Menu(menuAsString: String, new CookBook(InMemoryCategoryConfig))
    val list = new ShoppingList(menu, List())
    assertEquals(list.nameOfDayToDateMap("zondag"), new DateTime(2011, 10, 9, 0, 0))
  }

}
