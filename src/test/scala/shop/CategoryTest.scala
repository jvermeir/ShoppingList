package shop

import org.junit._
import Assert._

class CategoryTest {
  @Test(expected = classOf[PanicException])
  def testCategoryNamedTestDoesNotExistInFileBasedRepository = {
    val dummy = new CategoryClient(FileBasedCategoryConfig).getByName("test")
  }

  @Test
  def testDrankenExistsInFileBasedRepository = {
    assertEquals(new Category("dranken", 10), new CategoryClient(FileBasedCategoryConfig).getByName("dranken"))
  }

  @Test
  def testFileNameCanBeChangedInFileBasedCategoryRepository = {
	  
  }
  
  @Test(expected = classOf[PanicException])
  def testCategoryNamedGroenteDoesNotExistInTestConfig = {
    val dummy = new CategoryClient(TestCategoryConfig).getByName("groente")
  }

  @Test
  def testCategoryNamedTestExistsInInMemoryRepo = {
    assertEquals(new Category("test", 10), new CategoryClient(TestCategoryConfig).getByName("test"))
  }
  
  @Test
  def testListOfCategoriesContainsTwoItems = {
    assertEquals(2, new CategoryClient(TestCategoryConfig).getCategories.size)
  }
}

class InMemoryCategoryRepository extends CategoryRepository {
	var categories = Map[String, Category](
			"test" -> new Category("test", 10),
			"vlees" -> new Category("vlees", 15))
	def add(category:Category) = throw new shop.OperationNotSupportedException("InMemoryCategoryRepository does not support add operation")
}

object TestCategoryConfig {
  lazy val categoryRepository = new InMemoryCategoryRepository
}
