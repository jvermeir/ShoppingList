package shop

import org.junit.Assert._
import org.scalatest.Spec

class ReportTest extends Spec {

  implicit object InMemoryCookbookConfig extends Config {
    lazy val cookBookStore = new InMemoryCookbookStore
    lazy val categoryStore = new InMemoryCategoryStore
  }

  def `Report finds R1 once and R2 twice` {
    val cookbook = new CookBook
    val report = Report("./data/test/menus", cookbook)
    assertEquals(1, report.recipesByName("R1").length)
    assertEquals(2, report.recipesByName("R2").length)
    assertEquals(3, report.recipesByName("R").length)
    assertEquals(0, report.recipesByName("c").length)
  }

  def `Report contains a MenuItem R2 on 01022014` {
    val cookbook = new CookBook
    val report = Report("./data/test/menus", cookbook)
    assertEquals(MenuItem("zaterdag", "R1"), report.recipesByName("R1").head)
  }
}
