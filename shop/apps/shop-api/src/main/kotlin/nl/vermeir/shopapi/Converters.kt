package nl.vermeir.shopapi

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Path
import java.util.*

data class ConverterResult(val count: Int)

data class RecipeIngredients(val recipe: Recipe, val ingredients: List<Ingredient>)

@RestController
class ConverterResource(
  val categoryService: CategoryService,
  val ingredientService: IngredientService,
  val recipeService: RecipeService,
  val recipeIngredientService: RecipeIngredientService
) {
  @Value("\${dataFolderPath}")
  private val dataFolderPath: String = "data/production"

  @PostMapping("/converters/categories/{datafileName}")
  fun convertCategories(@PathVariable(name = "datafileName") datafileName: String): ConverterResult {
    val fileName = datafileName.split("/").last()
    val fullFileName = Path.of(System.getProperty("user.home"), dataFolderPath, fileName)
    val file = fullFileName.toFile()
    var count = 0
    file.readLines().forEach {
      val categoryParts = it.split(":")
      val category =
        Category(name = categoryParts[0], shopOrder = categoryParts[1].toInt())
      categoryService.save(category)
      count++
    }

    return ConverterResult(count)
  }

  @PostMapping("/converters/cookbook/{datafileName}")
  fun convertCookbook(@PathVariable(name = "datafileName") datafileName: String): ConverterResult {
    val fileName = datafileName.split("/").last()
    val fullFileName = Path.of(System.getProperty("user.home"), dataFolderPath, fileName)
    val file = fullFileName.toFile()
    var count = 0
    var recipe: Recipe? = null
    file.readLines().forEach {
      println("line $it")
      val lineParts = it.split(":")
      if (lineParts.size > 1) {
        if (lineParts[0] == "naam") {
          recipe = recipeService.save(Recipe(name = lineParts[1], favorite = false))
          count++
        } else {
          println("category ${lineParts[0]}")
          val category: Category = categoryService.findByName(lineParts[0])
          val ingredient = try {
            ingredientService.findByName(lineParts[1])
          } catch (e: ResourceNotFoundException) {
            ingredientService.save(
              Ingredient(name = lineParts[1], categoryId = category.id!!, unit = "N/A")
            )
          }
          val recipeIngredientFromFile =
            RecipeIngredient(
              recipeId = recipe?.id ?: UUID.randomUUID(),
              ingredientId = ingredient?.id ?: UUID.randomUUID(),
              unit = "kg",
              amount = 1.0f
            )
          recipeIngredientService.save(recipeIngredientFromFile)
        }
      }
    }

    return ConverterResult(count)
  }
}
