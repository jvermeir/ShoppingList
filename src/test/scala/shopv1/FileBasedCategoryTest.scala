package shopv1

import java.io.File

import org.apache.commons.io.FileUtils
import org.junit.Assert._
import org.scalatest.flatspec.AnyFlatSpec

class FileBasedCategoryTest extends AnyFlatSpec {

  implicit object FileCategoryConfig extends Config {
    lazy val categoryDatabaseFileName = "data/test/CategoryTestDataFile.txt"
    lazy val categoryStore = new FileBasedCategoryStore(categoryDatabaseFileName)
    lazy val cookBookStore = new InMemoryCookbookStore
  }

  val DATAFILENAME = "./data/test/CategoryTestDataFile.txt"

  def reset: Unit = {
    FileUtils.deleteQuietly(new File(DATAFILENAME))
    FileUtils.copyFile(new File("./data/test/CategoryTestDataFile.startDb"), new File(DATAFILENAME))
    FileCategoryConfig.categoryStore.reload
  }

  "Categories " should "be defined in a file" in {
    reset
    val categories: Categories = new Categories
    assertEquals(20, categories.getByName("schoonmaak").sequence)
    assertEquals(10, categories.getByName("dranken").sequence)
  }

  it should "Persist a new category" in {
    reset
    val categories: Categories = new Categories
    val contentsOfFileBeforeWrite = FileUtils.readLines(new File(DATAFILENAME), "UTF-8")
    assertEquals(2, contentsOfFileBeforeWrite.size())
    categories.add(Category("test", 1))
    val contentsOfFileAfterWrite = FileUtils.readLines(new File(DATAFILENAME), "UTF-8")
    assertEquals(3, contentsOfFileAfterWrite.size())
  }

  it should "Update the sequence number in a file" in {
    reset
    val categories: Categories = new Categories
    categories.update(Category("schoonmaak", 20), Category("schoonmaak", 12345))
    val contentsOfFileAfterWrite = FileUtils.readFileToString(new File(DATAFILENAME), "UTF-8")
    assertTrue(contentsOfFileAfterWrite.contains("12345"))
  }

  it should "delete a category from the DataFile" in {
    reset
    val categories: Categories = new Categories
    val category = categories.getByName("schoonmaak")
    val contentsOfFileBeforeWrite = FileUtils.readFileToString(new File(DATAFILENAME), "UTF-8")
    assertTrue(contentsOfFileBeforeWrite.contains("schoonmaak"))
    categories.delete(category)
    val contentsOfFileAfterWrite = FileUtils.readLines(new File(DATAFILENAME), "UTF-8")
    assertFalse(contentsOfFileAfterWrite.contains("schoonmaak"))
  }
}
