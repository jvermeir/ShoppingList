package nl.vermeir.shopapi

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.annotation.Id
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

//@kotlinx.serialization.Serializable
//data class MenuItem(@Id val id: String? = null, val menuId: String, val recipeId: String, val theDay: LocalDate)

@WebMvcTest(value = [MenuItemResource::class, MenuItemService::class])
class MenuItemTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @MockkBean
  lateinit var menuItemRepository: MenuItemRepository

  private val march10th = LocalDate(2022,3,10)

  private val recipe1 = Recipe(id = "1", name = "recipe1", favorite = false)
  private val menu1 = Menu(id = "1", firstDay = march10th)
  private val menuItem1 = MenuItem(id = "1", menuId = menu1.id.orEmpty(), recipeId = recipe1.id.orEmpty(), theDay = march10th)

  @Test
  fun `a menuItem without id and all properties set is saved correctly and can be loaded`() {
    every { menuItemRepository.save(menuItem1) } returns menuItem1

    mockMvc.perform(
      post("/menu-item").content(
        Json.encodeToString(menuItem1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(menuItem1.id))
      .andExpect(jsonPath("$.theDay").value(menuItem1.theDay.toString()))
  }

  @Test
  fun `GET menu-item should return 404 when menu-item not found by id`() {
    every { menuItemRepository.findById("1") } returns Optional.empty()

    mockMvc.perform(
      get("/menu-item/1")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of menuitems should be returned`() {
    every { menuItemRepository.findAll() } returns listOf(menuItem1)

    mockMvc.perform(
      get("/menu-items").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].id").value(menuItem1.id))
      .andExpect(jsonPath("$.[0].theDay").value(menuItem1.theDay.toString()))
  }

  @Test
  fun `a menu should be returned by findByDay`() {
    every { menuItemRepository.findByDay(march10th) } returns listOf(menuItem1)

    mockMvc.perform(
      get("/menu-item").param("day", march10th.toString())
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].id").value(menuItem1.id))
      .andExpect(jsonPath("$.[0].theDay").value(menuItem1.theDay.toString()))
  }
}
