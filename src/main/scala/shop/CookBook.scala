package shop

import scala.annotation.tailrec

class CookBook(recepten: Map [String, Recipe]) {
  def findRecipe(naam: String): Recipe = recepten(naam)

  def size: Int = recepten.size
}

object CookBook {
  def apply(kookboekAsText: String): CookBook = {
    val kookboekAsLinesOfText = List.fromArray(kookboekAsText.split("\n"))
    @tailrec def recursiveParse(kookboekAsLinesOfText: List[String], resultList: List[Recipe], recept: List[String]): List[Recipe] = {
      kookboekAsLinesOfText match {
        case Nil => resultList
        case head :: Nil =>
          {
            val theRecipe: Recipe = Recipe(recept.reverse)
            theRecipe :: resultList
          }
        case head :: tail =>
          {
            if (head.trim.length == 0) {
              val theRecipe: Recipe = Recipe(recept.reverse)
              recursiveParse(kookboekAsLinesOfText.dropWhile(_.trim.length==0), theRecipe :: resultList, Nil)
            } else recursiveParse(kookboekAsLinesOfText.drop(1), resultList, head :: recept)
          }
      }
    }
    val listOfRecipeen = recursiveParse(kookboekAsLinesOfText, Nil, Nil)
    apply(listOfRecipeen)
  }
  
  def apply(receptenList:List[Recipe]):CookBook = {
    @tailrec def recursiveAdd(receptenList:List[Recipe], recepten: Map[String, Recipe]): Map [String, Recipe] = {
      receptenList match {
        case Nil => recepten
        case head :: tail => recursiveAdd(tail, recepten + (head.naam -> head))
      }
    }
    new CookBook(recursiveAdd(receptenList, Map[String, Recipe]()))
  }
}