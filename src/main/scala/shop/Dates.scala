package shop

import java.time.{LocalDate, ZoneOffset}
import java.time.format.DateTimeFormatter

import scala.util.Try

object Dates {
  val jsonDateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME // .ofPattern("yyyy-MM-dd")
  val jsonOutputDateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE // .ofPattern("yyyy-MM-dd")
  val ddMMyyyyFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("ddMMyyyy")

  def dateToIsoString(date: LocalDate): String = {
    println(jsonOutputDateFormatter.format(date))
    jsonOutputDateFormatter.format(date)
  }

   def parseIsoDateString(date: String): Option[LocalDate] =
    Try {
      LocalDate.from(jsonDateFormatter.parse(date))
    }.toOption

  def parseddMMyyyyDateString(date: String): Option[LocalDate] =
    Try {
      LocalDate.from(ddMMyyyyFormatter.parse(date))
    }.toOption

  def main(args: Array[String]): Unit = {
//    val x:LocalDate = LocalDate.now
    val x=LocalDate.now(ZoneOffset.UTC);
    println("x:" + x)
    val s = jsonDateFormatter.format(x)
    println ("s:" + s)

    val d = parseIsoDateString(s)
    println(d)
    val d2 = parseIsoDateString(s.substring(0,s.length-1))
    println(d2)

    val isoformatter = DateTimeFormatter.ISO_DATE
    val s2 = isoformatter.format(x)
    println(s2)

    val s3 = parseIsoDateString(s2)
    println(s3)
  }
}
