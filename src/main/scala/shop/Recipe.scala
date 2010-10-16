package shop
 
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Buffer
import scala.collection.jcl.ArrayList
import Seasons._
import Categories._
import CookingTimeUnit._

// TODO: write unit test

case class Recipe (name:String, seasons:List[Seasons], steps:Steps, category:Categories, cookingTime:CookingTime) {
	import Seasons._
	def this (name:String, seasons:List[Seasons]) = this(name,seasons, new Steps,Categories.Gemiddeld, new CookingTime(1,CookingTimeUnit.uren))
	def this () = this (null,null,new Steps,Categories.Gemiddeld, new CookingTime(1,CookingTimeUnit.uren))
	def this (name:String, seasons:List[Seasons], steps:List[Step], category:Categories) = { this (name, seasons, new Steps(steps), category, new CookingTime(1,CookingTimeUnit.uren))}

  override def toString =
  	"Recipe: (name="+name+",seasons="+seasons+")\n" + steps + printIngredients
 
 	def getIngredients:List[Ingredient] = {
 		val ingredients:ListBuffer[Ingredient] = new ListBuffer[Ingredient]
  	steps.list.foreach(step => step.findIngredients.foreach(ingredient => ingredients.+(ingredient)))
 	  ingredients.toList
 	}
 	
 	def printIngredients:String = {
 		val result:StringBuffer = new StringBuffer("Ingredients: ")
 		getIngredients.foreach(ingredient => result.append(ingredient).append("\n"))
 		result.toString
 	}
  def addStep(step:Step) = steps.add(step)
}

class Ingredient (name:String, amount:Amount) {
  override def toString = {
  	if (amount==null) name 
  	else name+":"+amount
  } 
  def this(name:String) = this(name,null)
}

class Amount (amount:Int, unitName:String) {
  override def toString = amount+":"+unitName
}

abstract class Step {
	protected val operation:String = "Step"
	
	def findIngredients:List[Ingredient];
}

abstract class StepWithAnIngredient(protected val ingredient:Ingredient) extends Step {
	
	def findIngredients:List[Ingredient] = {
		val ingredients:ArrayList[Ingredient] = new ArrayList[Ingredient]
		ingredients.add(ingredient)
		ingredients.toList
	}
	
	override def toString = operation+":"+ingredient
}

class BakStep(i:Ingredient) extends StepWithAnIngredient(i:Ingredient) {
  override val operation="bak"
}

class ToevoegenStep(i:Ingredient) extends StepWithAnIngredient(i:Ingredient) {
  override val operation="toevoegen"
}

class OvenVerwarmenOpStep(degrees:Int) extends Step {
	override val operation="ovenVerwarmenOp"
	
	override def findIngredients:List[Ingredient] = new ListBuffer().toList
		
  override def toString = operation+":"+degrees
}

class ServerenMetStep(i:List[Ingredient]) extends Step {
	override val operation="serverenMet"
  val ingredientList:Buffer[Ingredient] = new ListBuffer().++(i)
	
	override def findIngredients:List[Ingredient] = ingredientList.toList
			
  override def toString = operation+":"+ingredientList
}

class Steps {
	val list:Buffer[Step] = new ListBuffer
	def add(step:Step) = list+=step

	def this (listOfSteps:List[Step]) {
		this()
		list.appendAll(listOfSteps)
	}

  override def toString = {
  	val result:StringBuilder = new StringBuilder("Steps: ") 
  	
   	list.foreach(step => result.append(step).append("\n"))
  	result.toString()
  }	
}

class CookingTime(amount:Int, unit:CookingTimeUnit) {
  override def toString = amount + " " + unit
}

