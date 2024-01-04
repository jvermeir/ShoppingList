package nl.vermeir.shopapi

import org.hamcrest.text.MatchesPattern
import java.time.LocalDate
import java.util.*

const val theId = "cb494d70-5c5c-45d4-ac66-9b84fe096fc6"
val march10th: LocalDate = LocalDate.parse("2022-03-10")
val march11th: LocalDate = LocalDate.parse("2022-03-11")

val objectMap = mutableMapOf<String, Any>()

fun <T> getFromMap(key: String): T {
  return objectMap[key]!! as T
}

val uuidPattern =
  MatchesPattern.matchesPattern("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\$")

val unknownId = UUID.randomUUID()
