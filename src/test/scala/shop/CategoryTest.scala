package shop

import org.junit._
import Assert._

class CategoryTest extends CategoryTestingEnvironment{

  @Test
  def testCategoriesCanBeLoadedFromText() {
    val zuivel = categoryService.getByName("zuivel")
    val brood = categoryService.getByName("brood")
    assertNotNull(zuivel)
    assertNotNull(brood)
    assertEquals(30, zuivel.sequence)
    assertEquals(40, brood.sequence)
  }

}
