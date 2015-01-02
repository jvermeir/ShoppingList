	package shop

import java.io.File
import java.util.Locale

import org.apache.commons.io.FileUtils
import org.joda.time.DateTime
import org.joda.time.format._

import scala.annotation.tailrec

  /**
   * ShoppingList creates a list of groceries sorted by category for a given Menu.
   */
  class ShoppingList(menu: Menu, extras:List[Ingredient]) (implicit val config:Config) {
    val nameOfDayToDateMap = getNameOfDayToDateMap
    val shoppingListItemsSortedByCategory = getShoppingListItemsSortedByCategory

    /* Construct the list of ShoppingListItems by combining a Recipe and a date. */
    def getShoppingListItemsWithDateAdded(recipe:Recipe, date:DateTime):List[ShoppingListItem] = {
      for(ingredient <- recipe.ingredients) yield new ShoppingListItem(ingredient, date)
    }

    def getShoppingListItemsSortedByCategory: List[ShoppingListItem] = {
      (itemsFromMenu::: itemsFromExtras).sortWith(_ < _)
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

      val cookbookFile = args(0)
      val menuFile = args(1)
      implicit object FileCategoryConfig extends Config {
        lazy val categoryStore = new FileBasedCategoryStore("data/CategoryDatabase.csv")
        lazy val cookBookStore = new FileBasedCookBookStore(cookbookFile)
      }

      val cookbook = new CookBook
      val menuAndListOfExtras = readAndSplit(menuFile)
      val menu = Menu(menuAndListOfExtras._1, cookbook)
      val extras:List[Ingredient]=Ingredient.readFromText(menuAndListOfExtras._2)
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