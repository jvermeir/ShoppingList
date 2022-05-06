package nl.vermeir.shopapi

import nl.vermeir.shopapi.data.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.WebRequest




@SpringBootApplication
class ShopApiApplication

fun main(args: Array<String>) {
  runApplication<ShopApiApplication>(*args)
}

@RestController
class CategoryResource(val categoryService: CategoryService) {
  // TODO: stream output
  @GetMapping("/categories")
  fun index(): List<Category> = categoryService.findCategories()

  @PostMapping("/category")
  fun post(@RequestBody category: Category):ResponseEntity<Category> {
       return ResponseEntity(categoryService.post(category),  HttpStatus.CREATED)
  }
  @GetMapping("/category")
  fun getCategoryByName(@RequestParam(name = "name") name:String): ResponseEntity<Category> {
    return ResponseEntity.ok(categoryService.getCategoryByName(name));
  }
}

@RestController
class IngredientResource(val ingredientService: IngredientService) {
  @GetMapping("/ingredients")
  fun index(): List<Ingredient> = ingredientService.findIngredients()

  @PostMapping("/ingredient")
  fun post(@RequestBody ingredient: Ingredient) {
    ingredientService.post(ingredient)
  }
}

@RestController
class RecipeResource(val recipeService: RecipeService) {
  @GetMapping("/recipes")
  fun index(): List<Recipe> = recipeService.findRecipes()

  @GetMapping("/recipe")
  fun getByName(@RequestParam("name") name: String) : RecipeDetails = recipeService.findByName(name)

  @PostMapping("/recipe")
  fun post(@RequestBody recipe: Recipe) {
    recipeService.post(recipe)
  }
}

@RestController
class RecipeIngredientResource(val recipeIngredientService: RecipeIngredientService) {
  @GetMapping("/recipe-ingredients")
  fun index(): List<RecipeIngredient> = recipeIngredientService.findRecipeIngredients()

  @PostMapping("/recipe-ingredient")
  fun post(@RequestBody recipeIngredient: RecipeIngredient) {
    recipeIngredientService.post(recipeIngredient)
  }
}

@Service
class CategoryService(val db: CategoryRepository) {

  fun findCategories(): List<Category> = db.findAll().toList()

  fun post(category: Category): Category {
    return db.save(category)
  }

  fun getCategoryByName(name: String):Category =
    db.findByName(name) ?: throw ResourceNotFoundException("Category '${name}' not found")
}

@Service
class IngredientService(val db: IngredientRepository) {

  fun findIngredients(): List<Ingredient> = db.findAll().toList()

  fun post(ingredient: Ingredient){
    db.save(ingredient)
  }
}

@Service
class RecipeService(val db: RecipeRepository, val ingredientDb: IngredientRepository) {

  fun findRecipes(): List<Recipe> = db.findAll().toList()

  fun findByName(name:String): RecipeDetails {
    val recipe = db.findByName(name)
    val ingredients = recipe.id?.let { ingredientDb.ingredientsByRecipe(it) }
    return RecipeDetails(recipe, ingredients.orEmpty())
  }

  fun post(recipe: Recipe){
    db.save(recipe)
  }
}

@Service
class RecipeIngredientService(val db: RecipeIngredientRepository) {

  fun findRecipeIngredients(): List<RecipeIngredient> = db.findAll().toList()
  fun findAllRecipeIngredients(): List<RI> = db.findAllRecipeIngredients().toList()

  fun post(recipeIngredient: RecipeIngredient){
    db.save(recipeIngredient)
  }
}
