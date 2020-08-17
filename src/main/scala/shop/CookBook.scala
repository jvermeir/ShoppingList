package shop

import scala.collection.mutable.Map
import scala.language.postfixOps
import scala.language.reflectiveCalls

/**
 * Cook book represents a list of recipes
 */
case class CookBook(config: Config) {
  lazy val cookbookStore = config.cookBookStore
  cookbookStore.reload

  val recipes: Map[String, Recipe] = cookbookStore.recipes

  def getRecipeByName(name: String): Recipe = cookbookStore.getRecipeByName(name)
  def delete(recipeToDelete: Recipe) = cookbookStore.delete(recipeToDelete)
  def update(recipe: Recipe, newRecipe: Recipe) = cookbookStore.update(recipe, newRecipe)
  def add(recipe: Recipe) = cookbookStore.add(recipe)
}

trait CookBookStore {
  lazy val recipes: Map[String, Recipe] = Map()

  def save:Unit
  def reload: Unit

  def getRecipeByName(name: String): Recipe = {
    val recipe = recipes.get(name)
    recipe.map { recipe => recipe } getOrElse (throw new PanicException("Recipe named " + name + " not found"))
  }

  def delete(recipeToDelete: Recipe) = {
    recipes.remove(recipeToDelete.name)
    save
  }

  def update(recipe: Recipe, newRecipe: Recipe) = {
    recipes.remove(recipe.name)
    recipes += (newRecipe.name -> newRecipe)
    save
  }

  def add(recipe: Recipe) {
    recipes += (recipe.name -> recipe)
    save
  }

  def loadFromText(cookBookAsText: String)(implicit config:Config): Map[String, Recipe] = {
    val cleanedUpText = cleanUpCookBookText(cookBookAsText)
    val cookBookSplitIntoRecipes = cleanedUpText.split("\n\n")
    val listOfRecipes = cookBookSplitIntoRecipes map { Recipe(_) }
    loadFromListOfRecipes(listOfRecipes.toList)
  }

  def loadFromListOfRecipes(listOfRecipes: List[Recipe]): Map[String, Recipe] = {
    recipes.retain(((k, v) => false))
    recipes ++= listOfRecipes map { case (recipe) => (recipe.name -> recipe) }
  }

  def cleanUpCookBookText(cookBookAsString: String): String =
    cookBookAsString.replaceAll("\t", "")
      .replaceAll(" +", " ")
      .replaceAll("(?m)^[ ]*", "")
      .replaceAll("(?m)[ ]*$", "")
      .replaceAll("(?m)\n\n\n*", "\n\n")
}
