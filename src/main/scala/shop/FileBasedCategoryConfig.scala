package shop

import org.apache.commons.io.FileUtils
import java.io.File
import scala.collection.mutable.Map
/**
 * A FileBasedCategoryRepository loads its set of Categories from a file.
 */
class FileBasedCategoryRepository extends CategoryRepository {
  val categories = loadCategoriesFromFile
  def loadCategoriesFromFile: Map[String, Category] = {
    val categoriesAsText = FileUtils.readFileToString(new File(FileBasedCategoryConfig.categoryDatabaseFileName))
    loadCategoriesFromAString(categoriesAsText)
  }

  private def loadCategoriesFromAString(categoriesAsText: String): Map[String, Category] = {
    val categoriesFromFile = for (line <- categoriesAsText.split("\n")) yield {
      val parts = line.split(":")
      (parts(0) -> new Category(parts(0), parts(1).toLong))
    }
    Map(categoriesFromFile.toList: _*)
  }

  // TODO: Is Map concurrent-proof?
  override def reload(): Unit = {
    categories.retain(((k, v) => false))
    for (category <- loadCategoriesFromFile) { categories += category }
  }

  def save = {
    val dataFile = new File(FileBasedCategoryConfig.categoryDatabaseFileName)
    FileUtils.writeStringToFile(dataFile, "")
    for (category <- categories) {
      FileUtils.writeStringToFile(dataFile, category._2.printAsDatabaseString, true)
    }
  }

  override def delete(categoryToDelete: Category) = {
    categories.remove(categoryToDelete.name)
    save
  }

  override def update(categoryToUpdate: Category, newCategory: Category) = {
    categories.remove(categoryToUpdate.name)
    categories += (newCategory.name -> newCategory)
    save
  }

  override def add(category: Category) {
    categories += (category.name -> category)
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

