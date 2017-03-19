package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import services.Kafka
import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (
  kafkaService : Kafka
)(implicit ec: ExecutionContext) extends Controller {

  def subscribe(topic: String) = Action {
    kafkaService.subscribe(topic)
    Ok
  }

  def unsubscribe(topic: String) = Action {
    kafkaService.unsubscribe(topic)
    Ok
  }

  def send(topic: String) = Action.async(parse.text) { implicit request =>
    kafkaService.sendMessage(topic, request.body).map {
      case _ => Ok
    }
  }

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action { implicit request =>
    kafkaService.subscribe("topic1")
    Ok(views.html.index())
  }
}
