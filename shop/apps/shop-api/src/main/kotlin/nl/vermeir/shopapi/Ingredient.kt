package nl.vermeir.shopapi

import kotlinx.serialization.Serializable
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*

@RestController
class IngredientResource(val ingredientService: IngredientService) {
  @GetMapping("/ingredients")
  fun index(): List<Ingredient> = ingredientService.findIngredients()

  @GetMapping("/ingredient/{id}")
  fun getIngredientById(@PathVariable(name = "id") id: String) = ResponseEntity.ok(ingredientService.findById(id))

  @GetMapping("/ingredient")
  fun getIngredientByName(@RequestParam(name = "name") name: String) =
    ResponseEntity.ok(ingredientService.getIngredientByName(name))

  @PostMapping("/ingredient")
  fun post(@RequestBody ingredient: Ingredient) = ResponseEntity(ingredientService.post(ingredient), HttpStatus.CREATED)
}

@Service
class IngredientService(val db: IngredientRepository) {

  fun findById(id: String): Ingredient = db.findById(id).orElseThrow { ResourceNotFoundException("Ingredient '${id}' not found") }

  fun findIngredients(): List<Ingredient> = db.findAll().toList()

  fun getIngredientByName(name: String): Ingredient =
    db.findByName(name) ?: throw ResourceNotFoundException("Ingredient '${name}' not found")

  fun post(ingredient: Ingredient):Ingredient = db.save(ingredient)

  fun deleteAll() = db.deleteAll()
}

@Table("INGREDIENTS")
data class Ingredient(@Id val id: String?, val name: String, val categoryId: String)

@Serializable
data class IngredientDetails(val recipeIngredientId: String?, val ingredientId: String?, val ingredientName:String, val categoryId: String, val categoryName:String)
interface IngredientRepository : CrudRepository<Ingredient, String> {
  @Query("SELECT * FROM ingredients WHERE name = :name")
  fun findByName(name: String): Ingredient?

  @Query(
    value = "SELECT ri.id as recipe_ingredient_id, i.name as ingredient_name, i.id as ingredient_id, c.name as category_name, c.id as category_id  FROM ingredients i, recipe_ingredients ri, categories c where ri.recipe_id = :recipeId and ri.ingredient_id = i.id and i.category_id = c.id"
  )
  fun ingredientsByRecipe(recipeId:String):List<IngredientDetails>
}

