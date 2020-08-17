package shop

/**
 * A recipe is the name and a list of ingredients of something to eat.
 */
case class Recipe(name: String, ingredients: List[Ingredient])(implicit val config: Config) {
  override def toString:String = {
    val result=new StringBuilder()
    result.append("name:").append(name).append("\n")
    result.append(ingredients.foldLeft("")(_ + _.toString()+"\n"))
    result.toString()
  }
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
   */
  // TODO: do we need the implicit here?
  def apply(recipeAsText: String)(implicit config: Config): Recipe = {
    val receptAsLinesOfText = recipeAsText.split("\n").toList
    apply(receptAsLinesOfText)
  }
  
  /* Create a recipe from a list of strings. See comment for apply(String). */
  def apply(recipeAsListOfLines: List[String])(implicit config: Config): Recipe = {
    val name = recipeAsListOfLines(0).split(":")(1)
    val ingredientLines = recipeAsListOfLines.drop(1)
    val ingredients = for (ingredientLine <- ingredientLines) yield (Ingredient.readFromLine(ingredientLine))
    new Recipe(name, ingredients.filter(isValidIngredientLine(_)).sortWith(_ < _))
  }

  def isValidIngredientLine(ingredient: Ingredient): Boolean = {
    ingredientIsNotEmpty(ingredient) && thisIsNotAName(ingredient)
  }

  // TODO: funny, this...
  def thisIsNotAName(ingredient: Ingredient): Boolean = {
    ingredient.category.name.length() > 0 && !ingredient.category.equals("naam")
  }

  def ingredientIsNotEmpty(ingredient: Ingredient): Boolean = {
    ingredient != null && ingredient.name.length > 0 
  }

}