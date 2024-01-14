package nl.vermeir.shopapi

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import kotlinx.serialization.Serializable
import nl.vermeir.shopapi.data.OutputIngredient
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class IngredientResource(val ingredientService: IngredientService) {
  @GetMapping("/ingredients")
  fun list(): List<Ingredient> = ingredientService.list()

  @GetMapping("/ingredient/{id}")
  fun findById(@PathVariable(name = "id") id: UUID) = ResponseEntity.ok(ingredientService.findById(id))

  @DeleteMapping("/ingredient/{id}")
  fun delete(@PathVariable(name = "id") id: UUID) = ResponseEntity.ok(ingredientService.delete(id))

  @GetMapping("/ingredient")
  fun findByName(@RequestParam(name = "name") name: String) =
    ResponseEntity.ok(ingredientService.findByName(name))

  @PostMapping("/ingredient")
  fun post(@RequestBody ingredient: Ingredient) = ResponseEntity(ingredientService.save(ingredient), HttpStatus.CREATED)

  @PutMapping("/ingredient")
  fun put(@RequestBody ingredient: Ingredient) = ResponseEntity(ingredientService.save(ingredient), HttpStatus.OK)

  @GetMapping("/ingredientsWithDetails")
  fun allWithCategoryName(): List<IngredientWithDetails> = ingredientService.allWithCategoryName()
}

@Service
class IngredientService(
  val db: IngredientRepository,
  val categoryService: CategoryService,
  val ingredientsWithDetailsRepository: IngredientWithDetailsRepository
) {
  fun list(): List<Ingredient> = db.findAll().toList()

  fun findById(id: UUID): Ingredient =
    db.findById(id).orElseThrow { ResourceNotFoundException("Ingredient '${id}' not found") }

  fun findByName(name: String): Ingredient? =
    db.findByName(name).orElseThrow { ResourceNotFoundException("Ingredient '${name}' not found") }

  fun save(ingredient: Ingredient): Ingredient = db.save(ingredient)

  fun deleteAll() = db.deleteAll()

  fun delete(id: UUID) = db.deleteById(id)

  fun toOutputIngredient(ingredient: Ingredient, unit: String, amount: Float): OutputIngredient {
    val outputCategory = categoryService.toOutputCategory(categoryService.findById(ingredient.categoryId))
    return OutputIngredient(ingredient.id.toString(), ingredient.name, outputCategory, unit, amount)
  }

  fun allWithCategoryName(): List<IngredientWithDetails> = ingredientsWithDetailsRepository.allWithCategoryName()
}

@Entity(name = "INGREDIENTS")
@Serializable
data class Ingredient(
  @Serializable(with = UUIDSerializer::class)
  @jakarta.persistence.Id @GeneratedValue
  var id: UUID? = null,
  var name: String,
  @Serializable(with = UUIDSerializer::class)
  var categoryId: UUID,
  var unit: String
)

@Entity(name = "INGREDIENTS_WITH_DETAILS")
data class IngredientWithDetails(
  @Id val id: UUID,
  val name: String,
  val unit: String,
  val categoryId: UUID,
  val categoryName: String
)

interface IngredientRepository : CrudRepository<Ingredient, UUID> {
  fun findByName(name: String): Optional<Ingredient>
}

interface IngredientWithDetailsRepository : CrudRepository<IngredientWithDetails, UUID> {
  @Query("SELECT NEW nl.vermeir.shopapi.IngredientWithDetails (i.id, i.name, i.unit, i.categoryId, c.name) FROM INGREDIENTS i, CATEGORIES c WHERE i.categoryId = c.id")
  fun allWithCategoryName(): List<IngredientWithDetails>
}
