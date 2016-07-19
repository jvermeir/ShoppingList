package shop

import java.io.File

import org.apache.commons.io.FileUtils
import org.joda.time.DateTime
import org.joda.time.format._

import scala.annotation.tailrec

/**
 * A menu is a collection of recipes for a week starting on a Saturday.
 */
class Menu(val menuItems: List[MenuItem], val cookbook: CookBook, val dateOfSaturday: DateTime) {
  val recipes: List[(String, Recipe)] = for (menuItem <- menuItems) yield (menuItem.dayOfWeek, cookbook.getRecipeByName(menuItem.recipe))

  // TODO: recursion? Tuple type in List?
  def printMenu(nameOfDayToDateMap: Map[String, DateTime]): String = {
    @tailrec def recursivePrintMenu(listOfRecipes: List[MenuItem], menuAsString: String): String = {
      listOfRecipes match {
        case Nil => menuAsString
        case head :: tail => {
          recursivePrintMenu(tail, menuAsString + "\n" + nameOfDayToDateMap(head.dayOfWeek).dayOfMonth.get + " " + head.dayOfWeek + ":" + head.recipe)
        }
      }
    }
    recursivePrintMenu(menuItems, "").substring(1)
  }

  def printMenuForShoppingList: String = {
    @tailrec def recursivePrintList(recipes: List[(String, Recipe)], recipesAsString: String): String = {
      recipes match {
        case Nil => recipesAsString
        case head :: tail => {
          val recipe: (String, Recipe) = head
          recursivePrintList(tail, recipesAsString + recipe._1 + ":" + recipe._2)
        }
      }
    }
    recursivePrintList(recipes, "")
  }
}

object Menu {
  /*
   * Create a menu from a list of strings representing day:recipe pairs and a cook book.
   * The first line of input is the date for Saturday. This line should look like this:
   * 	"Zaterdag valt op:08102011"
   * where the text up to the : doesn't matter. The date format used is ddMMyyyy as in JodaTime format
   * The next lines are assumed to be Day:Recipe pairs where Day is the name of a Day (in Dutch)
   * and recipe refers to a recipe name as specified in a Cook book.
   * Following a line containing the text 'extra' a list of groceries can be added, just like the ingredients in a cook book.
   */
  def apply(menuAsString: String, cookbook: CookBook): Menu = {
    val menuAsListOfStrings = menuAsString.lines.toList
    val dateOfSaturday = parseDateForSaturday(menuAsListOfStrings(0).split(":")(1).trim)
    val menuAsStringsWithoutHeaderLine = menuAsListOfStrings.drop(1)
    val menu: List[MenuItem] = menuAsStringsWithoutHeaderLine map {
      createMenuLineFromTextLine(_)
    } filter {
      _ != null
    }
    new Menu(menu, cookbook, dateOfSaturday)
  }

  def createMenuLineFromTextLine(textLine: String): MenuItem = {
    if (isValidMenuLine(textLine))
      MenuItem(textLine.split(":")(0).trim.toLowerCase(), textLine.split(":")(1).trim)
    else null
  }

  def isValidMenuLine(textLine: String): Boolean = {
    val text = textLine.trim()
    text.length() > 0 && text.indexOf(":") > 0 && !text.endsWith(":-")
  }

  def readFromFile(fileName: String, cookBook: CookBook): Menu = {
    readFromFile(new File(fileName), cookBook)
  }

  def readFromFile(file: File, cookBook: CookBook): Menu = {
    val menuAsText = FileUtils.readFileToString(file)
    apply(menuAsText, cookBook)
  }

  def parseDateForSaturday(saturday: String): DateTime = {
    val fmt = DateTimeFormat forPattern "ddMMyyyy"
    fmt parseDateTime saturday
  }
}