package nl.vermeir.shopapi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.vermeir.shopapi.data.OutputCategory
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.text.MatchesPattern.matchesPattern
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("classpath:/sql/schema.sql")
class CategoryTest {

  @Autowired
  lateinit var categoryService: CategoryService

  @Autowired
  lateinit var categoryRepository: CategoryRepository

  @Autowired
  lateinit var mockMvc: MockMvc

  private val inputCategory1 = Category(name = "cat1", shopOrder = 1)
  private val inputCategory2 = Category(name = "cat2", shopOrder = 2)

  @Test
  fun `a category without id and all properties set is saved correctly and can be loaded`() {
    val cat = Json.decodeFromString<Category>(
      mockMvc.perform(
        post("/category").content(
          Json.encodeToString(inputCategory1)
        ).contentType(MediaType.APPLICATION_JSON)
      ).andExpect(status().isCreated).andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value(inputCategory1.name))
        .andExpect(jsonPath("$.id").value(matchesPattern("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\$")))
        .andExpect(jsonPath("$.shopOrder").value(inputCategory1.shopOrder)).andReturn().response.contentAsString
    )

    mockMvc.perform(
      get("/category/${cat.id}")
    ).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(cat.name)).andExpect(jsonPath("$.id").value(cat.id.toString()))
      .andExpect(jsonPath("$.shopOrder").value(cat.shopOrder))

    mockMvc.perform(
      get("/category").param("name", cat.name)
    ).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.name").value(cat.name)).andExpect(jsonPath("$.id").value(cat.id.toString()))
      .andExpect(jsonPath("$.shopOrder").value(cat.shopOrder))
  }

  @Test
  fun `a category can be deleted by id`() {
    val cat = categoryRepository.save(inputCategory1)

    mockMvc.perform(
      delete("/category/${cat.id.toString()}")
    ).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))

    assert(categoryRepository.findById(cat.id!!).isEmpty)
  }

  @Test
  fun `GET category should return 404 when category not found by id`() {
    mockMvc.perform(
      get("/category/${UUID.randomUUID()}")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `GET category should return 404 when category not found by name`() {
    mockMvc.perform(
      get("/category").param("name", "1")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of categories should be returned`() {
    val cat1 = categoryRepository.save(inputCategory1)
    val cat2 = categoryRepository.save(inputCategory2)

    mockMvc.perform(
      get("/categories").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(
      jsonPath(
        "$[*].id", containsInAnyOrder(
          cat1.id.toString(), cat2.id.toString()
        )
      )
    ).andExpect(
      jsonPath(
        "$[*].name", containsInAnyOrder(
          cat1.name, cat2.name
        )
      )
    ).andExpect(
      jsonPath(
        "$[*].shopOrder", containsInAnyOrder(
          cat1.shopOrder, cat2.shopOrder
        )
      )
    )
  }

  @Test
  fun `a category object can be transformed to a OutputCategory object`() {
    val id = UUID.randomUUID()
    val category = Category(id, "cat1", 1)
    val outputCategory = categoryService.toOutputCategory(category)
    val expectedOutputCategory = OutputCategory(id.toString(), "cat1", 1)
    assert(outputCategory == expectedOutputCategory)
  }
}
