package nl.vermeir.shopapi

import com.github.kittinunf.fuel.httpGet
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.comparables.shouldBeEqualComparingTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.springframework.data.annotation.Id

@kotlinx.serialization.Serializable
data class Menu(@Id val id: String? = null, val firstDay: LocalDate)

class MenuIntegrationTest {
  @Test
  fun `a menu without id and all properties set is saved correctly and can be loaded`() {
    val menu1 = createAMenu(march10th)
    val menu1saved = save(menu1, path)
    menu1.firstDay shouldBeEqualComparingTo menu1saved.firstDay
    menu1saved.id shouldNotBe null
  }

  @Test
  fun `a menu should be updated`() {
    val menu1 = createAMenu(march10th)
    val menu1saved = save(menu1, path)
    val menu1savedAgain = save(menu1saved, path)
    menu1saved shouldBe menu1savedAgain
  }

  @Test
  fun `firstDay should be updated`() {
    val menu1 = createAMenu(march10th)
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
    val menu1 = createAMenu(march10th)
    val menu2 = createAMenu(august9th)
    val menu1saved = save(menu1, path)
    val menu2saved = save(menu2, path)
    val (_, _, result) = "${baseUrl}/menus".httpGet().responseString()
    val menus = Json.decodeFromString<List<Menu>>(result.get())
    menus shouldContainAll listOf(menu1saved, menu2saved)
  }

  @Test
  fun `a menu should be returned by findById`() {
    val menu1 = createAMenu(march10th)
    val menu1Saved = save(menu1, path)
    val menu1ById: Menu = load("menu/${menu1Saved.id}", listOf())
    menu1ById shouldBe menu1Saved
  }

  @Test
  fun `a menu should be returned by getByFirstDay`() {
    val menu1 = createAMenu(randomDate())
    val menu1Saved = save(menu1, path)
    val menu1ByFirstDay: Menu = load("menu", listOf(Pair("firstDay", menu1.firstDay)))
    menu1ByFirstDay shouldBe menu1Saved
  }

  companion object {
    private val path = "menu"
    private val march10th = "2022-03-10".toLocalDate()
    private val august9th = "2022-08-09".toLocalDate()
    private val january1th = "2022-01-01".toLocalDate()

    fun randomDate() = LocalDate((1972..2022).random(),(1..12).random(),(1..28).random())
  }
}
