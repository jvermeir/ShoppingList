package rest

import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import shop.{Category, CategoryService, Recipe}
import spray.json.DefaultJsonProtocol

class TestRestInterface extends AnyWordSpecLike with Matchers with ScalatestRouteTest with DefaultJsonProtocol {
  val apiRoute: Route = ApiRoute.getRoute;

  "The Api` service" should {
    "return a '{\"name\":\"kruiden\",\"sequence\":40}' response for GET requests to /category/kruiden" in {
      Get("/category/kruiden") ~> apiRoute ~> check
      { status should be(OK)
        val category = Category.fromJson(responseAs[String])
        category should be(Category("kruiden", 40))
      }
    }

    "return a recipe response for GET requests to /recipe/Lasagne%20met%20gehakt" in {
      Get("/recipe/Lasagne%20met%20gehakt") ~> apiRoute ~> check
      { status should be(OK)
        val recipe = Recipe.fromJson(responseAs[String])
        recipe.name should be("Lasagne met gehakt")
        recipe.ingredients.size should be(7)
      }
    }
  }
}
