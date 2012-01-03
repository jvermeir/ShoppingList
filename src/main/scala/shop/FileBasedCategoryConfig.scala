package shop

import org.apache.commons.io.FileUtils
import java.io.File
/**
 * A FileBasedCategoryRepository loads its set of Categories from a file (fixed for the time being).
 * It uses CategoryRepository to access the list of Categories loaded from the file.
 */
class FileBasedCategoryRepository extends CategoryRepository {
  var categories = loadCategoriesFromFile
  def loadCategoriesFromFile: Map[String, Category] = {
    val categoriesAsText = FileUtils.readFileToString(new File(FileBasedCategoryConfig.categoryDatabaseFileName))
    loadCategoriesFromAString(categoriesAsText)
  }

  private def loadCategoriesFromAString(categoriesAsText: String): Map[String, Category] = {
    val categoriesFromFile = for (line <- categoriesAsText.split("\n")) yield {
      val parts = line.split(":")
      (parts(0) -> new Category(parts(0), parts(1).toLong))
    }
    categoriesFromFile.toList.toMap
  }

  def reload = categories = loadCategoriesFromFile

  def save = {
    //TODO: implement 
  }
  
  def delete = {
    // TODO: implement
  }
  
  def update = {
    // TODO: implement
  }

  def add(category: Category) = {
    categories + (category.name -> category)
    save
  }
}

/**
 * CategoryConfig is used to provide a default implementation of a CategoryRepository.
 * In this case the FileBasedCategoryRepository is used.
 * For testing purposes a different config can be used (see CategoryTest for examples).
 */
object FileBasedCategoryConfig {
  var categoryDatabaseFileName = "data/categoryDatabase.csv"
  lazy val categoryRepository = new FileBasedCategoryRepository
}

