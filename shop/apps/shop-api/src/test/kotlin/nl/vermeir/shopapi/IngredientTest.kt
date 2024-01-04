package nl.vermeir.shopapi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.vermeir.shopapi.data.OutputCategory
import nl.vermeir.shopapi.data.OutputIngredient
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
class IngredientTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @Autowired
  lateinit var ingredientService: IngredientService

  @Autowired
  lateinit var ingredientRepository: IngredientRepository

  @Autowired
  lateinit var categoryRepository: CategoryRepository

  @BeforeEach
  fun setup() {
    val cat1 = categoryRepository.save(Category(name = "cat1", shopOrder = 1))
    objectMap["cat1"] = cat1
    val ing1 = ingredientRepository.save(Ingredient(name = "ing1", categoryId = cat1.id!!, unit = "kg"))
    objectMap["ing1"] = ing1
  }

  @Test
  fun `a ingredient without id and all properties set is saved correctly and can be loaded`() {
    val cat1 = getFromMap<Category>("cat1")
    val ingredient1 =
      Ingredient(id = UUID.randomUUID(), name = "name1", categoryId = cat1.id!!, unit = "kg")
    val inputIngredient1 = Ingredient(name = "name1", categoryId = cat1.id!!, unit = "kg")

    val ingredient = mockMvc.perform(
      post("/ingredient").content(
        Json.encodeToString(inputIngredient1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(ingredient1.name))
      .andExpect(jsonPath("$.id").value(uuidPattern))
      .andExpect(jsonPath("$.categoryId").value(ingredient1.categoryId.toString()))
      .andReturn().response.contentAsString

    val outputIngredient = Json.decodeFromString(Ingredient.serializer(), ingredient)

    mockMvc.perform(
      get("/ingredient/${outputIngredient.id}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(outputIngredient.name))
      .andExpect(jsonPath("$.id").value(outputIngredient.id.toString()))
      .andExpect(jsonPath("$.categoryId").value(outputIngredient.categoryId.toString()))
      .andExpect(jsonPath("$.unit").value(outputIngredient.unit))
  }

  @Test
  fun `GET ingredient should return 404 when ingredient not found by id`() {
    mockMvc.perform(
      get("/ingredient/${unknownId}")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `GET ingredient should return 404 when ingredient not found by name`() {
    mockMvc.perform(
      get("/ingredient").param("name", unknownId.toString())
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of ingredients should be returned`() {
    val ing1 = getFromMap<Ingredient>("ing1")
    mockMvc.perform(
      get("/ingredients").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].name").value(ing1.name))
      .andExpect(jsonPath("$.[0].id").value(ing1.id.toString()))
      .andExpect(jsonPath("$.[0].categoryId").value(ing1.categoryId.toString()))
      .andExpect(jsonPath("$.[0].unit").value(ing1.unit))
  }

  @Test
  fun `a ingredient should be returned by findById`() {
    val ing1 = getFromMap<Ingredient>("ing1")
    mockMvc.perform(
      get("/ingredient/${ing1.id}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(ing1.name))
      .andExpect(jsonPath("$.id").value(ing1.id.toString()))
      .andExpect(jsonPath("$.categoryId").value(ing1.categoryId.toString()))
      .andExpect(jsonPath("$.unit").value(ing1.unit))
  }

  @Test
  fun `a ingredient should be returned by name`() {
    val ing1 = getFromMap<Ingredient>("ing1")
    mockMvc.perform(
      get("/ingredient").param("name", ing1.name)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(ing1.name))
      .andExpect(jsonPath("$.id").value(ing1.id.toString()))
      .andExpect(jsonPath("$.categoryId").value(ing1.categoryId.toString()))
      .andExpect(jsonPath("$.unit").value(ing1.unit))
  }

  @Test
  fun `a ingredient object can be transformed to a OutputIngredient object`() {
    val ing1 = getFromMap<Ingredient>("ing1")
    val cat1 = getFromMap<Category>("cat1")
    val expectedOutputCategory = OutputCategory(cat1.id.toString(), "cat1", 1)

    val outputIngredient = ingredientService.toOutputIngredient(ing1, unit = "kg", amount = 1f)

    val expectedOutputIngredient =
      OutputIngredient(ing1.id.toString(), "ing1", expectedOutputCategory, unit = "kg", amount = 1f)
    assert(outputIngredient == expectedOutputIngredient)
  }
}
