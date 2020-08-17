package shop

object X {

  implicit object FileCategoryConfig extends Config {
    lazy val categoryStore = new FileBasedCategoryStore("data/test/categoryDatabase.csv")
    lazy val cookBookStore = new CookbookStoreForShoppingListTest
  }

  def main(args: Array[String]): Unit = {
    val cookbook = CookBook(FileCategoryConfig)
    val menuAsString =
      """Zaterdag valt op:08102011
      	zondag:Witlof met kip
      	maandag:Nasi
        """
    val menu = Menu(menuAsString, cookbook)
    val extras: List[Ingredient] = List()
    val shoppingList = new ShoppingList(menu, extras)
    println("x")
  }
}
