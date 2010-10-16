package shop

object Categories extends Enumeration {
	type Categories = Value
  val Eenvoudig = Value("Eenvoudig")
  val Gemiddeld = Value("Gemiddeld")
  val Moeilijk = Value("Moeilijk")
  
  def parse(value:String):Categories.Value=filter(value==_.toString).next
}

