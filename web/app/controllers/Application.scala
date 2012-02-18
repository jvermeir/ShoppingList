package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.validation.Constraints._
import views._
import models._
import shop._

/**
 * Manage a database of computers
 */
object Application extends Controller { 
  
  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Application.listCategories(0, 2, ""))
   
  val categoryForm = Form(
      of(Category.apply _) (
          "name" -> requiredText,
          "sequence" -> number
          )
      )

      /**
       * Handle default path requests, redirect to categories list
       */  
      def index = Action { Home }
  
  def listCategories(page: Int, orderBy: Int, filter: String) = Action { implicit request =>
    CategoryWeb.categoryList(page = page, orderBy = orderBy, filter = ("%"+filter+"%"))
    Ok(html.categoryList( 
      CategoryWeb.categoryList(page = page, orderBy = orderBy, filter = ("%"+filter+"%")),
      orderBy, filter
    ))
  }
    /**
   * Display the 'new category form'.
   */
  def createCategory = Action {
    Ok(html.categoryCreateForm(categoryForm))
  }
  
  def saveCategory = Action { implicit request =>
    categoryForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.categoryCreateForm(formWithErrors)),
      category => {
        CategoryWeb.insert(category)
        Home.flashing("success" -> "Category %s has been created".format(category.name))
      }
    )
  } 
}
            