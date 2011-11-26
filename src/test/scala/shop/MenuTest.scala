package shop

import org.junit._
import Assert._
// Category

class MenuTest {

  @Test
  def testMenuCanBeLoadedFromText() {
    val cookBook = CookBook("""naam:Witlof met kip
		  vlees:kipfilet plakjes
		  
		  naam:Nasi
		  groente:nasi pakket""")
    val menu = Menu.apply("""Zaterdag valt op:05112011
zaterdag:Witlof met kip
""", cookBook)
    assertEquals(1, menu.listOfRecipes.length)
  }

}
