package shop

/**
 * Ingredient represents stuff to eat
 */
case class Ingredient(category: String, name: String) extends Ordered[Ingredient] {

  /*
   * Sort ingredients by category, name
   */
  def compare(that: Ingredient) = {
    category match {
      case that.category => name.compare(that.name)
      case _ => category.compare(that.category)
    }
  }
  override def toString: String = category + ":" + name
}

object Ingredient {
  /*
   * Create an ingredient from a <category>:<name> pair.
   */
  def apply(ingredientLine: String): Ingredient = {
    val ingredient = ingredientLine.split(":")
    ingredient.length match {
      case 2 => new Ingredient(ingredient(0).trim(), ingredient(1).trim())
      case _ => null //new Ingredient("dummy", "dummy")
    }
  }
}