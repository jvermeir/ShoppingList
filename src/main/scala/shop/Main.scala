package shop

import java.io.File

import data.Ingredient
import org.apache.commons.io.FileUtils

object Main {

  /*
   * See if it works.
   */
  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      println("Usage: shop.ShoppingList <path to cookbook> <path to week-menu>")
      System.exit(-1)
    }

    val cookbookFile = args(0)
    val menuFile: String = args(1)
    val categoryFile = "data/categoryDatabase_v2.csv"
    CategoryService.config(categoryFile)
    CookBookService.config(cookbookFile)
    //TODO: move to shoppinglist
    val menuAndListOfExtras = readAndSplit(menuFile)
    val menu = Menu(menuAndListOfExtras._1)
    val extras: List[Ingredient] = shop.Ingredient.readFromText(menuAndListOfExtras._2)
    val shoppingList = new ShoppingList(menu, extras)
    val theList = shoppingList.printShoppinglistForUseWhileShopping
    println(theList)
  }

  def readAndSplit(fileName: String): (String, String) = {
    split(FileUtils.readFileToString(new File(fileName), "UTF-8"))
  }

  def split(contents: String): (String, String) = {
    if (contents.indexOf("extra") > 0) {
      val parts = contents.split("extra")
      (parts(0), parts(1))
    } else (contents, "")
  }

}
