package rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.http.scaladsl.server.Route
import shop.Main.readAndSplit
import shop.{CategoryService, CookBookService, Menu}
import spray.json.DefaultJsonProtocol

object ApiRoute extends DefaultJsonProtocol {
  val cookbookFile = "data/cookbook_v2.txt"
  val categoryFile = "data/categoryDatabase_v2.csv"
  CategoryService.config(categoryFile)
  CookBookService.config(cookbookFile)
  val menuAndListOfExtras: (String, String) = readAndSplit("data/test/2808.txt")
  val menu: Menu = Menu(menuAndListOfExtras._1)

  def getRoute: Route = {

     val route =
      path("category" / Segment) { name =>
        get {
          complete(CategoryService.getCategoryByName(name))
        }
      } ~ path("categories") {
        get {
          complete(CategoryService.allCategories().values)
        }
      } ~ path("recipe" / Segment) { name =>
        get {
          complete(CookBookService.getRecipeByName(name))
        }
      } ~ path("menu") {
        get {
          complete(menu)
        }
      }
    route
  }

}
