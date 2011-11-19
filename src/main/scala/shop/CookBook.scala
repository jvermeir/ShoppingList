package shop

import scala.annotation.tailrec
import org.apache.commons.io.FileUtils
import java.io.File
import scala.collection.JavaConversions._

/**
 * Cook book represents a list of recipes
 */
case class CookBook(recipes: Map[String, Recipe]) {
  def findRecipeByName(name: String): Recipe = recipes(name)
  def size: Int = recipes.size

  def equals(that: CookBook): Boolean = {
    val equalSize = recipes.size == that.recipes.size
    val equalRecipes = recipes.equals(that.recipes)
    equalSize && equalRecipes
  }

}

object CookBook {
  /*
   * Create a cook book from a list of strings. 
   * Each recipe is a list of consecutive <category>:<ingredient> lines where a category
   * represents an area in a shop. Both category and ingredient may contain blanks.
   * Recipes are separated by one or more blank lines. 
   */
  def apply(cookBookAsText: String): CookBook = {
    val cleanedUpText = cleanUpCookBookText(cookBookAsText)
    val cookBookSplitIntoRecipes = List.fromArray(cleanedUpText.split("\n\n"))
    val listOfRecipes = cookBookSplitIntoRecipes map { case (recipeAsString) => Recipe(recipeAsString.split("\n") toList) }
    apply(listOfRecipes)
  }
  
  /*
   * Create a cook book from a list of recipes
   */
  def apply(listOfRecipes: List[Recipe]): CookBook = {
    val recipes = listOfRecipes map { case (recipe) => (recipe.name -> recipe) }
    new CookBook(recipes toMap)
  }

  /*
   * Create a cook book from a text file. See apply(String) for info.
   */
  def readFromFile(fileName: String): CookBook = {
    val cookBookAsText = FileUtils.readFileToString(new File(fileName))
    apply(cookBookAsText)
  }

  def cleanUpCookBookText(cookBookAsString: String): String = 
    cookBookAsString.replaceAll("\t", "")
    .replaceAll(" +", " ")
    .replaceAll("(?m)^[ ]*", "")
    .replaceAll("(?m)[ ]*$", "")
    .replaceAll("(?m)\n\n\n*","\n\n")
}