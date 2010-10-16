package shop
 
object CookingTimeUnit extends Enumeration {
	type CookingTimeUnit = Value
  val minuten = Value("minuten")
  val uren = Value("uren")

  def parse(value:String):CookingTimeUnit.Value=filter(value==_.toString).next
}

