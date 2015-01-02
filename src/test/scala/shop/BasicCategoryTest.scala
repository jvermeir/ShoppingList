package shop

import org.junit._
import Assert._
import org.scalatest.Spec

class BasicCategoryTest extends Spec {

  implicit object InMemoryCategoryConfig extends Config {
    lazy val cookBookStore = new InMemoryCookbookStore
    lazy val categoryStore = new InMemoryCategoryStore
  }

  def `Category Database Can Be Parsed From Text` {
    val categories = new Categories
    categories.reload
    assertEquals(20,categories.getByName("schoonmaak").sequence)
    assertEquals(10,categories.getByName("dranken").sequence)
    assertEquals(123,categories.getByName("OneMore").sequence)
  }

  def `Category Name Is Unique` {
    val category20 = Category("schoonmaak", 20)
    val category30 = Category("schoonmaak", 30)
    val categories = new Categories
    categories.add(category20)
    categories.add(category30)
    assertEquals(category30, categories.getByName("schoonmaak"))
  }

  def `Category Named Groente Does Not Exist` {
    val categories = new Categories
    categories.reload
    intercept[PanicException] {
      val dummy = categories.getByName("groente")
    }
  }
}

