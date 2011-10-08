package shop

import scala.annotation.tailrec
import org.apache.commons.io.FileUtils
import java.io.File

class Menu(listOfRecipes: List[(String, String)], cookbook: CookBook) {
  val recipes = for (recipe <- listOfRecipes) yield cookbook.findRecipe(recipe._2)
  val ingredients = for (recipe <- recipes) yield recipe.ingredients
  val sortedListOfIngredients = ingredients.flatten.sort(_ < _)

  def printMenu: String = {
    var menu: String = ""
    for (recipe <- listOfRecipes) {
      menu = menu + recipe._1 + ":" + recipe._2 + "\n"
    }
    menu
  }

  def printShoppinglistForUseWhileShopping: String =
    printMenu + "\n" + printShoppinglistButSkipDuplicateCategoryLables

  def printShoppinglist: String = {
    var list: String = ""
    for (ingredient <- sortedListOfIngredients)
      list = list + ingredient + "\n"
    list
  }

  def printShoppinglistButSkipDuplicateCategoryLables: String = {
    var list: String = ""
    var currentCategory: String = ""
    var label = ""
    for (ingredient <- sortedListOfIngredients) {
      label = if (currentCategory.equals(ingredient.category)) "   " else ingredient.category + ":"
      currentCategory = ingredient.category
      list = list + label + ingredient.name + "\n"
    }
    list
  }
}

object Menu {
  def apply(menuAsString: String, cookbook: CookBook): Menu = {
    val menuAsListOfStrings = List.fromArray(menuAsString.split("\n"))
    @tailrec def recursiveAdd(menuAsListOfStrings: List[String], menu: List[(String, String)]): List[(String, String)] = {
      menuAsListOfStrings match {
        case Nil => menu.reverse
        case head :: tail =>
          if (head.trim.length > 0) recursiveAdd(menuAsListOfStrings.drop(1), createMenuLineFromTextLine(head) :: menu)
          else recursiveAdd(menuAsListOfStrings.drop(1), menu)
      }
    }
    new Menu(recursiveAdd(menuAsListOfStrings, List[(String, String)]()), cookbook: CookBook)
  }

  def createMenuLineFromTextLine(textLine:String):Tuple2[String, String] = {
    (textLine.split(":")(0).trim,textLine.split(":")(1).trim)
  }
  
  def readFromFile(fileName: String, cookBook: CookBook): Menu = {
    val menuAsText = FileUtils.readFileToString(new File(fileName))
    apply(menuAsText, cookBook)
  }
}