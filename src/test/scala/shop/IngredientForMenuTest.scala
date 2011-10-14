package shop

import org.junit._
import Assert._
import org.joda.time.DateTime

class IngredientForMenuTest {

  @Test 
  def testSortOrderIsOKIfIngredientsBelongToDifferentCategory() {
    val time = new DateTime(2011, 10, 9, 0, 0)
    val sla = IngredientForMenu(Ingredient("groente", "sla"),time)
    val gezeefdeTomaten = IngredientForMenu(Ingredient("saus","gezeefde tomaten"), time)
    assertTrue(sla < gezeefdeTomaten)
  }

  @Test 
  def testSortOrderIsOKIfIngredientsBelongToSameCategory() {
    val time = new DateTime(2011, 10, 9, 0, 0)
    val sla = IngredientForMenu(Ingredient("groente", "sla"),time)
    val gezeefdeTomaten = IngredientForMenu(Ingredient("groente","aaatomaten"), time)
    assertTrue(sla > gezeefdeTomaten)
  }

  @Test 
  def testSortOrderIsOKForAList() {
    val time = new DateTime(2011, 10, 9, 0, 0)
    val sla = IngredientForMenu(Ingredient("groente", "sla"),time)
    val tomaten = IngredientForMenu(Ingredient("groente","aaatomaten"), time)
    val gezeefdeTomaten = IngredientForMenu(Ingredient("blik","gezeefde tomaten"), time)
    val result = List(gezeefdeTomaten, tomaten, sla)
    val testList = List(sla, tomaten, gezeefdeTomaten).sort(_ < _)
    assertEquals(result, testList)
  }

}
