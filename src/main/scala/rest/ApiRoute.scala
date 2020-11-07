package rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.http.scaladsl.server.Route
import shop.Main.readAndSplit
import shop._
import spray.json.DefaultJsonProtocol

object ApiRoute extends DefaultJsonProtocol with CORSHandler {
  val cookbookFile = "../ShoppingListData/cookbook_v2.txt"
  val categoryFile = "../ShoppingListData/categoryDatabase_v2.csv"
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
            complete(CategoryService.getCategoryByName(name))
          }
        } ~ path(Segment) { name =>
          put {
            complete(CategoryService.getCategoryByName(name))
          }
        } ~ path(Segment) { name =>
          post {
            complete(CategoryService.getCategoryByName(name))
          }
        } ~ path(Segment) { name =>
          delete {
            complete(CategoryService.getCategoryByName(name))
          }
        }
      }

    val menuRoute =
      pathPrefix("menu") {
        get {
          complete(menu)
        } ~
          post {
            entity(as[String]) { newMenu => {
              menu = Menu.fromJson(newMenu)
              complete("OK")
            }
            }
          } ~
          path("items" / Segment) { id =>
            delete {
              menu = Menu.newMenuWithARecordRemoved(menu, id)
              complete(menu)
            }
          } ~ path("items" / "add") {
          post {
            formFields(Symbol("id").as[String], Symbol("date").as[String], Symbol("dayOfWeek").as[String], Symbol("recipe").as[String]) {
              (id, date, dayOfWeek, recipe) => complete(Menu.newMenuWithADayAdded(menu, Menu.item(id, date, dayOfWeek, recipe)))
            }
          }
        } ~ path ("reset") {
          get {
            menu = Menu(menuAndListOfExtras._1)
            complete(menu)
          }
        }
      }

    val shoppingListRoute =
      pathPrefix("shoppinglist") {
        path(Segment) { name =>
          get {
            complete(shoppingList)
          }
        }
      }

    val recipeRoute =
      pathPrefix("recipe") {
        path(Segment) { name =>
          get {
            complete(CookBookService.getRecipeByName(name))
          }
        } ~ path("search" / Segment) { prefix =>
          get {
            complete(CookBookService.getRecipeByPrefix(prefix))
          }
        }
      }

    val route =
      corsHandler(
        ctx => {
          extractLog { log =>
            log.info("uri:{}, method:{}", ctx.request.uri, ctx.request.method)
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
          }(ctx)
        }
      )

    route
  }

}
