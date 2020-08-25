package shopv1

import org.junit._
import Assert._
import org.scalatest.flatspec.AnyFlatSpec

class FileBasedCookbookTest extends AnyFlatSpec {
  implicit object InMemoryCookbookConfig extends Config {
    lazy val cookBookStore = new FileBasedCookBookStore("data/test/cookBookForReadFromFileScenario.txt")
    lazy val categoryStore = new FileBasedCategoryStore("data/test/categoryDatabase.csv")
  }

  "A Cookbook" should "be read from a file" in {
    val cookBook = new CookBook(InMemoryCookbookConfig)
    assertEquals(2, cookBook.recipes.size)
    assertEquals("R1", cookBook.getRecipeByName("R1").name)
    assertEquals("R2", cookBook.getRecipeByName("R2").name)
  }
}
