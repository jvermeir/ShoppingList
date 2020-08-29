package shop

import org.joda.time.DateTime

import scala.annotation.tailrec

/**
 * ShoppingList creates a list of groceries sorted by category for a given Menu.
 */
class ShoppingList(menu: Menu, extras: List[Ingredient]) {
  val nameOfDayToDateMap: Map[String, DateTime] = Menu.getNameOfDayToDateMap(menu.dateOfSaturday)
  val shoppingListItemsSortedByCategory: List[ShoppingListItem] = getShoppingListItemsSortedByCategory

  /* Construct the list of ShoppingListItems by combining a Recipe and a date. */
  def getShoppingListItemsWithDateAdded(recipe: Recipe, date: DateTime): List[ShoppingListItem] = {
    recipe.ingredients map (ingredient => ShoppingListItem(ingredient, date))
    for (ingredient <- recipe.ingredients) yield ShoppingListItem(ingredient, date)
  }

  def getShoppingListItemsSortedByCategory: List[ShoppingListItem] = (itemsFromMenu ::: itemsFromExtras).sortWith(_ < _)

  def itemsFromMenu: List[ShoppingListItem] = {
    val items = for (recipe <- menu.recipes) yield getShoppingListItemsWithDateAdded(recipe._2, findDate(recipe._1))
    items.flatten
  }

  def itemsFromExtras: List[ShoppingListItem] = {
    for (ingredient <- extras; if ingredient != null) yield new ShoppingListItem(ingredient)
  }

  def findDate(nameOfDay: String): DateTime = {
    nameOfDayToDateMap(nameOfDay)
  }

  def printShoppinglistForUseWhileShopping: String =
    menu.printMenu(nameOfDayToDateMap) + "\n" +
      printShoppinglistButSkipDuplicateCategoryLables +
      "\n\nrecepten:\n" +
      menu.printMenuForShoppingList

  def printShoppinglistButSkipDuplicateCategoryLables2: String = {
    ""
  }

  def printShoppinglistButSkipDuplicateCategoryLables: String = {
    @tailrec def recursiveAdd(shopingListItems: List[ShoppingListItem], currentCategory: Category, shoppingListAsString: String): String = {
      shopingListItems match {
        case Nil => shoppingListAsString
        case head :: tail => {
          val label = printLabelIfNecessary(currentCategory, head.category)
          recursiveAdd(tail, head.category, shoppingListAsString + "\n" + label + head)
        }
      }
    }

    recursiveAdd(shoppingListItemsSortedByCategory, null, "")
  }

  def printLabelIfNecessary(currentCategory: Category, newCategory: Category): String = {
    if (currentCategory == null) newCategory.name + ":\n      "
    else if (currentCategory.equals(newCategory)) "      "
    else newCategory.name + ":\n      "

  }

  def printShoppinglist: String = {
    shoppingListItemsSortedByCategory mkString "\n"
  }

}
