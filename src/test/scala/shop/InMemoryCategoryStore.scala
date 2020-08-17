package shop

/**
 * In memory stores for use in test cases.
 */
class InMemoryCategoryStore extends CategoryStore {
  override def reload: Unit = {
    categoryMap.retain(((k, v) => false))
    categoryMap ++= loadCategoriesFromAString("""schoonmaak:20
            |dranken:10
            |OneMore:123
            |""".stripMargin)
  }
  override def save:Unit = {}
}

class InMemoryCookbookStore()(implicit val config:Config) extends CookBookStore {
  override def reload: Unit = {
    loadFromText("""naam:R1
                   |		  dranken:d1
                   |		  schoonmaak:s1
                   |
                   |
                   |
                   |		  naam:R2
                   |		  dranken:d2
                   |		  schoonmaak:s2
                   |
                   |		  naam:R0
                   |		  dranken:d0
                   |		  schoonmaak:s0
                   |
                   |		  naam:R3
                   |		  dranken:d0
                   |		  schoonmaak:s0
                   |
                   |		  naam:R4
                   |		  dranken:d0
                   |		  schoonmaak:s0
                   |
                   |		  naam:R5
                   |		  dranken:d0
                   |		  schoonmaak:s0
                   |
                   |		  naam:R6
                   |		  dranken:d0
                   |		  schoonmaak:s0
                   |""".stripMargin)
  }
  override def save:Unit = {}
}

