package shop

import scala.annotation.tailrec

class Menu(listOfRecipeNames: List[String], kookboek: CookBook) {
  val recipes = for (recipe <- listOfRecipeNames) yield kookboek.findRecipe(recipe)
  val ingredients = for (recipe <- recipes) yield recipe.ingredienten
  val sortedListOfIngredients = ingredients.flatten.sort(_ < _)

  def printMenu: String = {
    var menu: String = ""
    for (recipeName <- listOfRecipeNames) {
      menu = menu + recipeName + "\n"
    }
    menu
  }
  
  def printBoodschappenlijstForUseWhileShopping:String = "dummy"

  def printBoodschappenlijst: String = {
    var list: String = ""
    for (ingredient <- sortedListOfIngredients)
      list = list + ingredient + "\n"
    list
  }
}

object Menu {
  def apply(menuAsString: String, kookboek: CookBook): Menu = {
    val menuAsListOfStrings = List.fromArray(menuAsString.split("\n"))
    @tailrec def recursiveAdd(menuAsListOfStrings: List[String], menu: List[String]): List[String] = {
      menuAsListOfStrings match {
        case Nil => menu.reverse
        case head :: tail =>
          if (head.trim.length > 0) recursiveAdd(menuAsListOfStrings.drop(1), head.split(":")(1) :: menu)
          else recursiveAdd(menuAsListOfStrings.drop(1), menu)
      }
    }
    new Menu(recursiveAdd(menuAsListOfStrings, List[String]()), kookboek: CookBook)
  }
}