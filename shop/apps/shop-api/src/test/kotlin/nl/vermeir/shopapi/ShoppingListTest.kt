package nl.vermeir.shopapi

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.util.*

@WebMvcTest(
  value = [
    ShoppingListResource::class,
    MenuService::class, MenuItemService::class, RecipeService::class, IngredientService::class, CategoryService::class, RecipeIngredientService::class, ShoppingListService::class
  ]
)

class ShoppingListTest {
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

  @MockkBean
  lateinit var uuidGenerator: UUIDGenerator

  @Test
  fun `a shoppinglist should be returned for a menu`() {
    every { categoryRepository.findById(category1.id!!) } returns Optional.of(category1)
    every { ingredientRepository.findById(ingredient1.id!!) } returns Optional.of(ingredient1)
    every { recipeRepository.findById(recipe1.id!!) } returns Optional.of(recipe1)
    every { recipeIngredientRepository.findByRecipeId(recipe1.id!!) } returns listOf(recipeIngredient1)
    every { menuItemRepository.findByMenuId(menu1.id!!) } returns listOf(menuItem1)
    every { menuRepository.findByFirstDay(march10th) } returns menu1
    every { uuidGenerator.generate() } returns UUID.fromString("cb494d70-5c5c-45d4-ac66-9b84fe096fc6")

    mockMvc.perform(
      MockMvcRequestBuilders.get("/shoppinglist/firstDay/$march10th")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(menu1.id.toString()))
      .andExpect(jsonPath("$.firstDay").value(menu1.firstDay.toString()))
      .andExpect(jsonPath("$.items").isMap)
      .andExpect(
        MockMvcResultMatchers.content()
          .string("""{"id":"0797c413-45d7-412a-a4da-7ccd90ded9ee","firstDay":"2022-03-10","items":{"Category(id=0797c413-45d7-412a-a4da-7ccd90ded9ee, name=cat1, shopOrder=1)":[{"id":"0797c413-45d7-412a-a4da-7ccd90ded9ee","name":"ing1","categoryId":"0797c413-45d7-412a-a4da-7ccd90ded9ee","listIngredientId":"cb494d70-5c5c-45d4-ac66-9b84fe096fc6"}]}}""")
      )
  }
}

/*

shoppinglist is based on a menu
extra ingredients may be added to the shoppinglist
ingredients of the menu may be removed from the shoppinglist

add unit and number to ingredient
add numbers for same ingredient
add date to perishable ingredient
 */
