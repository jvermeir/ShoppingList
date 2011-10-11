package shop

import scala.annotation.tailrec
import org.apache.commons.io.FileUtils
import java.io.File
import org.joda.time.DateTime
import org.joda.time.format._

class Menu(listOfRecipes: List[(String, String)], cookbook: CookBook, dateOfSaturday: DateTime) {
  val recipes = for (recipe <- listOfRecipes) yield cookbook.findRecipe(recipe._2)
  var dayNumber:Int = 0
  var theDate = dateOfSaturday
  val ingredients = 
    for {recipe <- recipes} 
    yield {theDate = theDate.plusDays(1); getIngredientsWithDateAdded(recipe, theDate)}
  val sortedListOfIngredients = ingredients.flatten.sort(_ < _)
  
  def printMenu: String = {
    var menu: String = ""
    for (recipe <- listOfRecipes) {
      menu = menu + recipe._1 + ":" + recipe._2 + "\n"
    }
    menu
  }

  def getIngredientsWithDateAdded(recipe: Recipe, date:DateTime): List[IngredientForMenu] = {
    def recursiveAdd(ingredients: List[Ingredient]): List[IngredientForMenu] = {
      ingredients match {
        case Nil => List()
        case head :: tail => new IngredientForMenu(head, date) :: recursiveAdd(tail)
      }
    }
    recursiveAdd(recipe.ingredients)
  }

  def getIngredientsWithDateAdded(recipe: Recipe, dateOfSaturday: DateTime, dayNumber: Int): List[IngredientForMenu] = {
    val date:DateTime = dateOfSaturday.plusDays(dayNumber)
    def recursiveAdd(ingredients: List[Ingredient]): List[IngredientForMenu] = {
      ingredients match {
        case Nil => List()
        case head :: tail => new IngredientForMenu(head, date) :: recursiveAdd(tail)
      }
    }
    recursiveAdd(recipe.ingredients)
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

