package shop

import org.joda.time.DateTime

case class MenuItem (date:DateTime, dayOfWeek:String, recipe:String)
//
//object MenuItem {
//  val dummyDate=new DateTime()
//  def apply (dayOfWeek:String, recipe:String): MenuItem = {
//    return MenuItem(dummyDate, dayOfWeek, recipe)
//  }
//}
