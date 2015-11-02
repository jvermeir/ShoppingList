trait MyConfig {
  def prompt:String
}

case class ImplicitTest(value: String)(implicit config: MyConfig) {
  val prompt = config.prompt
  override def toString: String = prompt + ":" + value
}

object ImplicitTest  {
  def apply(part1: String,part2: String)(implicit config: MyConfig): ImplicitTest = {
    new ImplicitTest(part1 + part2)
  }
  def apply(part1: String, part2: String,part3: String)(implicit config: MyConfig): ImplicitTest = {
    new ImplicitTest(part1 + part2 + part3)
  }
}

object Go {
  implicit object HelloConfig extends MyConfig {
    def prompt = "hello"
  }

  def main(args: Array[String]) {
    val instanceOfClass = new ImplicitTest("class value")
    println (instanceOfClass)
  }
}

object Go2 {
  implicit object HelloConfig extends MyConfig {
    def prompt = "hello2"
  }

  def main(args: Array[String]) {
    val usingTwoParamApplyInObject = ImplicitTest("aha", "!")
    println(usingTwoParamApplyInObject)
    val usingThreeParamApplyInObject = ImplicitTest("aha", "!", "?")
    println(usingThreeParamApplyInObject)
  }
}

object Go3 {
  implicit object HelloConfig2 extends MyConfig {
    def prompt = "hello"
  }
  case class ImplicitTest2(value: String)(implicit config: MyConfig) {
    val prompt = config.prompt
    override def toString: String = prompt + ":" + value
  }

  object ImplicitTest2  {
    def apply(part1: String,part2: String): ImplicitTest = {
      new ImplicitTest(part1 + part2)
    }
  }

  def main(args: Array[String]) {
    val instanceOfClass = new ImplicitTest2("class value")
    println (instanceOfClass)
    ImplicitTest2("through apply", "2")
  }
}


