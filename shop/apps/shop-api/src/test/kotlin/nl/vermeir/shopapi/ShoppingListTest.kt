package nl.vermeir.shopapi

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
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
  lateinit var shoppingListRepository: ShoppingListRepository

  @MockkBean
  lateinit var shoppingListCategoriesRepository: ShoppingListCategoriesRepository

  @MockkBean
  lateinit var shoppingListIngredientRepository: ShoppingListIngredientRepository

  @MockkBean
  lateinit var uuidGenerator: UUIDGenerator

  @BeforeEach
  fun setup() {
    every { categoryRepository.findById(category1.id!!) } returns Optional.of(category1)
    every { ingredientRepository.findById(ingredient1.id!!) } returns Optional.of(ingredient1)
    every { recipeRepository.findById(recipe1.id!!) } returns Optional.of(recipe1)
    every { recipeIngredientRepository.findByRecipeId(recipe1.id!!) } returns listOf(recipeIngredient1)
    every { menuItemRepository.findByMenuId(menu1.id!!) } returns listOf(menuItem1)
    every { menuRepository.findByFirstDay(march10th) } returns menu1
    every { uuidGenerator.generate() } returns UUID.fromString(theId)
    every { shoppingListRepository.save(any()) } returns shoppingList1
    every { shoppingListCategoriesRepository.findByShoppingListId(shoppingList1.id!!) } returns listOf(
      shoppingListCategory1
    )
    every { shoppingListIngredientRepository.findByCategoryId(shoppingList1.id!!) } returns listOf(
      ShoppingListIngredient(
        id = UUID.fromString(theId),
        name = "ing1",
        categoryId = UUID.fromString(theId),
        amount = 1.0f,
        unit = "kg"
      )
    )
  }

  @Test
  fun `a shoppinglist should be returned for a menu`() {
    // TODO find a better way to test the result
    mockMvc.perform(
      MockMvcRequestBuilders.post("/shoppinglist/frommenu/firstDay/$march10th")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(shoppingList1.id.toString()))
      .andExpect(jsonPath("$.firstDay").value(shoppingList1.firstDay.toString()))
      .andExpect(jsonPath("$.categories[0].id").value(shoppingListCategory1.id.toString()))
      .andExpect(
        MockMvcResultMatchers.content()
          .string(
            """{"id":"$theId","firstDay":"2022-03-10","categories":[{"id":"$theId","name":"cat1","shopOrder":1,"ingredients":[{"id":"$theId","name":"ing1","amount":1.0,"unit":"kg"}]}]}"""
          )
      )
  }

//  @Test
//  fun `an ingredient can be removed from a shoppinglist`() {
//    val shoppingListAsText: String = mockMvc.perform(
//      MockMvcRequestBuilders.get("/shoppinglist/firstDay/$march10th")
//    ).andReturn().response.contentAsString
//
//    val gson = GsonBuilder()
//      .registerTypeAdapter(LocalDate::class.java, GsonLocalDateAdapter())
//      .create()
//    val list: ShoppingList = gson.fromJson(shoppingListAsText, ShoppingList::class.java)
////    list.items
//
////    val shoppingListAsJson = Json.parseToJsonElement(shoppingListAsText)
////    val id = shoppingListAsJson.jsonObject["id"]!!.jsonPrimitive.content
////    println(id)
//  }
}

/*

done - initial shoppinglist is based on a menu
extra ingredients may be added to the shoppinglist
ingredients of the menu may be removed from the shoppinglist

done - add unit and number to ingredient
add numbers if ingredient is the same
add date to perishable ingredient
 */
