import nl.vermeir.shopapi.*

fun main(args: Array<String>) {
  if (args.contains("debug")) logEnabled = true
  cleanUpDatabase()
  loadTestData()
  readTestData()
}

var recipe1: Recipe = Recipe("", "dummy", false)

fun readTestData() {
  println(load<List<Category>>("categories"))
  println(load<List<Ingredient>>("ingredients"))
  println(load<List<Recipe>>("recipes"))
  println(load<List<RecipeIngredient>>("recipe-ingredients"))
  println(load<RecipeIngredients>("recipe-details", listOf(Pair("id", recipe1.id))))
  println(load<Recipe>("recipe", listOf(Pair("id", recipe1.id))))
}

fun loadTestData() {
  val cat1 = save(Category(id = null, name = "cat1", shopOrder = 10), path = "category")
  println(cat1)
  val cat2 = save(Category(id = null, name = "cat2", shopOrder = 20), path = "category")
  val ing1 = save(Ingredient(id = null, name = "cat1ing1", cat1.id.orEmpty()), path = "ingredient")
  val ing2 = save(Ingredient(id = null, name = "cat1ing2", cat1.id.orEmpty()), path = "ingredient")
  val ing3 = save(Ingredient(id = null, name = "cat2ing1", cat2.id.orEmpty()), path = "ingredient")
  val ing4 = save(Ingredient(id = null, name = "cat2ing2", cat2.id.orEmpty()), path = "ingredient")
  recipe1 = save(Recipe(id = null, name = "recipe1", false), path = "recipe")
  val favoriteRecipe = save(Recipe(id = null, name = "favorite recipe", true), path = "recipe")
  save(RecipeIngredient(id = null, recipe1.id.orEmpty(), ing1.id.orEmpty()), path = "recipe-ingredient")
  save(RecipeIngredient(id = null, recipe1.id.orEmpty(), ing2.id.orEmpty()), path = "recipe-ingredient")
  save(RecipeIngredient(id = null, favoriteRecipe.id.orEmpty(), ing3.id.orEmpty()), path = "recipe-ingredient")
  save(RecipeIngredient(id = null, favoriteRecipe.id.orEmpty(), ing4.id.orEmpty()), path = "recipe-ingredient")
//
//  val recipeIngredients = RecipeIngredients(Recipe(id=null, name="r1", true), listOf(IngredientDetails(recipeIngredientId = null, ingredientId = null, "newIng1","${cat1.id}", categoryName = "${cat1.name}" )) )
//  val encoded = Json.encodeToString(recipeIngredients)
//  val (_, response, result) = "${baseUrl}/recipe-details".httpPost().jsonBody(encoded)
//    .responseString()
//  println("recipe details: ${result}: ${response}")
//  val rdNew = Json.decodeFromString<RecipeIngredients>(result.get())
//  println("rdNew: ${rdNew}")
}
