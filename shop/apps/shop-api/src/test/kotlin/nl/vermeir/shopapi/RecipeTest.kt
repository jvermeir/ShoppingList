package nl.vermeir.shopapi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.vermeir.shopapi.data.OutputCategory
import nl.vermeir.shopapi.data.OutputIngredient
import nl.vermeir.shopapi.data.OutputRecipe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("classpath:/sql/schema.sql")
class RecipeTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @Autowired
  lateinit var recipeService: RecipeService

  @Autowired
  lateinit var categoryRepository: CategoryRepository

  @Autowired
  lateinit var ingredientRepository: IngredientRepository

  @Autowired
  lateinit var recipeRepository: RecipeRepository

  @Autowired
  lateinit var recipeIngredientRepository: RecipeIngredientRepository

  private val recipe1 = Recipe(id = UUID.randomUUID(), name = "recipe1", favorite = true)
  
  @Test
  fun `a recipe without id and all properties set is saved correctly and can be loaded`() {
    val recipe = mockMvc.perform(
      post("/recipe").content(
        Json.encodeToString(recipe1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(uuidPattern))
      .andExpect(jsonPath("$.name").value(recipe1.name))
      .andExpect(jsonPath("$.favorite").value(recipe1.favorite))
      .andReturn().response.contentAsString

    val outputRecipe = Json.decodeFromString(Recipe.serializer(), recipe)

    mockMvc.perform(
      get("/recipe/${outputRecipe.id}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(outputRecipe.id.toString()))
      .andExpect(jsonPath("$.name").value(outputRecipe.name))
      .andExpect(jsonPath("$.favorite").value(outputRecipe.favorite))
  }

  @Test
  fun `GET recipe should return 404 when recipe not found by id`() {
    mockMvc.perform(
      get("/recipe/${unknownId}")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of recipes should be returned`() {
    recipeRepository.save(recipe1)
    mockMvc.perform(
      get("/recipes").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].id").value(recipe1.id.toString()))
      .andExpect(jsonPath("$.[0].name").value(recipe1.name))
      .andExpect(jsonPath("$.[0].favorite").value(recipe1.favorite))
  }

  @Test
  fun `a recipe should be returned by findById`() {
    recipeRepository.save(recipe1)

    mockMvc.perform(
      get("/recipe/${recipe1.id}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(recipe1.id.toString()))
      .andExpect(jsonPath("$.name").value(recipe1.name))
      .andExpect(jsonPath("$.favorite").value(recipe1.favorite))
  }

  @Test
  fun `a recipe should be returned by findByName`() {
    recipeRepository.save(recipe1)

    mockMvc.perform(
      get("/recipe").param("name", recipe1.name)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(recipe1.id.toString()))
      .andExpect(jsonPath("$.name").value(recipe1.name))
      .andExpect(jsonPath("$.favorite").value(recipe1.favorite))
  }

  @Test
  fun `a recipe object can be transformed to a OutputRecipe object`() {
    val category = categoryRepository.save(Category(name = "cat1", shopOrder = 1))
    val ingredient = ingredientRepository.save(Ingredient(name = "ing1", categoryId = category.id!!, unit = "kg"))
    val recipe = recipeRepository.save(Recipe(name = "rec1", favorite = true))
    val recipeIngredient = recipeIngredientRepository.save(
      RecipeIngredient(
        recipeId = recipe.id!!,
        ingredientId = ingredient.id!!,
        amount = 1.0f,
        unit = "kg"
      )
    )

    val outputIngredient = OutputIngredient(
      ingredient.id.toString(),
      "ing1",
      OutputCategory(id = category.id.toString(), name = category.name, shopOrder = category.shopOrder),
      unit = ingredient.unit,
      amount = recipeIngredient.amount!!
    )

    val expectedOutputRecipe = OutputRecipe(recipe.id.toString(), "rec1", true, listOf(outputIngredient))

    val outputRecipe = recipeService.toOutputRecipe(recipe)
    assert(outputRecipe == expectedOutputRecipe)
  }
}
