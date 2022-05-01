package nl.vermeir.shopapi

import nl.vermeir.shopapi.data.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class ShopApiApplication

fun main(args: Array<String>) {
  runApplication<ShopApiApplication>(*args)
}

@RestController
class CategoryResource(val categoryService: CategoryService) {
  @GetMapping("/categories")
  fun index(): List<Category> = categoryService.findCategories()

  @PostMapping("/category")
  fun post(@RequestBody category: Category) {
    categoryService.post(category)
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

  @PostMapping("/recipe")
  fun post(@RequestBody recipe: Recipe) {
    recipeService.post(recipe)
  }
}

@RestController
class RecipeIngredientResource(val recipeIngredientService: RecipeIngredientService) {
  @GetMapping("/recipe-ingredients")
  fun index(): List<RecipeIngredient> = recipeIngredientService.findRecipeIngredients()

  @GetMapping("/recipe-ingredients/details")
  fun rid(): List<RI> = recipeIngredientService.findAllRecipeIngredients()

  @PostMapping("/recipe-ingredient")
  fun post(@RequestBody recipeIngredient: RecipeIngredient) {
    recipeIngredientService.post(recipeIngredient)
  }
}

@Service
class CategoryService(val db: CategoryRepository) {

  fun findCategories(): List<Category> = db.findAll().toList()

  fun post(category: Category){
    db.save(category)
  }
}

@Service
class IngredientService(val db: IngredientRepository) {

  fun findIngredients(): List<Ingredient> = db.findAll().toList()

  fun post(ingredient: Ingredient){
    db.save(ingredient)
  }
}

@Service
class RecipeService(val db: RecipeRepository) {

  fun findRecipes(): List<Recipe> = db.findAll().toList()

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
