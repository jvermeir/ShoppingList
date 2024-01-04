package nl.vermeir.shopapi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
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
class RecipeIngredientTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @Autowired
  lateinit var categoryRepository: CategoryRepository

  @Autowired
  lateinit var ingredientRepository: IngredientRepository

  @Autowired
  lateinit var recipeRepository: RecipeRepository

  @Autowired
  lateinit var recipeIngredientRepository: RecipeIngredientRepository

  @BeforeEach
  fun setup() {
    val cat1 = categoryRepository.save(Category(name = "cat1", shopOrder = 1))
    objectMap["cat1"] = cat1
    val ing1 = ingredientRepository.save(Ingredient(name = "ing1", categoryId = cat1.id!!, unit = "kg"))
    objectMap["ing1"] = ing1
    val recipe1 = recipeRepository.save(Recipe(id = UUID.randomUUID(), name = "recipe1", favorite = false))
    objectMap["recipe1"] = recipe1
    val recipeIngredient1 = RecipeIngredient(
      recipeId = recipe1.id!!,
      ingredientId = ing1.id!!,
      amount = 1.0f, unit = "kg"
    )
    objectMap["recipeIngredient1"] = recipeIngredient1
  }

  @Test
  fun `a recipe-ingredient without id but all properties set is saved correctly and can be loaded`() {
    val inputRecipeIngredient1 = getFromMap<RecipeIngredient>("recipeIngredient1")
    val recipeIngredient = mockMvc.perform(
      post("/recipeIngredient").content(
        Json.encodeToString(inputRecipeIngredient1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(uuidPattern))
      .andExpect(jsonPath("$.recipeId").value(inputRecipeIngredient1.recipeId.toString()))
      .andExpect(jsonPath("$.ingredientId").value(inputRecipeIngredient1.ingredientId.toString()))
      .andExpect(jsonPath("$.amount").value(inputRecipeIngredient1.amount))
      .andExpect(jsonPath("$.unit").value(inputRecipeIngredient1.unit))
      .andReturn().response.contentAsString

    val recipeIngredient1 = Json.decodeFromString(RecipeIngredient.serializer(), recipeIngredient)

    mockMvc.perform(
      get("/recipeIngredient/${recipeIngredient1.id}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(recipeIngredient1.id.toString()))
      .andExpect(jsonPath("$.recipeId").value(recipeIngredient1.recipeId.toString()))
      .andExpect(jsonPath("$.ingredientId").value(recipeIngredient1.ingredientId.toString()))
      .andExpect(jsonPath("$.amount").value(recipeIngredient1.amount))
      .andExpect(jsonPath("$.unit").value(recipeIngredient1.unit))
  }

  @Test
  fun `GET recipe-ingredient should return 404 when recipe not found by id`() {

    mockMvc.perform(
      get("/recipeIngredient/${unknownId}")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of recipe-ingredients should be returned`() {
    val recipeIngredient1 = getFromMap<RecipeIngredient>("recipeIngredient1")
    recipeIngredientRepository.save(recipeIngredient1)

    mockMvc.perform(
      get("/recipeIngredients").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].id").value(uuidPattern))
      .andExpect(jsonPath("$.[0].recipeId").value(recipeIngredient1.recipeId.toString()))
      .andExpect(jsonPath("$.[0].ingredientId").value(recipeIngredient1.ingredientId.toString()))
      .andExpect(jsonPath("$.[0].amount").value(recipeIngredient1.amount))
      .andExpect(jsonPath("$.[0].unit").value(recipeIngredient1.unit))
  }

  @Test
  fun `a recipe should be returned by findById`() {
    val recipeIngredient1 = getFromMap<RecipeIngredient>("recipeIngredient1")
    recipeIngredientRepository.save(recipeIngredient1)

    mockMvc.perform(
      get("/recipeIngredient/${recipeIngredient1.id}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(recipeIngredient1.id.toString()))
      .andExpect(jsonPath("$.recipeId").value(recipeIngredient1.recipeId.toString()))
      .andExpect(jsonPath("$.ingredientId").value(recipeIngredient1.ingredientId.toString()))
      .andExpect(jsonPath("$.amount").value(recipeIngredient1.amount))
      .andExpect(jsonPath("$.unit").value(recipeIngredient1.unit))
  }

  @Test
  fun `a recipe-ingredient should be returned by findByRecipeId`() {
    val recipeIngredient1 = getFromMap<RecipeIngredient>("recipeIngredient1")
    recipeIngredientRepository.save(recipeIngredient1)

    mockMvc.perform(
      get("/recipeIngredient").param("recipeId", recipeIngredient1.recipeId.toString())
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].id").value(uuidPattern))
      .andExpect(jsonPath("$.[0].recipeId").value(recipeIngredient1.recipeId.toString()))
      .andExpect(jsonPath("$.[0].ingredientId").value(recipeIngredient1.ingredientId.toString()))
      .andExpect(jsonPath("$.[0].amount").value(recipeIngredient1.amount))
      .andExpect(jsonPath("$.[0].unit").value(recipeIngredient1.unit))
  }

  @Test
  fun `if unit is not present, it should be copied from the ingredient`() {
    val recipeIngredient1 = getFromMap<RecipeIngredient>("recipeIngredient1")

    mockMvc.perform(
      post("/recipeIngredient").content(
        Json.encodeToString(recipeIngredient1.copy(unit = null))
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(uuidPattern))
      .andExpect(jsonPath("$.recipeId").value(recipeIngredient1.recipeId.toString()))
      .andExpect(jsonPath("$.ingredientId").value(recipeIngredient1.ingredientId.toString()))
      .andExpect(jsonPath("$.amount").value(recipeIngredient1.amount))
      .andExpect(jsonPath("$.unit").value(recipeIngredient1.unit))
  }
}
