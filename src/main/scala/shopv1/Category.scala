package shopv1

import scala.collection.mutable.Map
import scala.language.reflectiveCalls

/**
 * Category represents an area of a shop and the order of the category in the optimal route
 * through the shop.
 */
case class Category(val name: String, val sequence: Long) extends Ordered[Category] {
  def compare(that: Category) = sequence.compare(that.sequence)

  def printAsDatabaseString: String = name + ":" + sequence + "\n"
}

/*
 * Store technology agnostic representation of a set of categories.
 */
class Categories(implicit val config: Config) {
  lazy val categoryStore: CategoryStore = config.categoryStore

  def getByName(name: String): Category = categoryStore.getByName(name)

  def add(category: Category): Unit = categoryStore.add(category)

  def update(oldCategory: Category, newCategory: Category): Unit = categoryStore.update(oldCategory, newCategory)

  def delete(category: Category): Unit = categoryStore.delete(category)

  def reload: Unit = categoryStore.reload
}

/*
 * Stuff all Category stores have in common.
 */
trait CategoryStore {
  lazy val categoryMap: Map[String, Category] = Map()
  reload

  def save: Unit

  def reload: Unit

  def getByName(name: String): Category = {
    val category = categoryMap.get(name)
    category.map { category => category} getOrElse (throw new PanicException("Category named " + name + " not found"))
  }

  def delete(categoryToDelete: Category) = {
    categoryMap.remove(categoryToDelete.name)
    save
  }

  def update(categoryToUpdate: Category, newCategory: Category) = {
    categoryMap.remove(categoryToUpdate.name)
    categoryMap += (newCategory.name -> newCategory)
    save
  }

  def add(category: Category):Unit = {
    categoryMap += (category.name -> category)
    save
  }

  def loadCategoriesFromAString(categoriesAsText: String): Map[String, Category] = {
    val categoriesFromText = for (line <- categoriesAsText.split("\n")) yield {
      val parts = line.split(":")
      (parts(0) -> new Category(parts(0), parts(1).toLong))
    }
    Map(categoriesFromText.toList: _*)
  }
}
