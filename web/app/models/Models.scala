package models

import java.util.{Date}

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._
import shop._

case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object CategoryWeb {
	val categoryClient = new CategoryClient(FileBasedCategoryConfig)

    def categoryList(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = ""): Page[Category] = {
	  val cats = categoryClient.getCategories
	  Page(cats.values.toSeq , page, 1, cats.size)
	}
	
	def insert(category:Category) = {
	  categoryClient.add(category)
	}
}

