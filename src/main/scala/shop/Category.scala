package shop

/**
 * Category represents an area of a shop and the order of the category in the optimal route
 * through the shop.
 */
case class Category(val name: String, val sequence: Long) extends Ordered[Category] {
  /* 
   * Categories are considered equal if their sequences are equal.
   */
  def compare(that: Category) = sequence.compare(that.sequence)
}

/**
 * A CategoryRepository contains basic functions to search in a set of Category instances.
 */
trait CategoryRepository {
  var categories: Map[String, Category]
  def getByName(name: String): Category = {
    val category = categories.get(name)
    category match {
      case Some(category) => category
      case _ => throw new PanicException("Category named " + name + " not found")
    }
  }

  //TODO: Make up your mind, this one or the getByName version?
  def getByName2(name: String): Category = {
    val category = categories.get(name)
    category.map { category => category } getOrElse (throw new PanicException("Category named " + name + " not found"))
  }
  def add(category: Category)
}
/**
 * A CategoryClient is given a CategoryRepository. It knows how to access service methods
 * of a repository. Client delegates to Repository.
 */
class CategoryClient(env: { val categoryRepository: CategoryRepository }) {
  def getByName(name: String): Category = env.categoryRepository.getByName(name)
  // TODO: return a copy because the list of categories may change if the repo is file based. 
  def getCategories: Map[String, Category] = env.categoryRepository.categories
  def add(category: Category) = env.categoryRepository.add(category)
}

