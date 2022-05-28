package nl.vermeir.shopapi

import com.github.kittinunf.fuel.httpGet
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.datetime.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.Id
import java.util.*

@kotlinx.serialization.Serializable
data class Menu(@Id val id: String? = null, val firstDay: LocalDate)

class MenuIntegrationTest {
  @BeforeEach
  fun init() {
    cleanUpDatabase()
  }

  @Test
  fun `a menu without id and all properties set is saved correctly and can be loaded`() {
    val menu1saved = save(menu1, path)
    menu1.firstDay shouldBeEqualComparingTo  menu1saved.firstDay
    menu1saved.id shouldNotBe null
  }

  @Test
  fun `a menu should be updated`() {
    val menu1saved = save(menu1, path)
    val menu1savedAgain = save(menu1saved, path)
    menu1saved shouldBe menu1savedAgain
  }

  @Test
  fun `firstDay should be updated`() {
    val menu1saved = save(menu1, path)
    val menu1savedAgain = save(menu1saved.copy(firstDay = august9th), path)
    menu1savedAgain.firstDay shouldBeEqualComparingTo august9th
  }

  @Test
  fun `GET menu should return 404 when menu not found by id`() {
    val (_, response, _) = "${baseUrl}/menu/doesNotExist".httpGet().response()
    response.statusCode shouldBe 404
  }

  @Test
  fun `GET menu should return 404 when menu not found by firstDay`() {
    val (_, response, _) = "${baseUrl}/menu".httpGet(listOf(Pair("firstDay", january1th))).response()
    response.statusCode shouldBe 404
  }

  @Test
  fun `a list of menus should be returned`() {
    val menu1saved = save(menu1, path)
    val menu2saved = save(menu2, path)
    val (_, _, result) = "${baseUrl}/menus".httpGet().responseString()
    val menus = Json.decodeFromString<List<Menu>>(result.get())
    menus shouldBe listOf(menu1saved, menu2saved)
  }

  @Test
  fun `a menu should be returned by findById`() {
    val menu1 = save(menu1, path)
    val menu1ById:Menu = load("menu/${menu1.id}",listOf())
    menu1ById shouldBe menu1
  }

  @Test
  fun `a menu should be returned by getByFirstDay`() {
    val menu1 = save(menu1, path)
    val menu1ByFirstDay:Menu = load("menu",listOf(Pair("firstDay",menu1.firstDay)))
    menu1ByFirstDay shouldBe menu1
  }

  companion object {
    private val path = "menu"
    private val march10th = "2022-03-10".toLocalDate()
    private val august9th = "2022-08-09".toLocalDate()
    private val january1th = "2022-01-01".toLocalDate()
    private val menu1 = Menu(firstDay = march10th)
    private val menu2 = Menu(firstDay = august9th)
  }
}
