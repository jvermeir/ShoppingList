package shop

import org.junit._
import Assert._

class CategoryTest {

  @Test
  def testCategoriesCanBeLoadedFromText() {
    Category.loadCategoriesFromAString("""zuivel:30
brood:40""")
    val zuivel = Category.getByName("zuivel")
    val brood = Category.getByName("brood")
    assertNotNull(zuivel)
    assertNotNull(brood)
    assertEquals(30, zuivel.sequence)
    assertEquals(40, brood.sequence)
  }

}
