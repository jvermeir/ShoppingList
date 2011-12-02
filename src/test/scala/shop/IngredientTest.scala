package shop

import org.scalatest._
import org.junit._
import Assert._

class IngredientTest extends Suite with BeforeAndAfterAll {

  override def beforeAll {
    Ingredient.categoryClient = new CategoryClient(SmallCategoryTestConfig)
  }

  @Test
  def testSortOrderIsOK() {
    val sla = new Ingredient("groente", "sla")
    val gezeefdeTomaten = new Ingredient("pasta", "gezeefde tomaten")
    assertTrue(sla < gezeefdeTomaten)
    assertFalse(gezeefdeTomaten < sla)
    val spinazie = new Ingredient("groente", "spinazie")
    assertTrue(sla < spinazie)
  }

  @Test
  def testNullCategoryIsLessThanOtherCategories() {
    val sla = new Ingredient("groente", "sla")
    assertTrue(sla > null)
  }

}
