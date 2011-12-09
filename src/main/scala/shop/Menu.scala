package shop

import scala.annotation.tailrec
import org.apache.commons.io.FileUtils
import java.io.File
import org.joda.time.DateTime
import org.joda.time.format._

/**
 * A menu is a collection of recipes for a week starting on a Saturday.
 */
class Menu(val listOfRecipes: List[(String, String)], val cookbook: CookBookClient, val dateOfSaturday: DateTime) {
  val recipes: List[(String, Recipe)] = for (recipe <- listOfRecipes) yield (recipe._1, cookbook.getRecipeByName(recipe._2))

  def printMenu: String = {
    @tailrec def recursivePrintMenu(listOfRecipes: List[(String, String)], menuAsString: String): String = {
      listOfRecipes match {
        case Nil => menuAsString
        case head :: tail => {
          val recipe: (String, String) = head
          recursivePrintMenu(tail, menuAsString + "\n" + recipe._1 + ":" + recipe._2)
        }
      }
    }
    recursivePrintMenu(listOfRecipes, "").substring(1)
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
  def apply(menuAsString: String, cookbook: CookBookClient): Menu = {
    val menuAsListOfStrings = menuAsString.lines.toList
    val dateOfSaturday = parseDateForSaturday(menuAsListOfStrings(0).split(":")(1).trim)
    val menuAsStringsWithoutHeaderLine = menuAsListOfStrings.drop(1)
    val menu: List[(String, String)] = menuAsStringsWithoutHeaderLine map { createMenuLineFromTextLine(_) } filter { _ != null }
    new Menu(menu, cookbook, dateOfSaturday)
  }

  def createMenuLineFromTextLine(textLine: String): Tuple2[String, String] = {
    if (isValidMenuLine(textLine))
      (textLine.split(":")(0).trim.toLowerCase(), textLine.split(":")(1).trim)
    else null
  }

  def isValidMenuLine(textLine:String):Boolean = {
    val text = textLine.trim()
    text.length() > 0 && text.indexOf(":") > 0 && !text.endsWith(":-")
  }
  
  def readFromFile(fileName: String, cookBook: CookBookClient): Menu = {
    val menuAsText = FileUtils.readFileToString(new File(fileName))
    apply(menuAsText, cookBook)
  }

  def parseDateForSaturday(saturday: String): DateTime = {
    val fmt = DateTimeFormat forPattern "ddMMyyyy"
    fmt parseDateTime saturday
  }
}