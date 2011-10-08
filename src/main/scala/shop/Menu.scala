package shop

import scala.annotation.tailrec

class Menu(listOfRecipeNames: List[String], cookbook: CookBook) {
  val recipes = for (recipe <- listOfRecipeNames) yield cookbook.findRecipe(recipe)
  val ingredients = for (recipe <- recipes) yield recipe.ingredients
  val sortedListOfIngredients = ingredients.flatten.sort(_ < _)

  def printMenu: String = { 
    var menu: String = ""
    for (recipeName <- listOfRecipeNames) {
      menu = menu + recipeName + "\n"
    }
    menu
  }
  
  def printShoppinglistForUseWhileShopping:String = "todo"

  def printShoppinglist: String = {
    var list: String = ""
    for (ingredient <- sortedListOfIngredients)
      list = list + ingredient + "\n"
    list
  }
}

object Menu {
  def apply(menuAsString: String, cookbook: CookBook): Menu = {
    val menuAsListOfStrings = List.fromArray(menuAsString.split("\n"))
    @tailrec def recursiveAdd(menuAsListOfStrings: List[String], menu: List[String]): List[String] = {
      menuAsListOfStrings match {
        case Nil => menu.reverse
        case head :: tail =>
          if (head.trim.length > 0) recursiveAdd(menuAsListOfStrings.drop(1), head.split(":")(1) :: menu)
          else recursiveAdd(menuAsListOfStrings.drop(1), menu)
      }
    }
    new Menu(recursiveAdd(menuAsListOfStrings, List[String]()), cookbook: CookBook)
  }
}