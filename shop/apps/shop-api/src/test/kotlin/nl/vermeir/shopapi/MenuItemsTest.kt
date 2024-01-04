package nl.vermeir.shopapi

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
class MenuItemTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  private val menu1 = Menu(id = UUID.randomUUID(), firstDay = march10th)
  private val recipe1 =
    Recipe(id = UUID.randomUUID(), name = "r1", favorite = true)

  private val menuItem1 =
    MenuItem(
      id = UUID.randomUUID(),
      menuId = menu1.id ?: UUID.randomUUID(),
      recipeId = recipe1.id ?: UUID.randomUUID(),
      theDay = march10th
    )

  @Test
  fun `a menuItem without id and all properties set is saved correctly and can be loaded`() {
    mockMvc.perform(
      post("/menuItem").content(
        Json.encodeToString(menuItem1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(uuidPattern))
      .andExpect(jsonPath("$.theDay").value(menuItem1.theDay.toString()))
  }

  @Test
  fun `GET menu-item should return 404 when menu-item not found by id`() {
    mockMvc.perform(
      get("/menuItem/${UUID.randomUUID()}")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of menuItems should be returned`() {
    val menuItem = mockMvc.perform(
      post("/menuItem").content(
        Json.encodeToString(menuItem1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(uuidPattern))
      .andExpect(
        jsonPath("$.theDay").value(menuItem1.theDay.toString())
      ).andReturn().response.contentAsString

    val outputMenuItem = Json.decodeFromString<MenuItem>(menuItem)

    mockMvc.perform(
      get("/menuItems").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].id").value(outputMenuItem.id.toString()))
      .andExpect(jsonPath("$.[0].theDay").value(outputMenuItem.theDay.toString()))
  }
}
