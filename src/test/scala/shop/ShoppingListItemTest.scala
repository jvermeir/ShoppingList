package shop

import org.junit._
import Assert._
import org.joda.time.DateTime

class ShoppingListItemTest {

  @Test
  def testSortOrderIsOKIfIngredientsBelongToDifferentCategory() {
    val time = new DateTime(2011, 10, 9, 0, 0)
    val sla = ShoppingListItem(new Ingredient("groente", "sla"), time)
    val gezeefdeTomaten = ShoppingListItem(new Ingredient("pasta", "gezeefde tomaten"), time)
    assertTrue(sla < gezeefdeTomaten)
  }

  @Test
  def testSortOrderIsOKIfIngredientsBelongToSameCategory() {
    val time = new DateTime(2011, 10, 9, 0, 0)
    val sla = ShoppingListItem(new Ingredient("groente", "sla"), time)
    val gezeefdeTomaten = ShoppingListItem(new Ingredient("groente", "aaatomaten"), time)
    assertTrue(sla > gezeefdeTomaten)
  }

  @Test
  def testSortOrderIsOKForAList() {
    val time = new DateTime(2011, 10, 9, 0, 0)
    val sla = ShoppingListItem(new Ingredient("groente", "sla"), time)
    val tomaten = ShoppingListItem(new Ingredient("groente", "aaatomaten"), time)
    val gezeefdeTomaten = ShoppingListItem(new Ingredient("pasta", "gezeefde tomaten"), time)
    val result = List(tomaten, sla, gezeefdeTomaten)
    val testList = List(sla, tomaten, gezeefdeTomaten).sort(_ < _)
    assertEquals(result, testList)
  }

}
