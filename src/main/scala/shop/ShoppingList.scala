package shop

import scala.annotation.tailrec
import org.joda.time.DateTime
import org.joda.time.format._
import java.util.Locale
import org.apache.commons.io.FileUtils
import java.io.File

/**
 * ShoppingList creates a list of groceries sorted by category for a given Menu.
 */
class ShoppingList(menu: Menu, extras:List[Ingredient]) {
  val nameOfDayToDateMap = getNameOfDayToDateMap
  val shoppingListItemsSortedByCategory = getShoppingListItemsSortedByCategory

  def this(menu:Menu) = this(menu, List())
  
  /* Construct the list of ShoppingListItems by combining a Recipe and a date. */
  def getShoppingListItemsWithDateAdded(recipe:Recipe, date:DateTime):List[ShoppingListItem] = {
    for(ingredient <- recipe.ingredients) yield new ShoppingListItem(ingredient, date)
  }

  def getShoppingListItemsSortedByCategory: List[ShoppingListItem] = {
    (itemsFromMenu::: itemsFromExtras).sort(_ < _)
  }
  
  def itemsFromMenu: List[ShoppingListItem] = {
    val items = for(recipe <- menu.recipes) yield getShoppingListItemsWithDateAdded(recipe._2, findDate(recipe._1))
    items.flatten
  }
  
  def itemsFromExtras:List[ShoppingListItem] = {
    for (ingredient <- extras; if (ingredient !=null)) yield new ShoppingListItem(ingredient)
  }
  
  def findDate(nameOfDay: String): DateTime = {
    nameOfDayToDateMap(nameOfDay)
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
    val cookbook = CookBook.readFromFile(args(0))
    val menuAndList = readAndSplit(args(1))
    val menu = Menu(menuAndList._1, CookBook.readFromFile(args(0)))
    val extras:List[Ingredient]=Ingredient.readFromText(menuAndList._2)
    val shoppingList = new ShoppingList(menu, extras)
    val theList = shoppingList.printShoppinglistForUseWhileShopping
    println(theList)
  }
  
  def readAndSplit(fileName: String): (String, String) = {
    split(FileUtils.readFileToString(new File(fileName)))
  }
  
  def split(contents:String): (String, String) = {
    if (contents.indexOf("extra") > 0) {
      val parts = contents.split("extra")
      (parts(0), parts(1))
    } else (contents, "")
  }
}