package shop

import java.util.Date

case class MenuItem (date:Date, dayOfWeek:String, recipe:String) {

}

object MenuItem {
  val dummyDate=new Date
  def apply (dayOfWeek:String, recipe:String): MenuItem = {
    return MenuItem(dummyDate, dayOfWeek, recipe)
  }
}
