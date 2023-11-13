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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest(value = [CategoryResource::class, CategoryService::class])
class CategoryTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @MockkBean
  lateinit var categoryRepository: CategoryRepository

  private val category1 = Category(UUID.randomUUID(), "cat1", 1)

  @Test
  fun `a category without id and all properties set is saved correctly and can be loaded`() {
    every { categoryRepository.save(category1) } returns category1

    mockMvc.perform(
      post("/category").content(
        Json.encodeToString(category1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(category1.name))
      .andExpect(jsonPath("$.id").value(category1.id.toString()))
      .andExpect(jsonPath("$.shopOrder").value(category1.shopOrder))
  }

  @Test
  fun `a category can be deleted by id`() {
    every { categoryRepository.deleteById(category1.id.toString()) } returns Unit

    mockMvc.perform(
      delete("/category/${category1.id}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
  }

  @Test
  fun `GET category should return 404 when category not found by id`() {
    every { categoryRepository.findById(category1.id.toString()) } returns Optional.empty()

    mockMvc.perform(
      get("/category/${category1.id}")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `GET category should return 404 when category not found by name`() {
    every { categoryRepository.findByName("1") } returns Optional.empty()

    mockMvc.perform(
      get("/category").param("name", "1")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of categories should be returned`() {
    every { categoryRepository.findAll() } returns listOf(category1)

    mockMvc.perform(
      get("/categories").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].name").value(category1.name))
      .andExpect(jsonPath("$.[0].id").value(category1.id.toString()))
      .andExpect(jsonPath("$.[0].shopOrder").value(category1.shopOrder))
  }

  @Test
  fun `a category should be returned by findById`() {
    every { categoryRepository.findById(category1.id.toString()) } returns Optional.of(category1)

    mockMvc.perform(
      get("/category/${category1.id}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(category1.name))
      .andExpect(jsonPath("$.id").value(category1.id.toString()))
      .andExpect(jsonPath("$.shopOrder").value(category1.shopOrder))
  }

  @Test
  fun `a category should be returned by getCategoryByName`() {
    every { categoryRepository.findByName("cat1") } returns Optional.of(category1)

    mockMvc.perform(
      get("/category").param("name", "cat1")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(category1.name))
      .andExpect(jsonPath("$.id").value(category1.id.toString()))
      .andExpect(jsonPath("$.shopOrder").value(category1.shopOrder))
  }

//  @Test
//  fun `a category object can be transformed to a OutputCategory object`() {
//    val category = Category("1", "cat1", 1)
//    val outputCategory = categoryService.toOutputCategory(category)
//    val expectedOutputCategory = OutputCategory("1", "cat1", 1)
//    assert(outputCategory == expectedOutputCategory)
//  }
}
