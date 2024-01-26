package nl.vermeir.shopapi

import nl.vermeir.shopapi.shoppinglist.ShoppingList
import nl.vermeir.shopapi.shoppinglist.ShoppingListService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*

@RestController
class ShoppingListResource(val shoppingListService: ShoppingListService) {

  @PostMapping("/shoppinglist/fromMenu/firstDay/{firstDay}")
  fun fromMenu(@PathVariable(name = "firstDay") firstDay: LocalDate) =
    ResponseEntity.ok(shoppingListService.fromMenu(firstDay))

  @PostMapping("/shoppinglist/reloadFromMenu/firstDay/{firstDay}")
  fun reloadFromMenu(@PathVariable(name = "firstDay") firstDay: LocalDate) =
    ResponseEntity.ok(shoppingListService.reloadFromMenu(firstDay))

  @GetMapping("/shoppinglist/firstDay/{firstDay}")
  fun findByFirstDay(@PathVariable(name = "firstDay") firstDay: LocalDate) =
    ResponseEntity.ok(shoppingListService.getShoppingList(firstDay))

  @PutMapping("shoppinglist/{id}/ingredient/{ingredientId}/amount/{amount}")
  fun updateAmount(
    @PathVariable(name = "id") id: UUID,
    @PathVariable(name = "ingredientId") ingredientId: UUID,
    @PathVariable(name = "amount") amount: Float
  ) =
    ResponseEntity.ok(shoppingListService.updateIngredient(id, ingredientId, amount))

  @PostMapping("shoppinglist/{id}/add/ingredient/{ingredientId}/amount/{amount}")
  fun addIngredient(
    @PathVariable(name = "id") id: UUID,
    @PathVariable(name = "ingredientId") ingredientId: UUID,
    @PathVariable(name = "amount") amount: Float
  ) =
    ResponseEntity.ok(shoppingListService.addIngredient(id, ingredientId, amount))

  @PutMapping("shoppinglist/{id}/menuItem/{menuItemId}/day/{day}")
  fun addMenuItem(
    @PathVariable(name = "id") id: UUID,
    @PathVariable(name = "menuItemId") menuItemId: UUID,
    day: LocalDate
  ) =
    ResponseEntity.ok(shoppingListService.addMenuItem(id, menuItemId, day))

  @DeleteMapping("/shoppinglist/{id}/category/{categoryId}")
  fun deleteCategory(
    @PathVariable(name = "id") shoppingListId: UUID,
    @PathVariable(name = "categoryId") categoryId: UUID,
  ):
    ResponseEntity<ShoppingList> {
    return ResponseEntity.ok(shoppingListService.deleteCategory(shoppingListId, categoryId))
  }

  @DeleteMapping("/shoppinglist/{id}/ingredient/{ingredientId}")
  fun deleteIngredient(
    @PathVariable(name = "id") shoppingListId: UUID,
    @PathVariable(name = "ingredientId") ingredientId: UUID,
  ):
    ResponseEntity<ShoppingList> {
    return ResponseEntity.ok(shoppingListService.deleteIngredient(shoppingListId, ingredientId))
  }
}
