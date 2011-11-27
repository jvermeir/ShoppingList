package shop
import org.apache.commons.io.FileUtils
import java.io.File

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

trait CategoryRepositoryComponent {
  val categories = loadCategoriesFromFile("data/categoryDatabase.csv")
  val categoryRepository: CategoryRepository
  class CategoryRepository {
    def getByName(name: String): Category = {
      // TODO: remove nasty code duplication (from InMemoryCategoryStore)
      val category = categories.get(name)
      category match {
        case Some(category) => category
        case _ => throw new PanicException("Category named " + name + " not found")
      }
    }
  }

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

trait CategoryServiceComponent { this: CategoryRepositoryComponent =>
  val categoryService: CategoryService
  class CategoryService {
    def getByName(name: String): Category = categoryRepository.getByName(name)
  }
}

object CategoryComponentRegistry extends CategoryServiceComponent with CategoryRepositoryComponent {
  val categoryRepository = new CategoryRepository
  val categoryService = new CategoryService
}
