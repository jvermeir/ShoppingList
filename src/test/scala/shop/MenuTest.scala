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
    val categoryClient = new CategoryClient(TestCategoryConfig)
    val cookbookClient = new CookBookClient(TestCookBookConfig)
    val menu = Menu.apply("""Zaterdag valt op:05112011
zaterdag:Witlof met kip
""", cookbookClient)
    assertEquals(1, menu.listOfRecipes.length)
  }

}
