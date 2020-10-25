package shop

import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

import scala.util.Try

object Dates {
  val jsonDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  val ddMMyyyyFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyyyy")

  def dateToIsoString(date: LocalDateTime): String =
    jsonDateFormatter.format(date)

   def parseIsoDateString(date: String): Option[LocalDateTime] =
    Try {
      LocalDateTime.from(jsonDateFormatter.parse(date))
    }.toOption

  def parseddMMyyyyDateString(date: String): Option[LocalDateTime] =
    Try {
      LocalDate.from(ddMMyyyyFormatter.parse(date)).atStartOfDay
    }.toOption
}
