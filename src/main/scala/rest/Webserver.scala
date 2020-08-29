package rest

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import data.{Category, Ingredient, Recipe}
import shop.{CategoryService, CookBookService}
import spray.json.DefaultJsonProtocol

import scala.util.{Failure, Success}

object WebServer extends DefaultJsonProtocol {

  private def startHttpServer(routes: Route, system: ActorSystem[_]): Unit = {
    implicit val classicSystem: akka.actor.ActorSystem = system.toClassic
    import system.executionContext

    val futureBinding = Http().bindAndHandle(routes, "localhost", 8080)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  val cookbookFile = "data/cookbook_v2.txt"
  val categoryFile = "data/categoryDatabase_v2.csv"
  CategoryService.config(categoryFile)
  CookBookService.config(cookbookFile)

  def main(args: Array[String]): Unit = {

    val rootBehavior = Behaviors.setup[Nothing] { context =>

      implicit val categoryJsonFormat = jsonFormat2(Category.apply)
      implicit val ingredientJsonFormat = jsonFormat2(Ingredient.apply)
      implicit val recipeJsonFormat = jsonFormat2(Recipe.apply)

      val route =
      path("category" / Segment) { name =>
        get {
          complete(CategoryService.getCategoryByName(name))
        }
      } ~ path("categories") {
        get {
          complete(CategoryService.getAllCategories().values)
        }
      } ~ path("recipe"/ Segment) { name =>
        get {
          complete(CookBookService.getRecipeByName(name))
        }
      }

      startHttpServer(route, context.system)

      Behaviors.empty
    }
    val system = ActorSystem[Nothing](rootBehavior, "HelloAkkaHttpServer")

  }
}
