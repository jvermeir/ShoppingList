package shop

import org.junit.Assert._
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class BasicCategoryTest extends AnyFlatSpec with GivenWhenThen {

  implicit object InMemoryCategoryConfig extends Config {
    lazy val cookBookStore = new InMemoryCookbookStore
    lazy val categoryStore = new InMemoryCategoryStore
  }

  "Category database" should "parse a database from text" in {
      val categories = new Categories
      categories.reload
      assertEquals(20, categories.getByName("schoonmaak").sequence)
      assertEquals(10, categories.getByName("dranken").sequence)
      assertEquals(123, categories.getByName("OneMore").sequence)
    }

    it should "ensure unique category names" in {
      val category20 = Category("schoonmaak", 20)
      val category30 = Category("schoonmaak", 30)
      val categories = new Categories
      categories.add(category20)
      categories.add(category30)
      assertEquals(category30, categories.getByName("schoonmaak"))
    }

    it should "make sure a category named Groente does not exist" in {
      val categories = new Categories
      categories.reload
      intercept[PanicException] {
        val dummy = categories.getByName("groente")
      }
    }
}
