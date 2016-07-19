package shop

import java.io.File

import org.apache.commons.io.FileUtils
import org.junit._
import Assert._
import org.scalatest.Spec

class FileBasedCategoryTest extends Spec {

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

  def `Category Database Can Be Loaded From File` {
    reset
    val categories: Categories = new Categories
    assertEquals(20,categories.getByName("schoonmaak").sequence)
    assertEquals(10,categories.getByName("dranken").sequence)
  }

  def `A New Category Is Persisted In A DataFile` {
    reset
    val categories: Categories = new Categories
    val contentsOfFileBeforeWrite = FileUtils.readLines(new File(DATAFILENAME), "UTF-8")
    assertEquals(2, contentsOfFileBeforeWrite.size())
    categories.add(Category("test", 1))
    val contentsOfFileAfterWrite = FileUtils.readLines(new File(DATAFILENAME), "UTF-8")
    assertEquals(3, contentsOfFileAfterWrite.size())
  }

  def `Sequence Number Is Updated In File` {
    reset
    val categories: Categories = new Categories
    categories.update(Category("schoonmaak", 20),Category("schoonmaak", 12345))
    val contentsOfFileAfterWrite = FileUtils.readFileToString(new File(DATAFILENAME), "UTF-8")
    assertTrue(contentsOfFileAfterWrite.contains("12345"))
  }

  def `A Category Is Deleted From The DataFile` {
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
