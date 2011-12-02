package shop
import org.apache.commons.io.FileUtils
import java.io.File
import shop._

/**
 * Category represents an area of a shop and the order of the category in the optimal route
 * through the shop.
 */
case class Category(val name: String, val sequence: Int) extends Ordered[Category] {
  /* 
   * Categories are considered equal if their sequences are equal.
   */
  def compare(that: Category) = sequence.compare(that.sequence)
}

/**
 * A CategoryRepository contains basic functions to search in a set of Category instances.
 */
trait CategoryRepository {
  val categories: Map[String, Category]
  def getByName(name: String): Category = {
    val category = categories.get(name)
    category match {
      case Some(category) => category
      case _ => throw new PanicException("Category named " + name + " not found")
    }
  }
}

/**
 * A FileBasedCategoryRepository loads its set of Categories from a file (fixed for the time being).
 * It uses CategoryRepository to access the list of Categories loaded from the file. 
 */
class FileBasedCategoryRepository extends CategoryRepository {
  val categories = loadCategoriesFromFile("data/categoryDatabase.csv")

  def loadCategoriesFromFile(fileName: String): Map[String, Category] = {
    val categoriesAsText = FileUtils.readFileToString(new File(fileName))
    loadCategoriesFromAString(categoriesAsText)
  }

  private def loadCategoriesFromAString(categoriesAsText: String): Map[String, Category] = {
    val categoriesFromFile = for (line <- categoriesAsText.split("\n")) yield {
      val parts = line.split(":")
      (parts(0) -> new Category(parts(0), new Integer(parts(1))))
    }
    categoriesFromFile.toList.toMap
  }
} 

/**
 * A CategoryClient is given a CategoryRepository. It knows how to access service methods
 * of a repository, getByName in this case. It Client delegates to Repository. 
 */
class CategoryClient(env: { val categoryRepository: CategoryRepository }) {
  def getByName(name: String): Category = env.categoryRepository.getByName(name)
}

/**
 * CategoryConfig is used to provide a default implementation of a CategoryRepository.
 * In this case the FileBasedCategoryRepository is used. 
 * For testing purposes a different config cat be used (see CategoryTest). 
 */
object CategoryConfig {
  lazy val categoryRepository = new FileBasedCategoryRepository
}

