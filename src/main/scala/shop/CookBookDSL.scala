package shop

import scala.util.parsing.combinator.syntactical._
import scala.collection.mutable.ListBuffer
import Seasons._
import Categories._
import CookingTimeUnit._

// TODO: introduce 'en' keyword in list of ingredients 
// TODO: exception msg should not be null

class ParseErrorException extends java.lang.RuntimeException {
	def this(msg:String) = { this()
		println("msg: ${msg}")
	}
}

object CookBookDSL extends StandardTokenParsers {
  lexical.delimiters ++= List("(", ")", ",", " ", "\n")
  lexical.reserved += ("naam", "seizoen", "bak", "pak", "pot", "blik", "zakje", "stuks"
  										, "gram", "oven", "verwarmen", "op", "serveren", "met","toevoegen","graden", "categorie", "bereidingstijd")

  def parseCookBook(dsl:String):CookBook = {
		cookbook(new lexical.Scanner(dsl)) match {
		  case Success(recipeList, _) => new CookBook().addListOfRecipes(recipeList)
 		  case Failure(msg, _) => { println("in parse: " + msg ) 
		  throw new ParseErrorException(msg)}
		  case Error(msg, _) => throw new ParseErrorException(msg)
		}
	}
  def cookbook: Parser [List[Recipe]] =
    rep(recipe) ^^ { recipeList: List[Recipe] => recipeList }
  
	def recipe: Parser [Recipe] =
		recipeName ~ "seizoen" ~ listOfSeasons ~  category ~ cookingTime ~ listOfSteps ^^ {
     case name ~ "seizoen" ~ listOfSeasons ~  category ~ cookingTime ~ listOfSteps 
        => new Recipe(name, listOfSeasons, new Steps(listOfSteps), category, cookingTime) 
    }
	
	def listOfSteps: Parser [List[Step]] =
		rep(step)  ^^ { stepList: List[Step] => stepList}
		
  def recipeName: Parser[String] =
    "naam" ~ ident ^^ { case "naam" ~ name => name }

  def listOfSeasons: Parser[List[Seasons]] =
  repsep(season ,",") ^^ {listOfSeasons: List[Seasons] => listOfSeasons }

  def season: Parser[Seasons] = 
  ident ^^ { case season => Seasons.parse(season) }
  	
  def step: Parser[Step] = 
		(bakstep | ovenVerwarmenOpstep | serverenMetstep | toevoegenstep) ^^ { case step => step }
	
	def ovenVerwarmenOpstep: Parser[Step] = 
		"oven" ~ "verwarmen" ~ "op" ~ numericLit ~ "graden" ^^ { case "oven" ~ "verwarmen" ~ "op" ~ degrees ~ "graden" => (new OvenVerwarmenOpStep(degrees.toInt))}
  	
  def bakstep: Parser[Step] = 
  	"bak" ~ amount ~ ident ^^ { case "bak" ~ amount ~ ingredient => (new BakStep (new Ingredient(ingredient,amount)))}

  def toevoegenstep: Parser[Step] = 
  	"toevoegen" ~ amount ~ ident ^^ { case "toevoegen" ~ amount ~ ingredient => (new ToevoegenStep (new Ingredient(ingredient,amount)))}

  def serverenMetstep: Parser[Step] = 
  	"serveren" ~ "met" ~ listOfIngredients ^^ 
  		{ case "serveren" ~ "met" ~ ingredientList  => (new ServerenMetStep(ingredientList))}

	def listOfIngredients: Parser[List[Ingredient]] = 
		repsep(ingredient, ",") ^^ { ingredientList: List[Ingredient] => ingredientList }
		
	def ingredient: Parser[Ingredient] = 
		opt(amount) ~ ident ^^
  		{ case Some(amount) ~ ingredient => (new Ingredient(ingredient,amount))
  			case  _ ~ ingredient => {new Ingredient(ingredient)}
  		}
      
  def amount: Parser[Amount] = 
  	numericLit ~ ("pak"| "pot"| "blik"| "zakje"| "stuks"| "gram")  ^^ { case amount ~ amountUnit => (new Amount(amount.toInt, amountUnit.toString)) } 	
  
  def category: Parser[Categories] = 
    "categorie" ~ ident ^^ { case "categorie" ~ category => Categories.parse(category) }

  def cookingTime: Parser[CookingTime] = 
    "bereidingstijd" ~ numericLit ~ ident ^^ { case "bereidingstijd" ~ cookingTime ~ cookingTimeUnit
      => new CookingTime(cookingTime.toInt, CookingTimeUnit.parse(cookingTimeUnit)) }
} 

