package shop

import org.junit._
import Assert._

class IngredientTest {

  @Test 
  def testSortOrderIsOK() {
    val sla = Ingredient("groente", "sla")
    val gezeefdeTomaten = Ingredient("saus","gezeefde tomaten")
    assertTrue(sla < gezeefdeTomaten)
    assertFalse(gezeefdeTomaten<sla)
  }

}
