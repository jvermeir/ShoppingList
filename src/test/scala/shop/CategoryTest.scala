package shop

import org.junit._
import Assert._
import org.apache.commons.io.FileUtils
import java.io.File
import scala.collection.mutable.Map

class CategoryTest {
  def getClientLoadFromTestDataFile: CategoryClient = {
    FileUtils.deleteQuietly(new File("./data/test/CategoryTestDataFile.txt"))
    FileUtils.copyFile(new File("./data/test/CategoryTestDataFile.startDb"), new File("./data/test/CategoryTestDataFile.txt"))
    FileBasedCategoryConfig.categoryDatabaseFileName = "./data/test/CategoryTestDataFile.txt"
    val client = new CategoryClient(FileBasedCategoryConfig)
    client.reload
    client
  }

  @Test(expected = classOf[PanicException])
  def testCategoryNamedTestDoesNotExistInFileBasedRepository = {
    val dummy = new CategoryClient(FileBasedCategoryConfig).getByName("test")
  }

  @Test
  def testDrankenExistsInFileBasedRepository = {
    assertEquals(new Category("dranken", 10), new CategoryClient(FileBasedCategoryConfig).getByName("dranken"))
  }

  @Test
  def testCategoryDataCanBeLoadedFromAFileAndContains2categories = {
    val cats = getClientLoadFromTestDataFile
    assertEquals(2, cats.getCategories.size)
  }

  @Test
  def testACategoryCanBeAddedToDataLoadedFromAFile = {
    val client = getClientLoadFromTestDataFile
    client.add(Category("test", 1))
    assertEquals(3, client.getCategories.size)
    assertEquals(Category("test", 1), client.getByName("test"))
  }

  @Test
  def testANewCategoryIsPersistedInADataFile = {
    val client = getClientLoadFromTestDataFile
    client.add(Category("test", 1))
    val clientAfterCategoryIsAdded =  new CategoryClient(FileBasedCategoryConfig)
    clientAfterCategoryIsAdded.reload
    assertEquals(3, clientAfterCategoryIsAdded.getCategories.size)
    val contentsOfFileAfterWrite = FileUtils.readLines(new File("./data/test/CategoryTestDataFile.txt"))
    assertEquals(3, contentsOfFileAfterWrite.size())
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
  val categories = Map[String, Category](
    "test" -> new Category("test", 10),
    "vlees" -> new Category("vlees", 15))
}

object TestCategoryConfig {
  lazy val categoryRepository = new InMemoryCategoryRepository
}
