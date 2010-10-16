package shop
 
object Seasons extends Enumeration {
	type Seasons = Value
  val Zomer = Value("Zomer")
  val Winter = Value("Winter")
  val Herfst = Value("Herfst")
  val Lente = Value("Lente")
  val HeleJaar = Value("HeleJaar")

  def parse(value:String):Seasons.Value=filter(value==_.toString).next
}
