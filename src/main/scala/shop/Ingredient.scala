package shop

/**
 * Ingredient represents stuff to eat
 */
case class Ingredient(category: Category, name: String) extends Ordered[Ingredient] {
  def this(categoryName: String, name: String) = {
    this(Ingredient.categoryClient.getByName(categoryName), name)
  }

  /*
   * Sort ingredients by category, name
   */
  def compare(that: Ingredient) = {
    if (that != null) {
      category match {
        case that.category => name.compare(that.name)
        case _ => category.compare(that.category)
      }
    } else 1
  }

  override def toString: String = category.name + ":" + name
}

object Ingredient {
  var categoryClient = new CategoryClient(CategoryConfig)
  /*
   * Create an ingredient from a <category>:<name> pair.
   */
  def apply(ingredientLine: String): Ingredient = {
    val ingredient = ingredientLine.split(":")
    ingredient.length match {
      case 2 => new Ingredient(ingredient(0).trim(), ingredient(1).trim())
      case _ => null
    }
  }

  def readFromText(ingredientsAsText: String): List[Ingredient] = {
    ingredientsAsText.lines.toList map (Ingredient(_))
  }
}
