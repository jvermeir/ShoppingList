package shop

import org.scalatest.FunSuite
import scala.io.Source
import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator

// TODO: add parseRecipe method to ShoppingListDSL that accepts a filename or a File

class CookBookSuite extends FunSuite {
  // TODO: fix this path in log4j.properties
  PropertyConfigurator.configure("target/test-classes/log4j.properties");
  val logger:Logger = Logger.getLogger("shop");
	
  test("parse recipe") {
    val recipeAsText = "naam Tacos\nseizoen Zomer, Winter, HeleJaar\nbak 500 gram gehakt\nserveren met 1 zakje geraspteKaas\nserveren met sla, tomaat\ntoevoegen 1 pot TacoSaus\nbak 3 stuks paprika\noven verwarmen op 180 graden"
    val expectedResult = "Ingredients: gehakt:500:gram\ngeraspteKaas:1:zakje\nsla\ntomaat\nTacoSaus:1:pot\npaprika:3:stuks\n"
		
    val recipe:Recipe = CookBookDSL.parseCookBook(recipeAsText.toString).findRecipesByName("Tacos").first
    if (logger.isDebugEnabled) {
    	logger.debug("Expected:\n" + expectedResult.toString+"<<")
    	logger.debug("Actual: \n" + recipe.printIngredients+"<<")
    }
  	expect(expectedResult.toString) {recipe.printIngredients}
  }
}


