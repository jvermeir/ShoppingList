package shop

import org.apache.commons.io.FileUtils
import java.io.File
import scala.collection.mutable.Map
/**
 * A FileBasedCategoryStore loads its set of Categories from a file.
 */
class FileBasedCategoryStore(categoryDatabaseFileName:String) extends CategoryStore {

  reload

  def loadCategoriesFromFile: Map[String, Category] = {
    val categoriesAsText = FileUtils.readFileToString(new File(categoryDatabaseFileName), "UTF-8")
    loadCategoriesFromAString(categoriesAsText)
  }

  // TODO: Is Map concurrent-proof?
  // TODO: find better way to add all elements of new map to old map.
  override def reload: Unit = {
    categoryMap.filterInPlace(((k, v) => false))
    for (category <- loadCategoriesFromFile) { categoryMap += category }
  }

  override def save = {
    val dataFile = new File(categoryDatabaseFileName)
    FileUtils.writeStringToFile(dataFile, "", "UTF-8")
    for (category <- categoryMap) {
      FileUtils.writeStringToFile(dataFile, category._2.printAsDatabaseString, "UTF-8", true)
    }
  }

}
