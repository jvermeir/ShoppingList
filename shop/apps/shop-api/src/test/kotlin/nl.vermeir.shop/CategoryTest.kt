package nl.vermeir.shop

import nl.vermeir.shopapi.Category
import nl.vermeir.shopapi.CategoryRepository
import nl.vermeir.shopapi.CategoryService
import org.mockito.Mockito
import java.util.NoSuchElementException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CategoryTest {

    val mockCategoryRepository = Mockito.mock(CategoryRepository::class.java)
    val service = CategoryService(mockCategoryRepository)
  @Test
  fun `a category with all properties set is saved correctly`() {
    val category1 = Category("category1", "name", 10)
    Mockito.`when`(mockCategoryRepository.save(category1)).thenReturn(category1)

    val savedCategory = service.post(category1)
    assertEquals(category1, savedCategory)
  }

  @Test
  fun `a category with name field set and id field is empty is saved correctly`() {
    val category1 = Category("cat1", "name", 10)
    val newCategory1 = Category(null, "name", 101)
    val savedCategory1 = Category("cat1", "name", 101)
    Mockito.`when`(mockCategoryRepository.findByName("name")).thenReturn(category1)
    Mockito.`when`(mockCategoryRepository.save(savedCategory1)).thenReturn(savedCategory1)

    val savedCategory = service.post(newCategory1)
    assertEquals(savedCategory1, savedCategory)
  }

  @Test
  fun `a category that cannot be found by name throws exception`() {
    val newCategory1 = Category(null, "name", 101)
    Mockito.`when`(mockCategoryRepository.findByName("name")).thenReturn(null)

    assertFailsWith<NoSuchElementException> {
      service.post(newCategory1)
    }
  }

}
