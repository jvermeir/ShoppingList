package shop

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.{Locale, UUID}
import org.apache.commons.io.FileUtils
import rest.JsonFormats
import shop.Dates.{jsonDateFormatter, parseddMMyyyyDateString}
import shop.Menu.toDutchNameOfDayFormatter
import spray.json.{DefaultJsonProtocol, _}

import java.time.temporal.TemporalAmount

case class MenuItem(id: String, date: LocalDate, recipe: String)

object MenuItem extends DefaultJsonProtocol with JsonFormats {
  def fromJson(recipeAsText: String): MenuItem = recipeAsText.parseJson.convertTo[MenuItem]
}

/**
 * A menu is a collection of recipes for a period of a week or less.
 */
case class Menu(menuItems: List[MenuItem], startOfPeriod: LocalDate) {
  val recipes: List[(LocalDate, Recipe)] = for (menuItem <- menuItems) yield (menuItem.date, CookBookService.store.getRecipeByName(menuItem.recipe))

  def printMenu(nameOfDayToDateMap: Map[String, LocalDate]): String = {
    menuItems.map(menuItem => menuItem.date.getDayOfMonth + " " + menuItem.date.format(toDutchNameOfDayFormatter) + ":" + menuItem.recipe) mkString "\n"
  }

  def printMenuForShoppingList: String = {
    recipes.map { case (date, recipe) => date.format(toDutchNameOfDayFormatter) + ":" + recipe.toString } mkString "\n"
  }

  def getNameOfDayToDateMap: Map[String, LocalDate] = {
    Menu.getNameOfDayToDateMap(startOfPeriod)
  }

  def sorted: Menu = {
    Menu(menuItems.sortWith((i: MenuItem, j: MenuItem) => i.date.isBefore(j.date)), startOfPeriod)
  }
}

object Menu extends DefaultJsonProtocol with JsonFormats {
  def fromJson(data: String): Menu = data.parseJson.convertTo[Menu]

  def getSevenDayMenuWithRecipeForEachDay(menu: Menu):Menu = {
    val emptyRecipe = CookBookService.store.getRecipeByName("-")
    val menuList = for (dayOfWeek <- 0 to 6) yield getRecipeOrEmpty(menu, dayOfWeek, emptyRecipe)
    Menu(menuList.toList, menu.startOfPeriod)
  }

  def getRecipeOrEmpty(menu: Menu, dayOfWeek: Int, emptyRecipe: Recipe): MenuItem = {
    val theDate = menu.startOfPeriod.plusDays(dayOfWeek)
    val menuItemsForDate = menu.menuItems.filter(_.date == theDate)
    if (menuItemsForDate.nonEmpty)
      menuItemsForDate.head
    else
      MenuItem(dayOfWeek + 1 + "", theDate, emptyRecipe.name)
  }

  def item(id: String, date: String, dayOfWeek: String, recipe: String): MenuItem = {
    MenuItem(id, LocalDate.from(jsonDateFormatter.parse(date)), recipe)
  }

  def newMenuWithADayRemoved(menu: Menu, dateToBeRemoved: LocalDate): Menu = {
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

  def getNameOfDayToDateMap(dateOfFirstDay: LocalDate): Map[String, LocalDate] = {
    val day = dateOfFirstDay
    val result = for (i <- 0 until 7) yield {
      Tuple2(day.plusDays(i).format(toDutchNameOfDayFormatter).toLowerCase, day.plusDays(i))
    }
    result.toMap[String, LocalDate]
  }

  def createMenuLineFromTextLine(textLine: String, dateOfFirstDay: LocalDate): MenuItem = {
    if (isValidMenuLine(textLine)) {
      val day: String = textLine.split(":")(0).trim.toLowerCase()
      val date = getNameOfDayToDateMap(dateOfFirstDay)(day)
      MenuItem(UUID.randomUUID.toString, date, textLine.split(":")(1).trim)
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