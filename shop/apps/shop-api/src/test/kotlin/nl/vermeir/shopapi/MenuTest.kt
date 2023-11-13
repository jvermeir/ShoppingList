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

@WebMvcTest(
  value = [
    MenuResource::class, MenuService::class,
    MenuItemService::class, RecipeService::class, IngredientService::class, CategoryService::class,
    CategoryRepository::class, IngredientRepository::class, MenuItemRepository::class, RecipeRepository::class
  ]
)

class MenuTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @MockkBean
  lateinit var menuRepository: MenuRepository

  @MockkBean
  lateinit var menuItemRepository: MenuItemRepository

  @MockkBean
  lateinit var categoryRepository: CategoryRepository

  @MockkBean
  lateinit var ingredientRepository: IngredientRepository

  @MockkBean
  lateinit var recipeRepository: RecipeRepository

  @MockkBean
  lateinit var recipeIngredientRepository: RecipeIngredientRepository

  private val march10th = LocalDate.parse("2022-03-10")

  // TODO: !! ?
  private val category1 = Category(id = UUID.randomUUID(), name = "cat1", shopOrder = 1)
  private val menu1 = Menu(id = UUID.randomUUID(), firstDay = march10th)
  private val recipe1 = Recipe(id = UUID.randomUUID(), name = "r1", favorite = true)
  private val menuItem1 =
    MenuItem(id = UUID.randomUUID(), menuId = menu1.id!!, recipeId = recipe1.id!!, theDay = march10th)
  private val ingredient1 = Ingredient(id = UUID.randomUUID(), name = "ing1", categoryId = category1.id!!)
  private val recipeIngredient1 =
    RecipeIngredient(id = UUID.randomUUID(), recipeId = recipe1.id!!, ingredientId = ingredient1.id!!)

  @Test
  fun `a menu without id and all properties set is saved correctly and can be loaded`() {
    every { menuRepository.save(menu1) } returns menu1
    mockMvc.perform(
      post("/menu").content(
        Json.encodeToString(menu1)
      ).contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isCreated)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(menu1.id.toString()))
      .andExpect(jsonPath("$.firstDay").value(menu1.firstDay.toString()))
  }

  @Test
  fun `GET menu should return 404 when menu not found by id`() {
    every { menuRepository.findById(menu1.id.toString()) } returns Optional.empty()

    mockMvc.perform(
      get("/menu/${menu1.id}")
    ).andExpect(status().isNotFound)
  }

  @Test
  fun `a list of menus should be returned`() {
    every { menuRepository.findAll() } returns listOf(menu1)

    mockMvc.perform(
      get("/menus").contentType(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.[0].id").value(menu1.id.toString()))
      .andExpect(jsonPath("$.[0].firstDay").value(menu1.firstDay.toString()))
  }

  @Test
  fun `a menu should be returned by findById`() {
    every { menuRepository.findById(menu1.id.toString()) } returns Optional.of(menu1)

    mockMvc.perform(
      get("/menu/${menu1.id}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(menu1.id.toString()))
      .andExpect(jsonPath("$.firstDay").value(menu1.firstDay.toString()))
  }

  // TODO: this fails in IntelliJ with message 'Cannot access class 'nl.vermeir.shopapi.Menu'. Check your module classpath for missing or conflicting dependencies'
  // but works fine in gradle build
  @Test
  fun `a menu should be returned by findByFirstDay`() {
    every { menuRepository.findByFirstDay(march10th) } returns menu1
    every { menuItemRepository.findByMenuId(menuItem1.id ?: UUID.randomUUID()) } returns listOf(menuItem1)

    mockMvc.perform(
      get("/menu/firstDay/${march10th}")
    ).andExpect(status().isOk)
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(menu1.id.toString()))
      .andExpect(jsonPath("$.firstDay").value(menu1.firstDay.toString()))
  }

  // TODO: this fails with message 'Cannot access class 'nl.vermeir.shopapi.Menu'. Check your module classpath for missing or conflicting dependencies'
  // TODO: refactor, why do we need to query by name?
//  @Test
//  fun `a menu and its details should be returned by menu details firstday`() {
//    every { menuRepository.findByFirstDay(march10th) } returns menu1
//    every { menuItemRepository.findByMenuId(menuItem1.id ?: UUID.randomUUID()) } returns listOf(menuItem1)
//    every { recipeRepository.findById(recipe1.id.toString()) } returns Optional.of(recipe1)
//    every { recipeIngredientRepository.findByRecipeId(recipe1.id ?: UUID.randomUUID()) } returns listOf(
//      recipeIngredient1
//    )
//    every { ingredientRepository.findById(recipeIngredient1.ingredientId.toString()) } returns Optional.of(ingredient1)
//    every { categoryRepository.findByName(category1.name) } returns Optional.of(category1)
//
//    val d = march10th.toString()
//    mockMvc.perform(
//      get("/menu/details/firstDay/${d}")
//    ).andExpect(status().isOk)
//      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//      .andExpect(content().string("""{"id":"1","firstDay":"2022-03-10","menuItems":[{"id":"1","theDay":"2022-03-10","recipe":{"id":"1","name":"r1","favorite":true,"ingredients":[{"id":"1","name":"ing1","category":{"id":"1","name":"cat1","shopOrder":1}}]}}]}"""))
//  }
}
