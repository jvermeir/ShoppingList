package shop

import org.joda.time.format.DateTimeFormat
import org.junit.Assert._
import org.scalatest._


class TempTest extends Spec {

  implicit object InMemoryCategoryConfig extends Config {
    lazy val cookBookStore = new InMemoryCookbookStore
    lazy val categoryStore = new InMemoryCategoryStore
  }

  val cookBook = new CookBook

  def `Week may start on a Wednesday`: Unit = {
    val fmt = DateTimeFormat.forPattern("ddMMyyyy")
    val menu = Menu.readFromFile("data/test/menus/0502.txt", cookBook)
    assertEquals("08022014", fmt.print(menu.menuItems(3).date))
    assertEquals("09022014", fmt.print(menu.menuItems(4).date))
  }

}
