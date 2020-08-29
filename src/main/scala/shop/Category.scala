package shop

import java.io.File

import data.Category
import org.apache.commons.io.FileUtils

import scala.collection.mutable
import scala.language.reflectiveCalls

/**
 * Category represents an area of a shop and the order of the category in the optimal route
 * through the shop.
 */

//case class Category(name: String, sequence: Long) extends Ordered[Category] {
//  def compare(that: Category):Int = sequence.compare(that.sequence)
//
//  def printAsDatabaseString: String = name + ":" + sequence + "\n"
//}

object CategoryService {
  var store:CategoryStore = new CategoryStore
  def config(myStore:CategoryStore): Unit = {
    store = myStore
    store.load()
  }

  def config(categoryFileName:String):Unit = {
    store.load(categoryFileName)
  }

  def loadCategories():Unit = store.load()

  def getCategoryByName(name: String): Category = store.getCategoryByName(name)
  def getAllCategories(): mutable.Map[String, Category] = store.categories
}

class CategoryStore {
  val categoryFileName ="data/categoryDatabase.csv"

  lazy val categories: mutable.Map[String, Category] = mutable.Map()

  def load(): Unit = load(categoryFileName)

  def load(categoryFileName:String): Unit = {
    readFromFile(categoryFileName)
  }

  def readFromFile(categoryFileName:String): mutable.Map[String, Category] = {
    val categoriesAsText = FileUtils.readFileToString(new File(categoryFileName), "UTF-8")
    loadFromText(categoriesAsText)
  }

  def loadFromText(categoriesAsText: String): mutable.Map[String, Category] = {
    categories.filterInPlace((_,_) => false)
    val cats = for (line <- categoriesAsText.split("\n")) yield {
      val parts = line.split(":")
      Category(parts(0), parts(1).toLong)
    }
    categories ++= cats map { category => category.name -> category}
  }

  def getCategoryByName(name: String): Category = {
    categories.getOrElse(name, DummyCategory)
  }

}

object DummyCategory extends Category("dummy", -1)