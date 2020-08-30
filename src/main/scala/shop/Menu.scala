package shop

import java.io.File
import java.time.LocalDate
import java.util.Locale

import org.apache.commons.io.FileUtils
import rest.JsonFormats
import spray.json._

case class MenuItem (date:LocalDate, dayOfWeek:String, recipe:String) extends DefaultJsonProtocol

/**
 * A menu is a collection of recipes for a week starting on a Saturday.
 */
case class Menu(menuItems: List[MenuItem], dateOfSaturday: LocalDate) {

  val recipes: List[(String, Recipe)] = for (menuItem <- menuItems) yield (menuItem.dayOfWeek, CookBookService.store.getRecipeByName(menuItem.recipe))

  def printMenu(nameOfDayToDateMap: Map[String, LocalDate]): String = {
      menuItems.map (menuItem => nameOfDayToDateMap(menuItem.dayOfWeek).dayOfMonth.get + " " + menuItem.dayOfWeek + ":" + menuItem.recipe) mkString "\n"
  }

  def printMenuForShoppingList: String = {
    recipes.map {case (name,recipe) => name + ":" + recipe.toString } mkString "\n"
  }

  def getNameOfDayToDateMap:Map[String, LocalDate] = {
    Menu.getNameOfDayToDateMap(dateOfSaturday)
  }

}

object Menu extends DefaultJsonProtocol with JsonFormats {
  def fromJson(data: String):Menu = data.parseJson.convertTo[Menu]
  /*
   * Create a menu from a list of strings representing day:recipe pairs and a cook book.
   * The first line of input is the date for Saturday. This line should look like this:
   * 	"Zaterdag valt op:08102011"
   * where the text up to the : doesn't matter. The date format used is ddMMyyyy as in JodaTime format
   * The next lines are assumed to be Day:Recipe pairs where Day is the name of a Day (in Dutch)
   * and recipe refers to a recipe name as specified in a Cook book.
   * Following a line containing the text 'extra' a list of groceries can be added, just like the ingredients in a cook book.
   */
  def apply(menuAsString: String): Menu = {
    val menuAsListOfStrings = menuAsString.split("\n").toList
    val dateOfFirstDay = parseDateForFirstDay(menuAsListOfStrings.head.split(":")(1).trim)
    val menuAsStringsWithoutHeaderLine = menuAsListOfStrings.drop(1)
    val menu: List[MenuItem] = menuAsStringsWithoutHeaderLine map {
      createMenuLineFromTextLine(_, dateOfFirstDay)
    } filter {
      _ != null
    }
    new Menu(menu, dateOfFirstDay)
  }

  val dayInMs = 24*60*60*1000

  def getNameOfDayToDateMap (dateOfFirstDay:LocalDate): Map[String, LocalDate] = {
    val fmt = (LocalDateFormat forPattern "EEEE").withLocale(new Locale("nl"))
    val day = dateOfFirstDay
    val result = for (i <- 0 until 7) yield {
      Tuple2(fmt.print(day.plusDays(i)), day.plusDays(i))
    }
    result.toMap[String, LocalDate]
  }

  def createMenuLineFromTextLine(textLine: String, dateOfFirstDay: LocalDate): MenuItem = {
    if (isValidMenuLine(textLine)) {
      val day: String = textLine.split(":")(0).trim.toLowerCase()
      val date = getNameOfDayToDateMap(dateOfFirstDay)(day)
      MenuItem(date, day, textLine.split(":")(1).trim)
    }
    else null
  }

  def isValidMenuLine(textLine: String): Boolean = {
    val text = textLine.trim()
    text.length() > 0 && text.indexOf(":") > 0 && !text.endsWith(":-")
  }

  def readFromFile(fileName: String): Menu = readFromFile(new File(fileName))

  def readFromFile(file: File): Menu = {
    val menuAsText = FileUtils.readFileToString(file, "UTF-8")
    apply(menuAsText)
  }

  def parseDateForFirstDay(saturday: String): LocalDate = {
    val fmt = LocalDateFormat forPattern "ddMMyyyy"
    new LocalDate(fmt.parseLocalDate(saturday))
  }
}