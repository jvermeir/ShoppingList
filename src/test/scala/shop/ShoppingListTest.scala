package shop

import org.junit._
import Assert._
import org.joda.time.DateTime
import org.joda.time.format._
import java.util.Locale
// Category

class ShoppingListTest {

  @Test
  def namesOfDaysAreMappedToCorrectDates() {
    val cookBook = CookBook("""naam:Witlof met kip
      				zuivel:geraspte kaas""")
    val menuAsString = """Zaterdag valt op:08102011
      	zondag:Witlof met kip
      	maandag:Witlof met kip
      """
    val menu = Menu(menuAsString: String, cookBook: CookBook)
    val list = new ShoppingList(menu)
    assertEquals(list.nameOfDayToDateMap("zondag"), new DateTime(2011, 10, 9, 0, 0))
  }

  @Test
  def jodaTimeTest {
    val xx=Locale.getAvailableLocales
    val fmt = (DateTimeFormat forPattern "EEEE").withLocale(new Locale("nl"))
    val day = new DateTime(2011, 10, 9, 0, 0)
    val nameOfDay = fmt.print(day)
    assertEquals("zondag", nameOfDay)
  }
}
