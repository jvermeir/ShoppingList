package shop

import org.junit._
import Assert._

class IngredientTest {
  
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
