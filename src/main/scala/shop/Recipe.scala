package shop

case class Recipe(name: String, ingredients: List[Ingredient]) {

}

object Recipe {
  def apply(receptAsText: String): Recipe = {
    val receptAsLinesOfText: List[String] = List.fromArray(receptAsText.split("\n"))
    apply(receptAsLinesOfText)
  }
  
  def apply(recipeAsListOfLines: List[String]): Recipe = {
    val name = recipeAsListOfLines(0).split(":")(1)
    val ingredients = for (ingredient <- recipeAsListOfLines) yield (Ingredient(ingredient))
    new Recipe(name, ingredients.filter(isValidIngredientLine(_)).sort(_ < _))
  }

  def isValidIngredientLine(ingredient: Ingredient): Boolean = {
    thisIsNotADummy(ingredient) && thisIsNotAName(ingredient)
  }

  def thisIsNotAName(ingredient: Ingredient): Boolean = {
    ingredient.category.length > 0 && !ingredient.category.equals("naam")
  }

  def thisIsNotADummy(ingredient: Ingredient): Boolean = {
    ingredient.name.length > 0 && !ingredient.name.equals("dummy")
  }
}