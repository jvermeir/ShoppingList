package shop

/**
 * A recipe is the name and a list of ingredients of something to eat.
 */
case class Recipe(name: String, ingredients: List[Ingredient]) {

}

object Recipe {
  /*
   * Create a recipe from a string. A recipe is list of text lines where the first line represents the name of
   * the recipe formatted as:
   *    name:<name of the recipe>
   * name is a constant and <name of the recipe> is an arbitrary string.
   * The lines following the name line refer to ingredients. Each ingredient is formatted as:
   *    <category>:<name>
   * where <category> refers to some label that makes sense to locate the ingredient in a shop and name is the name of the ingredient.
   * 
   */
  def apply(receptAsText: String): Recipe = {
    val receptAsLinesOfText: List[String] = List.fromArray(receptAsText.split("\n"))
    apply(receptAsLinesOfText)
  }
  
  /*
   * Create a recipe from a list of strings. See comment for apply(String).
   */
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

  // TODO: do we still need this? Dummy sounds a bit dumb...
  def thisIsNotADummy(ingredient: Ingredient): Boolean = {
    ingredient.name.length > 0 && !ingredient.name.equals("dummy")
  }
}