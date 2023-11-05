package nl.vermeir.shopapi

import kotlinx.datetime.LocalDate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.format.FormatterRegistry
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.filter.CommonsRequestLoggingFilter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


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
  val menuService: MenuService,
  val menuItemService: MenuItemService
) {
  @PostMapping("/cleanup")
  fun post() {
    categoryService.deleteAll()
    ingredientService.deleteAll()
    recipeService.deleteAll()
    recipeIngredientService.deleteAll()
    menuService.deleteAll()
    menuItemService.deleteAll()
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

class StringToDateConverter : Converter<String, LocalDate> {
  override fun convert(source: String): LocalDate {
    return LocalDate.parse(source)
  }
}

@Configuration
class WebConfig : WebMvcConfigurer {
  override fun addFormatters(registry: FormatterRegistry) {
    registry.addConverter(StringToDateConverter())
  }
}

