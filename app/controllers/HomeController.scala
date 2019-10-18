package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json.Json

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index = Action { implicit request =>
    Ok(Json.obj("Hello" -> "This is Home Assignment Task"))
  }

  def ping = Action { implicit request =>
    Ok(Json.obj("ping" -> true))
  }
}
