package shop
import scala.collection.mutable.Map

/**
 * Category represents an area of a shop and the order of the category in the optimal route
 * through the shop.
 */
case class Category(val name: String, val sequence: Long) extends Ordered[Category] {
  /* 
   * Categories are considered equal if their names are equal.
   */
  def compare(that: Category) = sequence.compare(that.sequence)

  def printAsDatabaseString: String = name + ":" + sequence + "\n"
}

/**
 * A CategoryRepository contains basic functions to search in a set of Category instances.
 */
trait CategoryRepository {
  val categories: Map[String, Category]

  def getByName(name: String): Category = {
    val category = categories.get(name)
    category.map { category => category } getOrElse (throw new PanicException("Category named " + name + " not found"))
  }
  
  def add(category: Category): Unit = throw new shop.OperationNotSupportedException("add operation not supported")
  def update(oldCategory:Category, newCategory: Category): Unit = throw new shop.OperationNotSupportedException("update operation not supported")
  def delete(category: Category): Unit = throw new shop.OperationNotSupportedException("delete operation not supported")
  def reload: Unit = throw new shop.OperationNotSupportedException("reload operation not supported")
}
/**
 * A CategoryClient is given a CategoryRepository. It knows how to access service methods
 * of a repository. Client delegates to Repository.
 */
class CategoryClient(env: { val categoryRepository: CategoryRepository }) {
  def getByName(name: String): Category = env.categoryRepository.getByName(name)
  def getCategories: Map[String, Category] = env.categoryRepository.categories.clone
  def add(category: Category) = env.categoryRepository.add(category)
  def update(oldCategory:Category, newCategory: Category) = env.categoryRepository.update(oldCategory, newCategory)
  def delete(category: Category) = env.categoryRepository.delete(category)
  def reload = env.categoryRepository.reload
}

