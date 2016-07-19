package shop

import java.io.File

import collection.JavaConverters._
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter

/**
  * Various reports about past menus
  */
case class Report(directoryThatContainsMenuFiles: String, cookBook: CookBook) {
  val menus = loadMenus()

  def loadMenus(): List[Menu] = {
    val files = FileUtils.listFiles(new File(directoryThatContainsMenuFiles), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).asScala.toArray.toList
    return files.map(Menu.readFromFile(_, cookBook))
  }

  def recipesByName(recipeName: String):List[MenuItem] = {
    val recipes = menus.map(_.menuItems).flatten
    return recipes.filter(_.recipe.contains (recipeName))
  }

}
