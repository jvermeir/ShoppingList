package shop.service

import org.specs2.mutable.Specification
import spray.http.StatusCodes._
import spray.json._
import spray.testkit.Specs2RouteTest


case class Person(name: String, firstName: String, age: Int) //(implicit val i:Int)

object PersonJsonProtocol extends DefaultJsonProtocol {
  implicit val personFormat = jsonFormat3(Person)
}

class MyServiceSpec extends Specification with Specs2RouteTest with MyService {
  def actorRefFactory = system

  import PersonJsonProtocol._

  "MyService" should {

    "return a greeting for GET requests to the root path" in {
      Get() ~> myRoute ~> check {
        responseAs[String] must contain("Say hello")
      }
    }

    "return a JSON formatted Person for GET requests to /myTest" in {
      Get("/myTest") ~> myRoute ~> check {
        val personAsString: String = responseAs[String]
        val personAsJson:JsValue = personAsString.parseJson
        val person:Person = personAsJson.convertTo[Person]

        person === Person("hello", "World", 42)
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }
  }
}
