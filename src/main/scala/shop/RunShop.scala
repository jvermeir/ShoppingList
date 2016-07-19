package shop

import java.io.File

import org.apache.commons.io.FileUtils

object RunShop {
  def main(args: Array[String]) {
    implicit object FileCategoryConfig extends Config {
      lazy val categoryDatabaseFileName = "data/test/CategoryTestDataFile.txt"
      lazy val categoryStore = new FileBasedCategoryStore(categoryDatabaseFileName)
      lazy val cookBookStore = new FileBasedCookBookStore(("dummy"))
    }
    val categories: Categories = new Categories

  val DATAFILENAME = "./data/test/CategoryTestDataFile.txt"
    val categories2: Categories = new Categories
    val contentsOfFileBeforeWrite = FileUtils.readLines(new File(DATAFILENAME), "UTF-8")
    categories2.add(Category("test", 1))
    val contentsOfFileAfterWrite = FileUtils.readLines(new File(DATAFILENAME), "UTF-8")
  }
}
