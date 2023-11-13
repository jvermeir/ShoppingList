package nl.vermeir.shopapi

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest(value = [IngredientResource::class, IngredientService::class])
class IngredientTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @MockkBean
  lateinit var ingredientRepository: IngredientRepository

  @MockkBean
  lateinit var categoryService: CategoryService

  @Autowired
  lateinit var ingredientService: IngredientService

  private val ingredient1 = Ingredient(id = UUID.randomUUID(), name = "name1", categoryId = UUID.randomUUID())

  @Test
  fun `a ingredient without id and all properties set is saved correctly and can be loaded`() {
    every { ingredientRepository.save(ingredient1) } returns ingredient1

    mockMvc.perform(
      post("/ingredient").content(
        Json.encodeToString(ingredient1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(ingredient1.name))
      .andExpect(jsonPath("$.id").value(ingredient1.id.toString()))
      .andExpect(jsonPath("$.categoryId").value(ingredient1.categoryId.toString()))
  }

  @Test
  fun `GET ingredient should return 404 when ingredient not found by id`() {
    every { ingredientRepository.findById(ingredient1.id.toString()) } returns Optional.empty()

    mockMvc.perform(
      get("/ingredient/${ingredient1.id}")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `GET ingredient should return 404 when ingredient not found by name`() {
    every { ingredientRepository.findByName("ingredient1") } returns Optional.empty()

    mockMvc.perform(
      get("/ingredient").param("name", "ingredient1")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of categories should be returned`() {
    every { ingredientRepository.findAll() } returns listOf(ingredient1)

    mockMvc.perform(
      get("/ingredients").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].name").value(ingredient1.name))
      .andExpect(jsonPath("$.[0].id").value(ingredient1.id.toString()))
      .andExpect(jsonPath("$.[0].categoryId").value(ingredient1.categoryId.toString()))
  }

  @Test
  fun `a ingredient should be returned by findById`() {
    every { ingredientRepository.findById(ingredient1.id.toString()) } returns Optional.of(ingredient1)

    mockMvc.perform(
      get("/ingredient/${ingredient1.id}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(ingredient1.name))
      .andExpect(jsonPath("$.id").value(ingredient1.id.toString()))
      .andExpect(jsonPath("$.categoryId").value(ingredient1.categoryId.toString()))
  }

  @Test
  fun `a ingredient should be returned by getCategoryByName`() {
    every { ingredientRepository.findByName("name1") } returns Optional.of(ingredient1)

    mockMvc.perform(
      get("/ingredient").param("name", "name1")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(ingredient1.name))
      .andExpect(jsonPath("$.id").value(ingredient1.id.toString()))
      .andExpect(jsonPath("$.categoryId").value(ingredient1.categoryId.toString()))
  }

//  @Test
//  fun `a ingredient object can be transformed to a OutputIngredient object`() {
//    val category = Category("1", "cat1", 1)
//    val ingredient = Ingredient("1", "ing1", "1")
//    val expectedOutputCategory = OutputCategory("1", "cat1", 1)
//
//    every { categoryService.findById("1") } returns category
//    every { categoryService.toOutputCategory(category) } returns expectedOutputCategory
//    every { ingredientRepository.findById("1") } returns Optional.of(ingredient)
//
//    val outputIngredient = ingredientService.toOutputIngredient(ingredient)
//
//    val expectedOutputIngredient = OutputIngredient("1", "ing1", expectedOutputCategory)
//    assert(outputIngredient == expectedOutputIngredient)
//  }
}
