package shop
  
import org.specs._
import org.specs.specification._
import org.specs.runner._

class CookBookBDDSpecTest extends JUnit4(prut)
object prut extends Specification {
	val expectedResult = "Ingredients: gehakt:500:gram\ngeraspteKaas:1:zakje\nsla\ntomaat\nTacoSaus:1:pot\npaprika:3:stuks\n"
	val recipeAsText = "naam Tacos\nseizoen Zomer, Winter, HeleJaar\nbak 500 gram gehakt\nserveren met 1 zakje geraspteKaas\nserveren met sla, tomaat\ntoevoegen\n1 pot TacoSaus\nbak 3 stuks paprika\noven verwarmen op 180 graden"
	"A Taco Recipe ingredient list for '" + recipeAsText + "' should have " + expectedResult + "<<debug>>" + CookBookDSL.parseCookBook(recipeAsText.toString).findRecipesByName("Tacos").first.printIngredients + "<<debug>>"  in {
	    val cookBook:CookBook = CookBookDSL.parseCookBook(recipeAsText.toString)
	    expectedResult mustBe cookBook.findRecipesByName("Tacos").first.printIngredients
	}
}
