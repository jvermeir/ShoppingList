package nl.vermeir.shopapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
class ManagementResource(
  val categoryService: CategoryService,
  val ingredientService: IngredientService,
  val recipeService: RecipeService,
  val recipeIngredientService: RecipeIngredientService
) {
  @PostMapping("/cleanup")
  fun post() {
    categoryService.deleteAll()
    ingredientService.deleteAll()
    recipeService.deleteAll()
    recipeIngredientService.deleteAll()
    println(categoryService.findCategories())
  }
}
