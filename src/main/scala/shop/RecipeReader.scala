package shop

import scala.io.Source

object RecipeReader {
  def main(args:Array[String]) : Unit = {
		val recipeAsString:String = Util.readFile(args(0))
   
    println("parsing: " +recipeAsString)
    try {
		  val recipe:Recipe = CookBookDSL.parseCookBook(recipeAsString).getListOfRecipes.first
			println ("parsed: "+recipe)
			println ("--"+recipe.printIngredients+"--")
			println (recipe.printIngredients == "Ingredients: gehakt:500:gram\ngeraspteKaas:1:zakje\nsla\ntomaat\nTacoSaus:1:pot\npaprika:3:stuks\n")
		} catch {
			case ex:ParseErrorException => println ("Error parsing recipe: " + ex.getMessage)
		}
	}	
}
