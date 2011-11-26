package shop

import org.junit._
import Assert._

class CategoryTest {

  @Test
  def testCategoriesCanBeLoadedFromText() {
    object CategoryStore extends InMemoryCategoryStore
    val categoryRepository = CategoryStore.categoryRepository
    val zuivel = categoryRepository.getByName("zuivel")
    val brood = categoryRepository.getByName("brood")
    assertNotNull(zuivel)
    assertNotNull(brood)
    assertEquals(30, zuivel.sequence)
    assertEquals(40, brood.sequence)
  }

}
