package shop

import scala.annotation.tailrec
import org.apache.commons.io.FileUtils
import java.io.File
import scala.collection.JavaConversions._

/**
 * Cookbook represents a list of recipes
 */
class CookBook(recipes: Map [String, Recipe]) {
  def findRecipeByName(name: String): Recipe = recipes(name)

  def size: Int = recipes.size
}

object CookBook {
  /*
   * Create a cookbook from a list of strings. 
   * Each recipe is a list of consecutive <category>:<ingredient> lines where a category
   * represents an area in a shop. Both category and ingredient may contain blanks.
   * Recipes are seperated by one or more blank lines. 
   */
  def apply(cookBookAsText: String): CookBook = {
    val cookBookAsLinesOfText = List.fromArray(cookBookAsText.split("\n"))
    @tailrec def recursiveParse(cookBookAsLinesOfText: List[String], resultList: List[Recipe], recipe: List[String]): List[Recipe] = {
      cookBookAsLinesOfText match {
        case Nil => resultList
        case head :: Nil => Recipe(recipe.reverse) :: resultList
        case head :: tail =>
          {
            if (head.trim.length == 0) {
              val theRecipe: Recipe = Recipe(recipe.reverse)
              recursiveParse(cookBookAsLinesOfText.dropWhile(_.trim.length==0), theRecipe :: resultList, Nil)
            } else recursiveParse(cookBookAsLinesOfText.drop(1), resultList, head :: recipe)
          }
      }
    }
    val listOfRecipes = recursiveParse(cookBookAsLinesOfText, Nil, Nil)
    apply(listOfRecipes)
  }
  
  /*
   * Create a cookbook from a list of recipes
   */
  def apply(listOfRecipes:List[Recipe]):CookBook = {
    @tailrec def recursiveAdd(recipeList:List[Recipe], recipes: Map[String, Recipe]): Map [String, Recipe] = {
      recipeList match {
        case Nil => recipes
        case head :: tail => recursiveAdd(tail, recipes + (head.name -> head))
      }
    }
    new CookBook(recursiveAdd(listOfRecipes, Map[String, Recipe]()))
  }
  
  /*
   * Create a cookbook from a text file. See apply(String) for info.
   */
  def readFromFile(fileName:String):CookBook = {
    val cookBookAsText = FileUtils.readFileToString(new File(fileName))
    apply(cookBookAsText)
  }
}