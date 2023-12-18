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
    every { categoryRepository.findById(c1.id!!) } returns Optional.of(c1)
    every { categoryRepository.findById(c2.id!!) } returns Optional.of(c2)

    every { ingredientRepository.findById(i1.id!!) } returns Optional.of(i1)
    every { ingredientRepository.findById(i2.id!!) } returns Optional.of(i2)

    every { recipeIngredientRepository.findByRecipeId(r1.id!!) } returns listOf(r1i1)
    every { recipeIngredientRepository.findByRecipeId(r2.id!!) } returns listOf(r1i2)
    every { recipeIngredientRepository.findById(r1i1.id!!) } returns Optional.of(r1i1)
    every { recipeIngredientRepository.findById(r2i1.id!!) } returns Optional.of(r2i1)

    every { recipeRepository.findById(r1.id!!) } returns Optional.of(r1)
    every { recipeRepository.findById(r2.id!!) } returns Optional.of(r2)

    every { menuItemRepository.findByMenuId(m.id!!) } returns listOf(mi1, mi2)
    every { menuItemRepository.findById(mi1.id!!) } returns Optional.of(mi1)
    every { menuItemRepository.findById(mi2.id!!) } returns Optional.of(mi2)
    every { menuRepository.findByFirstDay(march10th) } returns m

    every { shoppingListRepository.findById(sl1.id!!) } returns Optional.of(sl1)
    every { shoppingListRepository.findByFirstDay(sl1.firstDay) } returns sl1
    every { shoppingListCategoriesRepository.findByShoppingListId(sl1.id!!) } returns listOf(
      slc1,
      slc2
    )
    every { shoppingListIngredientRepository.findById(i1.id!!) } returns Optional.of(sli1)
    every { shoppingListIngredientRepository.findById(i2.id!!) } returns Optional.of(sli2)
    every { shoppingListIngredientRepository.findByShoppingListCategoryId(slc1.id!!) } returns listOf(
      sli1
    )
    every { shoppingListIngredientRepository.findByShoppingListCategoryId(slc2.id!!) } returns listOf(
      sli2
    )
    every { shoppingListIngredientRepository.save(sli1) } returns sli1
    every { shoppingListIngredientRepository.save(sli1.copy(amount = 10.0f)) } returns sli1.copy(amount = 10.0f)
    every { shoppingListIngredientRepository.save(sli2) } returns sli2
    every { shoppingListRepository.save(sl1) } returns sl1
    every { shoppingListCategoriesRepository.save(slc1) } returns slc1
    every { shoppingListCategoriesRepository.save(slc2) } returns slc2
  }

  // TODO: enable
  fun `a shoppinglist should be returned for a menu`() {
    every { uuidGenerator.generate() } returns sl1.id!!
    every {
      shoppingListIngredientRepository.save(
        sli1.copy(
          id = sl1.id,
          shoppingListCategoryId = sl1.id!!
        )
      )
    } returns sli1
    every {
      shoppingListIngredientRepository.save(
        sli2.copy(
          id = sl1.id,
          shoppingListCategoryId = sl1.id!!
        )
      )
    } returns sli2
    every { shoppingListCategoriesRepository.save(slc1.copy(id = sl1.id)) } returns slc1.copy(id = sl1.id)
    every { shoppingListCategoriesRepository.save(slc2.copy(id = sl1.id)) } returns slc2.copy(id = sl1.id)

    mockMvc.perform(
      MockMvcRequestBuilders.post("/shoppinglist/frommenu/firstDay/$march10th")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(sl1.id.toString()))
      .andExpect(jsonPath("$.firstDay").value(sl1.firstDay.toString()))
      .andExpect(jsonPath("$.categories[0].ingredients[0].amount").value(1f))
      .andExpect(jsonPath("$.categories[1].ingredients[0].amount").value(2f))
  }

  @Test
  fun `a shoppinglist should be returned from the shoppinglist table`() {
    // TODO find a better way to test the result
    // TODO: why Optional when searching by id and not when searching by firstDay?

    mockMvc.perform(
      MockMvcRequestBuilders.get("/shoppinglist/firstDay/$march10th")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(sl1.id.toString()))
      .andExpect(jsonPath("$.firstDay").value(sl1.firstDay.toString()))
      .andExpect(jsonPath("$.categories[0].id").value(slc1.id.toString()))
  }

  // TODO: enable
  fun `an ingredient can be added to the shoppinglist`() {

    mockMvc.perform(
      MockMvcRequestBuilders.put("/shoppinglist/${sl1.id}/ingredient/${i2.id}/amount/10")
    ).andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.id").value(sl1.id.toString()))
      .andExpect(jsonPath("$.firstDay").value(sl1.firstDay.toString()))
      .andExpect(jsonPath("$.categories[0].id").value(slc1.id.toString()))
      .andExpect(jsonPath("$.categories[1].id").value(slc2.id.toString()))
      .andExpect(jsonPath("$.categories[0].ingredients[0].amount").value(1f))
      .andExpect(jsonPath("$.categories[1].ingredients[0].amount").value(10.0f))
  }
}

/*

done - initial shoppinglist is based on a menu
extra ingredients may be added to the shoppinglist
ingredients of the menu may be removed from the shoppinglist

done - add unit and number to ingredient
add numbers if ingredient is the same
add date to perishable ingredient
 */
