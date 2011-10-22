package shop

import scala.annotation.tailrec
import org.apache.commons.io.FileUtils
import java.io.File
import org.joda.time.DateTime
import org.joda.time.format._

/**
 * A menu is a collection of recipes for a week starting on a Saturday.
 */
class Menu(val listOfRecipes: List[(String, String)], val cookbook: CookBook, val dateOfSaturday: DateTime) {
  val recipes: List[(String, Recipe)] = for (recipe <- listOfRecipes) yield (recipe._1, cookbook.findRecipeByName(recipe._2))

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
   * Create a menu from a list of strings representing day:recipe pairs and a cookbook.
   * The first line of input is the date for Saturday. This line should look like this:
   * 	"Zaterdag valt op:08102011"
   * where the text up to the : doesn't matter. The date format used is ddMMyyyy as in JodaTime format
   * The next lines are assumed to be Day:Recipe pairs where Day is the name of a Day (in Dutch, starting with a lower case letter)
   * and recipe refers to a recipe name as specified in a Cookbook.
   */
  def apply(menuAsString: String, cookbook: CookBook): Menu = {
    val menuAsListOfStrings = List.fromArray(menuAsString.split("\n"))
    val dateOfSaturday = parseDateForSaturday(menuAsListOfStrings(0).split(":")(1).trim)

    @tailrec def recursiveAdd(menuAsListOfStrings: List[String], menu: List[(String, String)]): List[(String, String)] = {
      menuAsListOfStrings match {
        case Nil => menu.reverse
        case head :: tail =>
          if (head.trim.length > 0) {
            val menuLine = head.trim
            if (menuLine.endsWith(":-"))
            	recursiveAdd(menuAsListOfStrings.drop(1), menu)
              else
            	  recursiveAdd(menuAsListOfStrings.drop(1), createMenuLineFromTextLine(head) :: menu)
          } else recursiveAdd(menuAsListOfStrings.drop(1), menu)
      }
    }
    new Menu(recursiveAdd(menuAsListOfStrings.drop(1), List[(String, String)]()), cookbook: CookBook, dateOfSaturday)
  }

  def createMenuLineFromTextLine(textLine: String): Tuple2[String, String] = {
    (textLine.split(":")(0).trim, textLine.split(":")(1).trim)
  }

  def readFromFile(fileName: String, cookBook: CookBook): Menu = {
    val menuAsText = FileUtils.readFileToString(new File(fileName))
    apply(menuAsText, cookBook)
  }

  def parseDateForSaturday(saturday: String): DateTime = {
    val fmt = DateTimeFormat forPattern "ddMMyyyy"
    fmt parseDateTime saturday
  }
}