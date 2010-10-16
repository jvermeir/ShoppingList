package shop
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Buffer
import Seasons._

class CookBook {
   private val recipes:Buffer[Recipe] = new ListBuffer[Recipe]
  def addRecipes(recipeAsText:String):CookBook = {
    addRecipesFromCookBook(CookBookDSL.parseCookBook(recipeAsText))
    this 
  }
  def addRecipesFromCookBook(cookBook:CookBook):CookBook = {
    recipes.++(cookBook.getListOfRecipes)
    this
  }
  def addRecipe(recipe:Recipe):CookBook = {
    recipes.+(recipe)
    this
  }
  def addListOfRecipes(newRecipes:List[Recipe]) = {
    recipes.++(newRecipes)
    this
  }
  def getListOfRecipes:Seq[Recipe] = recipes
  
  def findRecipesByName(name:String):Seq[Recipe] = {
    recipes.filter(_.name == name)
  }
  def findRecipesBySeason(season:Seasons):Seq[Recipe] = {    
    recipes.filter(_.seasons.contains(season))
  }
  def this(cookBookFileName:String) {
    this()
    addRecipes(Util.readFromStream(cookBookFileName))
  }
  def numberOfRecipes:Int = recipes.length
  override def toString:String = {
    val cookBookAsString:StringBuilder = new StringBuilder
    for (recipe <- recipes) {
      cookBookAsString.append(recipe)
    }
    cookBookAsString.toString
  }
}

