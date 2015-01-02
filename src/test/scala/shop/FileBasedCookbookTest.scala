package shop

import org.junit._
import Assert._
import org.scalatest.Spec

class FileBasedCookbookTest extends Spec {
  implicit object InMemoryCookbookConfig extends Config {
    lazy val cookBookStore = new FileBasedCookBookStore("data/test/cookBookForReadFromFileScenario.txt")
    lazy val categoryStore = new FileBasedCategoryStore("data/test/categoryDatabase.csv")
  }

  def `A Cookbook can be read from a file` {
    val cookBook = new CookBook
    assertEquals(2, cookBook.recipes.size)
    assertEquals("R1", cookBook.getRecipeByName("R1").name)
    assertEquals("R2", cookBook.getRecipeByName("R2").name)
  }
}
