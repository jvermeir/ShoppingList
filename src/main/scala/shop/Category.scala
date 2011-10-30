package shop
import org.apache.commons.io.FileUtils
import java.io.File

/**
 * Category represents an area of a shop and the order of the category in the optimal route
 * through the shop.
 */
class Category(val name: String, val sequence: Int) extends Ordered[Category] {
  /* 
   * Categories are considered equal if their sequences are equal.
   */
  def compare(that: Category) = sequence.compare(that.sequence)
}

object Category {
  def getByName(name: String): Category = categories(name)
  var categories = Map.empty[String, Category]
  def apply = {
    categories = Map[String, Category](
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
    categories
  }

  /*
   * Read list of category/order pairs from a text file. 
   * Each line should contain a string (the name of the category), 
   * a colon and an integer (the order of the category in a shop).
   */
  def loadCategoriesFromFile(fileName: String) = {
    val categoriesAsText = FileUtils.readFileToString(new File(fileName))
    loadCategoriesFromAString(categoriesAsText)
  }

  /*
   * Read list of category/order pairs from a text string. 
   * Each line should contain a string (the name of the category), 
   * a colon and an integer (the order of the category in a shop).
   */
  def loadCategoriesFromAString(categoriesAsText: String) = {
    val categoriesFromFile = for (line <- categoriesAsText.split("\n")) yield {
      val parts = line.split(":")
      (parts(0) -> new Category(parts(0), new Integer(parts(1))))
    }
    categories = categoriesFromFile.toList.toMap
  }
}