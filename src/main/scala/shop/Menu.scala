package shop

import scala.annotation.tailrec
import org.apache.commons.io.FileUtils
import java.io.File
import org.joda.time.DateTime
import org.joda.time.format._

class Menu(val listOfRecipes: List[(String, String)], val cookbook: CookBook, val dateOfSaturday: DateTime) {
  val recipes = for (recipe <- listOfRecipes) yield cookbook.findRecipe(recipe._2)
  
  def printMenu: String = {
    var menu: String = ""
    for (recipe <- listOfRecipes) {
      menu = menu + recipe._1 + ":" + recipe._2 + "\n"
    }
    menu
  }
}

object Menu {
  def apply(menuAsString: String, cookbook: CookBook): Menu = {
    val menuAsListOfStrings = List.fromArray(menuAsString.split("\n"))
    val dateOfSaturday = parseDateForSaturday(menuAsListOfStrings(0).split(":")(1).trim)

    @tailrec def recursiveAdd(menuAsListOfStrings: List[String], menu: List[(String, String)]): List[(String, String)] = {
      menuAsListOfStrings match {
        case Nil => menu.reverse
        case head :: tail =>
          if (head.trim.length > 0) recursiveAdd(menuAsListOfStrings.drop(1), createMenuLineFromTextLine(head) :: menu)
          else recursiveAdd(menuAsListOfStrings.drop(1), menu)
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