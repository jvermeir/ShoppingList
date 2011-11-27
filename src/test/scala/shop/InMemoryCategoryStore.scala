package shop

trait CategoryTestingEnvironment extends InMemoryCategoryServiceComponent with InMemoryCategoryRepositoryComponent {
  val categoryRepository = new InMemoryCategoryRepository
  val categoryService = new CategoryService
}

trait InMemoryCategoryServiceComponent { this: InMemoryCategoryRepositoryComponent =>
  val categoryService: CategoryService
  class CategoryService {
    def getByName(name: String): Category = categoryRepository.getByName(name)
  }
}

trait InMemoryCategoryRepositoryComponent {
  val categories = Map[String, Category](
    "dranken" -> new Category("dranken", 10),
    "schoonmaak" -> new Category("schoonmaak", 20),
    "zuivel" -> new Category("zuivel", 30),
    "brood" -> new Category("brood", 40),
    "beleg" -> new Category("beleg", 45),
    "vlees" -> new Category("vlees", 50),
    "vis" -> new Category("vis", 60),
    "groente" -> new Category("groente", 70),
    "sauzen" -> new Category("sauzen", 75),
    "kruiden" -> new Category("kruiden", 76),
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
  val categoryRepository: InMemoryCategoryRepository
  class InMemoryCategoryRepository {
    def getByName(name: String): Category = {
      // TODO: remove nasty code duplication (from Category)
      val category = categories.get(name)
      category match {
        case Some(category) => category
        case _ => throw new PanicException("Category named " + name + " not found")
      }
    }
  }
}
