package shop

class TestCookBookRepository extends CookBookRepository {
  val witlofRecipe = new Recipe("Witlof met kip", List(new Ingredient("vlees", "kipfilet plakjes"),
    new Ingredient("pasta", "gezeefde tomaten"),
    new Ingredient("rijst", "rijst"),
    new Ingredient("groente", "witlof"),
    new Ingredient("zuivel", "geraspte kaas")))
  val nasiRecipe = new Recipe("Nasi", List(new Ingredient("groente", "nasi pakket"),
    new Ingredient("vlees", "kipfilet"),
    new Ingredient("sauzen", "sate saus"),
    new Ingredient("rijst", "rijst"),
    new Ingredient("rijst", "kroepoek"),
    new Ingredient("olie", "augurken"),
    new Ingredient("olie", "zilveruitjes"),
    new Ingredient("zuivel", "ei"),
    new Ingredient("zuivel", "vloeibare bakboter")))
  val simpleRecipe = new Recipe("dish1", List (new Ingredient("groente", "witlof")))
  val recipes: Map[String, Recipe] = Map("Witlof met kip" -> witlofRecipe, "Nasi" -> nasiRecipe, "dish1" -> simpleRecipe)
}

object TestCookBookConfig {
  lazy val cookBookRepository = new TestCookBookRepository
}

class StringBasedCookBook(val cookBookAsText: String) extends CookBookRepository {
  val recipes = CookBook.loadFromText(cookBookAsText)
}
