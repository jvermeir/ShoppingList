package shop

import java.io.File

import org.apache.commons.io.FileUtils

import scala.collection.mutable.Map

class FileBasedCookBookStore(fileName: String)(implicit val config:Config) extends CookBookStore {
  reload

  override def reload = {
    recipes.retain(((k, v) => false))
    recipes ++= readFromFile
  }

  def readFromFile: Map[String, Recipe] = {
    val cookBookAsText = FileUtils.readFileToString(new File(fileName), "UTF-8")
    loadFromText(cookBookAsText)
  }

  override def save = {
    val dataFile = new File(fileName)
    FileUtils.writeStringToFile(dataFile, "", "UTF-8")
    for (recipe <- recipes) {
      FileUtils.writeStringToFile(dataFile, recipe.toString, "UTF-8", true)
    }
  }
}