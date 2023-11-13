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
import java.time.LocalDate
import java.util.*

@WebMvcTest(value = [MenuItemResource::class, MenuItemService::class])
class MenuItemTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @MockkBean
  lateinit var menuItemRepository: MenuItemRepository

  private val march10th = LocalDate.parse("2022-03-10")

  private val recipe1 = Recipe(id = UUID.randomUUID(), name = "recipe1", favorite = false)
  private val menu1 = Menu(id = UUID.randomUUID(), firstDay = march10th)
  private val menuItem1 =
    MenuItem(
      id = UUID.randomUUID(),
      menuId = menu1.id ?: UUID.randomUUID(),
      recipeId = recipe1.id ?: UUID.randomUUID(),
      theDay = march10th
    )

  @Test
  fun `a menuItem without id and all properties set is saved correctly and can be loaded`() {
    every { menuItemRepository.save(menuItem1) } returns menuItem1

    mockMvc.perform(
      post("/menu-item").content(
        Json.encodeToString(menuItem1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(menuItem1.id.toString()))
      .andExpect(jsonPath("$.theDay").value(menuItem1.theDay.toString()))
  }

  @Test
  fun `GET menu-item should return 404 when menu-item not found by id`() {
    every { menuItemRepository.findById(menuItem1.id.toString()) } returns Optional.empty()

    mockMvc.perform(
      get("/menu-item/${menuItem1.id}")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of menuitems should be returned`() {
    every { menuItemRepository.findAll() } returns listOf(menuItem1)

    mockMvc.perform(
      get("/menu-items").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].id").value(menuItem1.id.toString()))
      .andExpect(jsonPath("$.[0].theDay").value(menuItem1.theDay.toString()))
  }

//  @Test
//  fun `a menu should be returned by findByDay`() {
//    every { menuItemRepository.findByDay(march10th) } returns listOf(menuItem1)
//
//    mockMvc.perform(
//      get("/menu-item").param("day", march10th.toString())
//    ).andExpect(status().isOk)
//      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//      .andExpect(jsonPath("$.[0].id").value(menuItem1.id))
//      .andExpect(jsonPath("$.[0].theDay").value(menuItem1.theDay.toString()))
//  }
}
