package services

import javax.inject._
import akka.actor.ActorRef

import actors.KafkaConsumerActor._

@Singleton
class Kafka @Inject()(
  @Named(actors.Names.KafkaConsumer) consumerActor: ActorRef
) {

  def subscribe(topic: String) = {
    consumerActor ! Subscribe(topic)
  }

  def unsubscribe(topic: String) = {
    consumerActor ! Unsubscribe(topic)
  }

}
