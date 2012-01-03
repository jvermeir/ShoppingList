package shop

class SmallCategoryTestRepository extends CategoryRepository {
  var categories = Map[String, Category](
    "test" -> new Category("test", 10),
    "groente" -> new Category("groente", 20),
    "pasta" -> new Category("pasta", 30))
	def add(category:Category) = throw new shop.OperationNotSupportedException("SmallCategoryTestRepository does not support add operation")
}

object SmallCategoryTestConfig {
  lazy val categoryRepository = new SmallCategoryTestRepository
}

class LargeCategoryTestRepository extends CategoryRepository {
  var categories = Map[String, Category](
    "dranken" -> new Category("dranken", 10),
    "schoonmaak" -> new Category("schoonmaak", 20),
    "zuivel" -> new Category("zuivel", 30),
    "brood" -> new Category("brood", 40),
    "beleg" -> new Category("beleg", 45),
    "vlees" -> new Category("vlees", 50),
    "vis" -> new Category("vis", 60),
    "groente" -> new Category("groente", 70),
    "sauzen" -> new Category("sauzen", 75),
    "blik" -> new Category("blik", 77),
    "koffie" -> new Category("koffie", 80),
    "snoep" -> new Category("snoep", 90),
    "pasta" -> new Category("pasta", 100),
    "rijst" -> new Category("rijst", 110),
    "olie" -> new Category("olie", 120),
    "meel" -> new Category("meel", 130),
    "soep" -> new Category("soep", 140),
    "chips" -> new Category("chips", 150),
    "diepvries" -> new Category("diepvries", 160),
    "zeep" -> new Category("zeep", 170))
  def add(category: Category) = throw new shop.OperationNotSupportedException("LargeCategoryTestRepository does not support add operation")

}

object LargeCategoryTestConfig {
  lazy val categoryRepository = new LargeCategoryTestRepository
}

