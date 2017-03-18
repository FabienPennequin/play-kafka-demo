package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import akka.actor.ActorRef
import actors.KafkaConsumerActor._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (
  @Named(actors.Names.KafkaConsumer) kafkaConsumerActor: ActorRef
) extends Controller {

  def subscribe(topic: String) = Action {
    kafkaConsumerActor ! Subscribe(topic)
    Ok
  }

  def unsubscribe(topic: String) = Action {
    kafkaConsumerActor ! Unsubscribe(topic)
    Ok
  }

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action { implicit request =>
    kafkaConsumerActor ! Subscribe("topic1")
    Ok(views.html.index())
  }
}
