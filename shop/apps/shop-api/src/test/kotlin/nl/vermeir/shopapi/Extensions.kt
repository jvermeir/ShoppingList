package nl.vermeir.shopapi

import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder
import java.util.*

fun LocalDateTime.format(): String = this.format(dateFormatter)

private val dateFormatter = DateTimeFormatterBuilder()
  .appendPattern("yyyy-MM-dd")
  .toFormatter(Locale.ENGLISH)
