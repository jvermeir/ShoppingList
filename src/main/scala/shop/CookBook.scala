package shop

import java.io.File

import org.apache.commons.io.FileUtils
import shop.Recipe.DummyRecipe

import scala.collection.mutable

case class CookBook(recipes: mutable.Map[String, Recipe])

object CookBookService {
  var store:CookBookStore = new CookBookStore
  def config(myStore:CookBookStore): Unit = {
    store = myStore
    store.load()
  }

  def config(cookBookFileName:String):Unit = {
    store.load(cookBookFileName)
  }

  def loadRecipes():Unit = store.load()
  def getRecipeByName(name: String): Recipe = store.getRecipeByName(name)
  def getRecipeByPrefix(prefix: String): Iterable[Recipe] = store.getRecipeByPrefix(prefix)
}

class CookBookStore {
  val cookBookFileName ="data/cookbook_v2.txt"

  lazy val recipes: mutable.Map[String, Recipe] = mutable.Map()

  def load():Unit = load(cookBookFileName)

  def load(cookBookFileName:String):Unit = {
    readFromFile(cookBookFileName)
  }

  def readFromFile(fileName:String): mutable.Map[String, Recipe] = {
    val cookBookAsText = FileUtils.readFileToString(new File(fileName), "UTF-8")
    loadFromText(cookBookAsText)
  }

  def loadFromText(cookBookAsText: String): mutable.Map[String, Recipe] = {
    val cleanedUpText = cleanUpCookBookText(cookBookAsText)
    val cookBookSplitIntoRecipes = cleanedUpText.split("\n\n")
    val listOfRecipes = cookBookSplitIntoRecipes map { Recipe(_) }
    loadFromListOfRecipes(listOfRecipes.toList)
  }

  def loadFromListOfRecipes(listOfRecipes: List[Recipe]): mutable.Map[String, Recipe] = {
    recipes.filterInPlace((_, _) => false)
    recipes ++= listOfRecipes map { recipe => recipe.name -> recipe }
  }

  // TODO: refactor to use Options?
  def getRecipeByName(recipeName:String): Recipe =
    recipes.getOrElse(recipeName, DummyRecipe)

  def getRecipeByPrefix(prefix:String): Iterable[Recipe] = {
    val searchString = prefix.toLowerCase
    recipes.view.filterKeys(_.toLowerCase.contains(searchString)).toMap.values
  }

  def cleanUpCookBookText(cookBookAsString: String): String =
    cookBookAsString.replaceAll("\t", "")
      .replaceAll(" +", " ")
      .replaceAll("(?m)^[ ]*", "")
      .replaceAll("(?m)[ ]*$", "")
      .replaceAll("(?m)\n\n\n*", "\n\n")

}
