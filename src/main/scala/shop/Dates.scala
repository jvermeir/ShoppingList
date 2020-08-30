package shop

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.util.Try

object Dates {
  val ddMMyyyyFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyyyy")

  def dateToIsoString(date: LocalDate): String =
    ddMMyyyyFormatter.format(date)

   def parseIsoDateString(date: String): Option[LocalDate] =
    Try {
      LocalDate.from(ddMMyyyyFormatter.parse(date))
    }.toOption

}
