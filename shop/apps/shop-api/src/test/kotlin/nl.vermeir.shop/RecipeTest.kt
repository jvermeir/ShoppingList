package nl.vermeir.shop

import nl.vermeir.shopapi.*
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.data.repository.CrudRepository
import java.util.NoSuchElementException
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RecipeTest {

  val mockRecipeRepository = Mockito.mock(RecipeRepository::class.java)
  val mockIngredientRepository = Mockito.mock(IngredientRepository::class.java)
  val mockRecipeIngredientRepository = Mockito.mock(RecipeIngredientRepository::class.java)
  val mockCategoryRepository = Mockito.mock(CategoryRepository::class.java)
  val service = RecipeService(
    mockRecipeRepository,
    mockIngredientRepository,
    mockRecipeIngredientRepository,
    mockCategoryRepository
  )
  val category = Optional.of(Category("cat1", "cat1name", 1))
  val recipe1 = Recipe("recipe1", "name", true)
  val ingredient1 = Ingredient("ing1", "ing1name", category.get().id!!)
  val ingredientDetails =
    IngredientDetails("recIngredient1", ingredient1.id, ingredient1.name, category.get().id!!, category.get().name)
  val recipeIngredient = RecipeIngredient(ingredientDetails.recipeIngredientId, recipe1.id!!, ingredient1.id!!)
  val recipeDetails = RecipeDetails(recipe1, listOf(ingredientDetails))

  // TODO: This gets really verbose, how to test?
  @Test
  fun `recipeDetails are saved in recipe, recipeDetails and ingredients tables`() {
    Mockito.`when`(mockRecipeRepository.save(recipe1)).thenReturn(recipe1)
    Mockito.`when`(mockCategoryRepository.findById("cat1")).thenReturn(category)
    Mockito.`when`(mockIngredientRepository.save(ingredient1)).thenReturn(ingredient1)
    Mockito.`when`(mockIngredientRepository.ingredientsByRecipe(recipe1.id!!)).thenReturn(listOf(ingredientDetails))

    val recipeDetails = service.post(recipeDetails)

    assertEquals(recipeDetails, RecipeDetails(recipe1, listOf(ingredientDetails)))
    Mockito.verify(mockRecipeRepository, times(1)).save(recipe1)
    Mockito.verify(mockIngredientRepository, times(1)).save(ingredient1)
    Mockito.verify(mockRecipeIngredientRepository, times(1)).save(recipeIngredient)
    Mockito.verify(mockIngredientRepository, times(1)).ingredientsByRecipe(recipe1.id!!)
  }

  @Test
  fun `a recipe with all properties set is saved correctly`() {
    Mockito.`when`(mockRecipeRepository.save(recipe1)).thenReturn(recipe1)

    val savedRecipe = service.post(recipe1)

    assertEquals(recipe1, savedRecipe)
  }

  @Test
  fun `a recipe with name field set and id field is empty is saved correctly`() {
    val newRecipe1 = Recipe(null, "name", false)
    val savedRecipe1 = Recipe("rec1", "name", false)
    Mockito.`when`(mockRecipeRepository.findByName("name")).thenReturn(recipe1)
    Mockito.`when`(mockRecipeRepository.save(savedRecipe1)).thenReturn(savedRecipe1)

    val savedrecipe = service.post(newRecipe1)

    assertEquals(savedRecipe1, savedrecipe)
  }

  // TODO: test exception message
  @Test
  fun `a recipe that cannot be found by name throws exception`() {
    val newrecipe1 = Recipe(null, "name", false)
    Mockito.`when`(mockRecipeRepository.findByName("name")).thenReturn(null)

    assertFailsWith<NoSuchElementException> {
      service.post(newrecipe1)
    }
  }

}
