package data

import org.joda.time.DateTime

import scala.collection.mutable

case class Category(name: String, sequence: Long) extends Ordered[Category] {
  def compare(that: Category):Int = sequence.compare(that.sequence)

  def printAsDatabaseString: String = name + ":" + sequence + "\n"
}

case class CookBook(recipes: mutable.Map[String, Recipe])
case class Ingredient(category: Category, name: String)  extends Ordered[Ingredient] {

  /*
   * Sort ingredients by category, name
   */
  def compare(that: Ingredient): Int = {
    if (that != null) {
      category match {
        case that.category => name.compare(that.name)
        case _ => category.compare(that.category)
      }
    } else 1
  }

  override def toString: String = s"${category.name}:${name}"
}

case class MenuItem (date:DateTime, dayOfWeek:String, recipe:String)
final case class Recipe(name: String, ingredients: List[Ingredient]) {
  override def toString:String = {
    val result=new StringBuilder()
    result.append("name:").append(name).append("\n")
    result.append(ingredients.foldLeft("")(_ + _.toString()+"\n"))
    result.toString()
  }
}

