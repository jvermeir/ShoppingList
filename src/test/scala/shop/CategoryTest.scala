package shop

import org.junit._
import Assert._

class CategoryTest {
  @Test(expected = classOf[PanicException])
  def testCategoryNamedTestDoesNotExistInFileBasedRepository = {
    val dummy = new CategoryClient(CategoryConfig).getByName("test")
  }

  @Test
  def testDrankenExistsInFileBasedRepository = {
    assertEquals(new Category("dranken", 10), new CategoryClient(CategoryConfig).getByName("dranken"))
  }

  @Test(expected = classOf[PanicException])
  def testCategoryNamedGroenteDoesNotExistInTestConfig = {
    val dummy = new CategoryClient(TestCategoryConfig).getByName("groente")
  }

  @Test
  def testCategoryNamedTestExistsInInMemoryRepo = {
    assertEquals(new Category("test", 10), new CategoryClient(TestCategoryConfig).getByName("test"))
  }
}

class InMemoryCategoryRepository extends CategoryRepository {
	val categories = Map[String, Category](
			"test" -> new Category("test", 10),
			"vlees" -> new Category("vlees", 15))
}

object TestCategoryConfig {
  lazy val categoryRepository = new InMemoryCategoryRepository
}
