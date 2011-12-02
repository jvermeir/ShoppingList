package shop

import org.junit._
import Assert._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.Suite

class MenuTest extends Suite with BeforeAndAfterAll {

  override def beforeAll {
    Ingredient.categoryClient = new CategoryClient(SmallCategoryTestConfig)
  }

  @Test
  def testMenuCanBeLoadedFromText() {
    var categoryClient = new CategoryClient(TestCategoryConfig)
    val cookBook = CookBook("""naam:Witlof met kip
		  vlees:kipfilet plakjes
		  
		  naam:Nasi
		  groente:nasi pakket""")
    val menu = Menu.apply("""Zaterdag valt op:05112011
zaterdag:Witlof met kip
""", cookBook)
    assertEquals(1, menu.listOfRecipes.length)
  }

}
