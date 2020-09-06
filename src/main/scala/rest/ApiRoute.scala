package rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.http.scaladsl.server.Route
import shop.Main.readAndSplit
import shop.{CategoryService, CookBookService, Dates, Menu, ShoppingList}
import spray.json.DefaultJsonProtocol

object ApiRoute extends DefaultJsonProtocol with CORSHandler {
  val cookbookFile = "data/cookbook_v2.txt"
  val categoryFile = "data/categoryDatabase_v2.csv"
  CategoryService.config(categoryFile)
  CookBookService.config(cookbookFile)
  val menuAndListOfExtras: (String, String) = readAndSplit("data/test/2808.txt")
  var menu: Menu = Menu(menuAndListOfExtras._1)
  val shoppingList = new ShoppingList(menu, List())

  def getRoute: Route = {
    val categoryRoute =
      pathPrefix("category") {
        path(Segment) { name =>
          get {
            println(s"get - category: ${name}")
            complete(CategoryService.getCategoryByName(name))
          }
        } ~ path(Segment) { name =>
          put {
            println(s"put - category: ${name}")
            complete(CategoryService.getCategoryByName(name))
          }
        } ~ path(Segment) { name =>
          post {
            println(s"post - category: ${name}")
            complete(CategoryService.getCategoryByName(name))
          }
        } ~ path(Segment) { name =>
          delete {
            println(s"delete - category: ${name}")
            complete(CategoryService.getCategoryByName(name))
          }
        }
      }

    val menuRoute =
      pathPrefix("menu") {
        get {
          println(s"get - menu")
          complete(menu)
        } ~
          path("items" / Segment) { date =>
            delete {
              println(s"delete - menu item for date: ${date}")
              menu = Menu.newMenuWithADayRemoved(menu, Dates.parseIsoDateString(date).get)
              complete(menu)
            }
          }
      }

    val shoppingListRoute =
      pathPrefix("shoppinglist") {
        path(Segment) { name =>
          get {
            println(s"get - shoppinglist")
            complete(shoppingList)
          }
        }
      }

    val recipeRoute =
      pathPrefix("recipe") {
        path(Segment) { name =>
          get {
            println(s"get - recipe ${name}")
            complete(CookBookService.getRecipeByName(name))
          }
        }
      }

    val route =
      corsHandler(
        pathPrefix("api") {
          categoryRoute ~
            menuRoute ~
            shoppingListRoute ~
            recipeRoute ~
            path("categories") {
              get {
                complete(CategoryService.allCategories().values)
              }
            }
        }
      )

    //    val route =
    //      corsHandler(
    //        pathPrefix("api") {
    //          path("category" / Segment) { name =>
    //            get {
    //              println(s"get - category: ${name}")
    //              complete(CategoryService.getCategoryByName(name))
    //            }
    //          } ~ path("categories") {
    //            get {
    //              complete(CategoryService.allCategories().values)
    //            }
    //          } ~ path("menu") {
    //            get {
    //              println("get menu")
    //              complete(menu)
    //            }
    //          } ~ path("shoppinglist") {
    //            get {
    //              println("get shoppinglist")
    //              complete(shoppingList)
    //            }
    //          } ~ path("category" / Segment) { name =>
    //            put {
    //              println(s"put - category: ${name}")
    //              complete(CategoryService.getCategoryByName(name))
    //            }
    //          } ~ path("post" / Segment) { name =>
    //            post {
    //              println(s"post - category: ${name}")
    //              complete(CategoryService.getCategoryByName(name))
    //            }
    //          } ~ path("delete" / Segment) { name =>
    //            delete {
    //              println(s"delete - category: ${name}")
    //              complete(CategoryService.getCategoryByName(name))
    //            }
    //          } ~ path("recipe" / Segment) { name =>
    //            get {
    //              complete(CookBookService.getRecipeByName(name))
    //            }
    //          }
    //        })
    route
  }
}
