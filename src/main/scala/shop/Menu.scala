package shop

import java.io.File
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter
import java.util.{Locale, UUID}

import org.apache.commons.io.FileUtils
import rest.JsonFormats
import shop.Dates.{jsonDateFormatter, parseIsoDateString, parseddMMyyyyDateString}
import shop.Menu.toDutchNameOfDayFormatter
import spray.json.{DefaultJsonProtocol, _}

case class MenuItem (id:String, date:LocalDateTime, dayOfWeek:String, recipe:String) extends DefaultJsonProtocol

/**
 * A menu is a collection of recipes for a period of a week or less.
 */
case class Menu(menuItems: List[MenuItem], startOfPeriod: LocalDateTime) {
  val recipes: List[(LocalDateTime, Recipe)] = for (menuItem <- menuItems) yield (menuItem.date, CookBookService.store.getRecipeByName(menuItem.recipe))

  def printMenu(nameOfDayToDateMap: Map[String, LocalDateTime]): String = {
      menuItems.map (menuItem => nameOfDayToDateMap(menuItem.dayOfWeek).getDayOfMonth + " " + menuItem.dayOfWeek + ":" + menuItem.recipe) mkString "\n"
  }

  def printMenuForShoppingList: String = {
    recipes.map {case (date,recipe) => date.format(toDutchNameOfDayFormatter) + ":" + recipe.toString } mkString "\n"
  }

  def getNameOfDayToDateMap:Map[String, LocalDateTime] = {
    Menu.getNameOfDayToDateMap(startOfPeriod)
  }

}

object Menu extends DefaultJsonProtocol with JsonFormats {
  def fromJson(data: String):Menu = data.parseJson.convertTo[Menu]

  def item(id:String, date:String, dayOfWeek:String, recipe: String):MenuItem = {
    MenuItem(id, LocalDateTime.from(jsonDateFormatter.parse(date)), dayOfWeek, recipe)
  }

  def newMenuWithADayRemoved(menu: Menu, dateToBeRemoved: LocalDateTime): Menu = {
    val newMenuItems = menu.menuItems.filter(item => dateToBeRemoved != item.date)
    Menu(newMenuItems, menu.startOfPeriod)
  }

  def newMenuWithARecordRemoved(menu: Menu, idToBeRemoved: String): Menu = {
    val newMenuItems = menu.menuItems.filter(item => idToBeRemoved != item.id)
    Menu(newMenuItems, menu.startOfPeriod)
  }

  def newMenuWithADayAdded(menu: Menu, menuItem: MenuItem): Menu = {
    val newMenuItems = menuItem :: menu.menuItems
    Menu(newMenuItems, menu.startOfPeriod)
  }

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
    val dateOfFirstDay = parseddMMyyyyDateString(menuAsListOfStrings.head.split(":")(1).trim).get
    val menuAsStringsWithoutHeaderLine = menuAsListOfStrings.drop(1)
    val menu: List[MenuItem] = menuAsStringsWithoutHeaderLine map {
      createMenuLineFromTextLine(_, dateOfFirstDay)
    } filter {
      _ != null
    }
    new Menu(menu, dateOfFirstDay)
  }

  val toDutchNameOfDayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE", new Locale("nl", "NL"))

  def getNameOfDayToDateMap (dateOfFirstDay:LocalDateTime): Map[String, LocalDateTime] = {
    val day = dateOfFirstDay
    val result = for (i <- 0 until 7) yield {
      Tuple2(day.plusDays(i).format(toDutchNameOfDayFormatter).toLowerCase, day.plusDays(i))
    }
    result.toMap[String, LocalDateTime]
  }

  def createMenuLineFromTextLine(textLine: String, dateOfFirstDay: LocalDateTime): MenuItem = {
    if (isValidMenuLine(textLine)) {
      val day: String = textLine.split(":")(0).trim.toLowerCase()
      val date = getNameOfDayToDateMap(dateOfFirstDay)(day)
      MenuItem(UUID.randomUUID.toString, date, day, textLine.split(":")(1).trim)
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

}