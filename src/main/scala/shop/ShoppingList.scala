package shop

import scala.annotation.tailrec
import org.joda.time.DateTime

/**
 * ShoppingList creates a list of groceries sorted by category for a given Menu.
 */
class ShoppingList(menu: Menu) {
  val shoppingListItemsSortedByCategory = getShoppingListItemsSortedByCategory

  /* Construct the list of ShoppingListItems by combining a Recipe and a date. */
  def getShoppingListItemsWithDateAdded(recipe: Recipe, date: DateTime): List[ShoppingListItem] = {
    @tailrec def recursiveAdd(ingredients: List[Ingredient], result: List[ShoppingListItem]): List[ShoppingListItem] = {
      ingredients match {
        case Nil => result
        case head :: tail => recursiveAdd(tail, new ShoppingListItem(head, date) :: result)
      }
    }
    recursiveAdd(recipe.ingredients, List())
  }

  def getShoppingListItemsSortedByCategory: List[ShoppingListItem] = {
    @tailrec def recursiveAdd(theDate: DateTime, recipes: List[Recipe], result: List[List[ShoppingListItem]]): List[List[ShoppingListItem]] = {
      recipes match {
        case Nil => result
        case head :: tail => recursiveAdd(theDate.plusDays(1), tail, getShoppingListItemsWithDateAdded(head, theDate) :: result)
      }
    }
    recursiveAdd(menu.dateOfSaturday, menu.recipes, List()).flatten.sort(_ < _)
  }

  def printShoppinglistForUseWhileShopping: String =
    menu.printMenu + "\n" + printShoppinglistButSkipDuplicateCategoryLables

  def printShoppinglistButSkipDuplicateCategoryLables: String = {
    @tailrec def recursiveAdd(shopingListItems: List[ShoppingListItem], currentCategory: Category, shoppingListAsString: String): String = {
      shopingListItems match {
        case Nil => shoppingListAsString
        case head :: tail =>
          {
            val label = printLabelIfNecessary(currentCategory, head.category)
            recursiveAdd(tail, head.category, shoppingListAsString + "\n" + label + head)
          }
      }
    }
    recursiveAdd(shoppingListItemsSortedByCategory, null, "")
  }

  def printLabelIfNecessary(currentCategory: Category, newCategory: Category): String = {
    if (currentCategory == null) newCategory.name + ":"
    else {
      if (currentCategory.equals(newCategory)) "      "
      else newCategory.name + ":"
    }
  }

  def printShoppinglist: String = {
    @tailrec def recursivePrint(shoppingListItems: List[ShoppingListItem], printedShoppingListItems: String): String = {
      shoppingListItems match {
        case Nil => printedShoppingListItems.substring(1)
        case head :: tail => recursivePrint(tail, printedShoppingListItems + "\n" + head)
      }
    }
    recursivePrint(shoppingListItemsSortedByCategory, "")
  }
}

object ShoppingList {

  /*
   * See if it works.
   */
  def main(args: Array[String]): Unit = {
    val menu = Menu.readFromFile("data/15102011.txt", CookBook.readFromFile("data/cookbook.txt"))
    val shoppingList = new ShoppingList(menu)
    val theList = shoppingList.printShoppinglistForUseWhileShopping
    println(theList)
  }

}