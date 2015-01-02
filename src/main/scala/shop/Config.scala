package shop

/**
 * Configurable objects for shop application
 */
trait Config {
  val categoryStore: CategoryStore
  val cookBookStore: CookBookStore
}
