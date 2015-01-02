package shop

import org.joda.time.DateTime
import org.junit.Assert._
import org.junit._
import org.scalatest.{Spec, BeforeAndAfterAll, Suite}

class ShoppingListItemTest extends Spec with BeforeAndAfterAll {

  implicit object InMemoryCategoryConfig extends Config {
    lazy val cookBookStore = new InMemoryCookbookStore
    lazy val categoryStore = new InMemoryCategoryStore
  }

  def `Sort Order Is OK If Ingredients Belong To Different Category` {
    val time = new DateTime(2011, 10, 9, 0, 0)
    val zeep = ShoppingListItem(new Ingredient("schoonmaak", "zeep"), time)
    val bier = ShoppingListItem(new Ingredient("dranken", "Duvel"), time)
    assertTrue(zeep > bier)
  }

  def `Sort Order Is OK If Ingredients Belong To Same Category` {
    val time = new DateTime(2011, 10, 9, 0, 0)
    val duvel = ShoppingListItem(new Ingredient("dranken", "Duvel"), time)
    val chouffe = ShoppingListItem(new Ingredient("dranken", "Chouffe"), time)
    assertTrue(duvel > chouffe)
  }

  def `Sort Order Is OK For A List` {
    val time = new DateTime(2011, 10, 9, 0, 0)
    val duvel = ShoppingListItem(new Ingredient("dranken", "Duvel"), time)
    val chouffe = ShoppingListItem(new Ingredient("dranken", "Chouffe"), time)
    val karmeliet = ShoppingListItem(new Ingredient("dranken", "Karmeliet"), time)
    val result = List(chouffe, duvel, karmeliet)
    val testList = List(duvel, karmeliet, chouffe).sortWith(_ < _)
    assertEquals(result, testList)
  }

  def `names Of Days Are Mapped To Correct Dates` {
    val menuAsString = """Zaterdag valt op:08102011
      	|zondag:R1
      	|maandag:R2
      |""".stripMargin
    val menu = Menu(menuAsString: String, new CookBook)
    val list = new ShoppingList(menu, List())
    assertEquals(list.nameOfDayToDateMap("zondag"), new DateTime(2011, 10, 9, 0, 0))
  }

}
