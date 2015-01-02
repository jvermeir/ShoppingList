package shop

/**
 * Ingredient represents stuff to buy in a store
 */
case class Ingredient(categoryName: String, name: String)(implicit val config: Config) extends Ordered[Ingredient] {
  val categories = new Categories
  val category = categories.getByName(categoryName)

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

  override def equals(other: Any) = other match {
    case that: Ingredient => (that canEqual this) &&
      this.category.equals(that.category) && this.name.equals(that.name)
    case _ => false
  }

  def canEqual(other: Any) = other.isInstanceOf[Ingredient]

  override def hashCode: Int =
    41 * (
      41 * (
        41 + name.hashCode
        ) + category.hashCode
      )

}

object Ingredient {
  /*
   * Create an ingredient from a <category>:<name> pair.
   */
  def readFromLine(ingredientLine: String)(implicit config:Config): Ingredient = {
    val ingredient = ingredientLine.split(":")
    ingredient.length match {
      case 2 => new Ingredient(ingredient(0).trim(), ingredient(1).trim())
      case _ => null
    }
  }

  def readFromText(ingredientsAsText: String)(implicit config:Config): List[Ingredient] = {
    ingredientsAsText.lines.toList map (readFromLine(_))
  }
}
