package shop.service

import akka.actor.Actor
import spray.http.MediaTypes._
import spray.json._
import spray.routing._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

//case class Color(name: String, red: Int, green: Int, blue: Int)
//
//object MyJsonProtocol extends DefaultJsonProtocol {
//  implicit val colorFormat = jsonFormat4(Color)
//}


case class Person(name: String, firstName: String, age: Int)

object PersonJsonProtocol extends DefaultJsonProtocol {
  implicit val personFormat = jsonFormat3(Person)
}


// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  import shop.service.PersonJsonProtocol._

  //  val json = Color("CadetBlue", 95, 158, 160).toJson
  //  val color = json.convertTo[Color]

  val json:JsValue = Person("hello", "World", 42).toJson
    val person:Person = json.convertTo[Person]

  val source:String = """{ "some": "JSON source" }"""
  val jsonAst:JsValue = source.parseJson

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to
                  <i>spray-routing</i>
                  on
                  <i>spray-can</i>
                  !</h1>
              </body>
            </html>
          }
        }
      }
    } ~
      path("myTest") {
        get {
          respondWithMediaType(`application/json`) {
            complete {
        println("print")
              json.prettyPrint
              //          myPerson.toJson.prettyPrint
            }
          }
        }
      }
}