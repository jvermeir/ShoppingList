package shop

import scala.annotation.tailrec
import org.joda.time.DateTime
import org.joda.time.format._
import java.util.Locale

/**
 * ShoppingList creates a list of groceries sorted by category for a given Menu.
 */
class ShoppingList(menu: Menu) {
  val nameOfDayToDateMap = getNameOfDayToDateMap
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
    @tailrec def recursiveAdd(recipes: List[(String, Recipe)], result: List[List[ShoppingListItem]]): List[List[ShoppingListItem]] = {
      recipes match {
        case Nil => result
        case head :: tail => recursiveAdd(tail, getShoppingListItemsWithDateAdded(head._2, findDate(head._1)) :: result)
      }
    }
    recursiveAdd(menu.recipes, List()).flatten.sort(_ < _)
  }

  def findDate(nameOfDay: String): DateTime = {
    val result =nameOfDayToDateMap(nameOfDay)
    result
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

  def getNameOfDayToDateMap: Map[String, DateTime] = {
    val fmt = (DateTimeFormat forPattern "EEEE").withLocale(new Locale("nl"))
    val saturday = menu.dateOfSaturday
    val result = for (i <- 0 until 7) yield {
      Tuple2(fmt.print(saturday.plusDays(i)), saturday.plusDays(i))
    }
    result.toMap[String, DateTime]
  }
}

object ShoppingList {

  /*
   * See if it works.
   */
  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      println("Usage: shop.ShoppingList <path to cookbook> <path to week-menu>")
      System.exit(-1)
    }
    val menu = Menu.readFromFile(args(1), CookBook.readFromFile(args(0)))
    val shoppingList = new ShoppingList(menu)
    val theList = shoppingList.printShoppinglistForUseWhileShopping
    println(theList)
  }

}