package shop
import org.apache.commons.io.FileUtils
import java.io.File
import scala.collection.JavaConversions._
import scala.language.postfixOps
import scala.language.reflectiveCalls

/**
 * Cook book represents a list of recipes
 */
case class CookBook(recipes: Map[String, Recipe]) {
  def size: Int = recipes.size

  def equals(that: CookBook): Boolean = {
    val equalSize = recipes.size == that.recipes.size
    val equalRecipes = recipes.equals(that.recipes)
    equalSize && equalRecipes
  }
}

/**
 * A CookBookRepository contains basic functions to search for Recipes in a CookBook.
 */
trait CookBookRepository {
  var recipes: Map[String, Recipe]
  def getRecipeByName(name: String): Recipe = {
    val recipe = recipes.get(name)
    recipe match {
      case Some(recipe) => recipe
      case _ => throw new PanicException("Recipe named " + name + " not found")
    }
  }
}

class FileBasedCookBookRepository extends CookBookRepository {
  //TODO: why is this cookbook initialize here? this means it may be init'd twice if we decide to load a different file
  var recipes = CookBook.readFromFile("data/cookbook.txt")
  def readFromFile(fileName: String) = {
    recipes = CookBook.readFromFile(fileName)
  }
}

object CookBook {
  def readFromFile(fileName: String): Map[String, Recipe] = {
    val cookBookAsText = FileUtils.readFileToString(new File(fileName))
    loadFromText(cookBookAsText)
  }

  def loadFromText(cookBookAsText: String): Map[String, Recipe] = {
    val cleanedUpText = CookBook.cleanUpCookBookText(cookBookAsText)
    val cookBookSplitIntoRecipes = (cleanedUpText.split("\n\n")).toList
    val listOfRecipes = cookBookSplitIntoRecipes map { case (recipeAsString) => Recipe(recipeAsString.lines.toList) }
    loadFromListOfRecipes(listOfRecipes)
  }

  def loadFromListOfRecipes(listOfRecipes: List[Recipe]): Map[String, Recipe] = {
    val recipes = listOfRecipes map { case (recipe) => (recipe.name -> recipe) }
    recipes toMap
  }

  def cleanUpCookBookText(cookBookAsString: String): String =
    cookBookAsString.replaceAll("\t", "")
      .replaceAll(" +", " ")
      .replaceAll("(?m)^[ ]*", "")
      .replaceAll("(?m)[ ]*$", "")
      .replaceAll("(?m)\n\n\n*", "\n\n")
}

/**
 * A CookBookClient is given a CookBookRepository. It knows how to access service methods
 * of a repository, getByName in this case. Client delegates to Repository.
 */
class CookBookClient(env: { val cookBookRepository: CookBookRepository }) {
  def getRecipeByName(name: String): Recipe = env.cookBookRepository.getRecipeByName(name)
}

/**
 * CookBookConfig is used to provide a default implementation of a CookBookRepository.
 * In this case the FileBasedCookBookRepository is used.
 */
object CookBookConfig {
  lazy val cookBookRepository = new FileBasedCookBookRepository
  def reload(fileName: String) = { cookBookRepository.readFromFile(fileName) }
}

