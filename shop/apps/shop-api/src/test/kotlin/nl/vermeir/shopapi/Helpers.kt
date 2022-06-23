package nl.vermeir.shopapi

import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val baseUrl = "http://localhost:8080/api"

var logEnabled = false
fun log(theObject: Any) {
  if (logEnabled) {
    println(theObject)
  }
}

inline fun <reified T> save(data: T, path: String): T {
  val (_, _, result) = "${baseUrl}/${path}".httpPost().jsonBody(Json.encodeToString(data))
    .responseString()
  return Json.decodeFromString(result.get())
}

inline fun <reified T> saveJson(data: String, path: String): T {
  val (_, _, result) = "${baseUrl}/${path}".httpPost().jsonBody(data)
    .responseString()
  return Json.decodeFromString(result.get())
}

inline fun <reified T> load(path: String, params: Parameters = listOf()): T {
  val (_, _, result) = "${baseUrl}/${path}".httpGet(params).responseString()
  log(result.get())
  return Json.decodeFromString(result.get())
}

fun cleanUpDatabase() {
  val (_, _, result) = "${baseUrl}/cleanup".httpPost()
    .responseString()
  println(result)
}
