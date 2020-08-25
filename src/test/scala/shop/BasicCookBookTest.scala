package shop

import org.junit.Assert.assertEquals
import org.scalatest.flatspec.AnyFlatSpec

class CookBookTest extends AnyFlatSpec {

  CookBookService.config(new TestCookBookStore())

  "Cookbook database" should "parse a cookbook database from text" in {
    val notFound = CookBookService.getRecipeByName("not found")
    val lasagne = CookBookService.getRecipeByName("Lasagne met gehakt")
    assertEquals("dummy", notFound.name)
    assertEquals("Lasagne met gehakt", lasagne.name)
  }

  it should "properly split tekst with multiple whitespace chareacters" in {
    val stringWithDoubleBackslashN = "part1\n\npart2"
    val splitOnDoubleBackslashN = stringWithDoubleBackslashN.split("\n\n")
    assertEquals(2, splitOnDoubleBackslashN.length)
    val stringWithDoubleBackslashNAndSomeWhitespace = "part1     \n\t  \npart2"
    val splitOnDoubleBackslahNAndSomeWhitespace = stringWithDoubleBackslashN.replaceAll("""^\s+""", "").split("\n\n")
    assertEquals(2, splitOnDoubleBackslahNAndSomeWhitespace.length)

    val cleanedUpString = CookBookService.store.cleanUpCookBookText(stringWithDoubleBackslashNAndSomeWhitespace)
    assertEquals(2, cleanedUpString.split("\n\n").length)
  }

}
