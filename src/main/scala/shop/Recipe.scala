package shop

case class Recipe(naam: String, ingredienten: List[Ingredient]) {

}

object Recipe {
  def apply(receptAsText: String): Recipe = {
    val receptAsLinesOfText: List[String] = List.fromArray(receptAsText.split("\n"))
    apply(receptAsLinesOfText)
  }
  
  def apply(receptAsListOfLines: List[String]): Recipe = {
    val naam = receptAsListOfLines(0).split(":")(1)
    val ingredients = for (ingredient <- receptAsListOfLines) yield (Ingredient(ingredient))
    new Recipe(naam, ingredients.filter(isValidIngredientLine(_)).sort(_ < _))
  }

  def isValidIngredientLine(ingredient: Ingredient): Boolean = {
    thisIsNotADummy(ingredient) && thisIsNotANaam(ingredient)
  }

  def thisIsNotANaam(ingredient: Ingredient): Boolean = {
    ingredient.categorie.length > 0 && !ingredient.categorie.equals("naam")
  }

  def thisIsNotADummy(ingredient: Ingredient): Boolean = {
    ingredient.naam.length > 0 && !ingredient.naam.equals("dummy")
  }
}