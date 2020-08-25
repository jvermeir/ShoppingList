package shop

class TestCookBookStore extends CookBookStore {
  override def load(): Unit = {
    println("test store load")
    readFromFile("data/test/cookbook_test.txt")
  }
}