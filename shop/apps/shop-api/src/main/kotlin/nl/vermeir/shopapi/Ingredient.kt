package nl.vermeir.shopapi

import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class IngredientResource(val ingredientService: IngredientService) {
  @GetMapping("/ingredients")
  fun index(): List<Ingredient> = ingredientService.findIngredients()

  @PostMapping("/ingredient")
  fun post(@RequestBody ingredient: Ingredient) = ResponseEntity(ingredientService.post(ingredient), HttpStatus.CREATED)
}

@Service
class IngredientService(val db: IngredientRepository) {

  fun findIngredients(): List<Ingredient> = db.findAll().toList()

  fun post(ingredient: Ingredient) = db.save(ingredient)

  fun deleteAll() = db.deleteAll()
}

@Serializable
@Table("INGREDIENTS")
data class Ingredient(@Id val id: String?, val name: String, val categoryId: String)

@Serializable
data class IngredientDetails(val recipeIngredientId: String?, val ingredientId: String?, val ingredientName:String, val categoryId: String, val categoryName:String)
interface IngredientRepository : CrudRepository<Ingredient, String> {
  @Query(
    value = "SELECT ri.id as recipe_ingredient_id, i.name as ingredient_name, i.id as ingredient_id, c.name as category_name, c.id as category_id  FROM ingredients i, recipe_ingredients ri, categories c where ri.recipe_id = :recipeId and ri.ingredient_id = i.id and i.category_id = c.id"
  )
  fun ingredientsByRecipe(recipeId:String):List<IngredientDetails>
}

