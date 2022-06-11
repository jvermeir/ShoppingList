package nl.vermeir.shopapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.filter.CommonsRequestLoggingFilter
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
class ShopApiApplication

fun main(args: Array<String>) {
  runApplication<ShopApiApplication>(*args)
}

@RestController
class ManagementResource(
  val categoryService: CategoryService,
  val ingredientService: IngredientService,
  val recipeService: RecipeService,
  val recipeIngredientService: RecipeIngredientService,
  val messageService: MessageService,
  val menuService: MenuService
) {
  @PostMapping("/cleanup")
  fun post() {
    categoryService.deleteAll()
    ingredientService.deleteAll()
    recipeService.deleteAll()
    recipeIngredientService.deleteAll()
    messageService.deleteAll()
    menuService.deleteAll()
  }
}

@Configuration
class RequestLoggingFilterConfig {
  @Bean
  fun logFilter(): CommonsRequestLoggingFilter {
    val filter = CommonsRequestLoggingFilter()
    filter.setIncludeQueryString(true)
    filter.setIncludePayload(true)
    filter.setMaxPayloadLength(10000)
    filter.setIncludeHeaders(false)
    filter.setAfterMessagePrefix("REQUEST DATA : ")
    return filter
  }
}
