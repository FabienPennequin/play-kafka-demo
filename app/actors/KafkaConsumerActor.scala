package actors

import javax.inject._
import akka.actor._

import akka.stream._
import akka.stream.scaladsl._

import akka.kafka._
import akka.kafka.scaladsl._
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization._

import scala.collection.mutable.Set
import KafkaConsumerActor._

class KafkaConsumerActor @Inject()(
  config: KafkaConsumerConfiguration
)(implicit materializer: Materializer) extends Actor with ActorLogging {

  // mutable vars... yes... but inside an actor
  var currentKillSwitch: Option[KillSwitch] = None
  var topics: Set[String] = Set()

  def receive = {
    case Subscribe(topic) =>
      topics add topic
      refreshSubscription()

    case Unsubscribe(topic) =>
      topics remove topic
      refreshSubscription()

    case KafkaConsumerSubscribe =>
      subscribeToKafka()

    case r: KafkaMessage =>
      log.info("Received kafka message from topic '{}': '{}'", r.topic, r.value)
      log.debug("{}", r)

    case Status.Success(_) =>
      log.debug("Stream ended")

    case msg =>
      log.warning("Received unknown message:\n\t{}", msg)
  }

  private def refreshSubscription() = {
    currentKillSwitch.foreach(_.shutdown())
    self ! KafkaConsumerSubscribe
  }

  private def subscribeToKafka() = {
    if (!topics.isEmpty) {
      log.info("Subscribe on topics: {}", topics mkString ", ")

      val sink = Sink.actorRef(self, Status.Success(()))
      val (killSwitch, done) = Consumer.plainSource(consumerSettings(), Subscriptions.topics(topics.toSeq:_*))
        .viaMat(KillSwitches.single)(Keep.right)
        .toMat(sink)(Keep.both)
        .run()

      currentKillSwitch = Some(killSwitch)
    } else {
      currentKillSwitch = None
    }
  }

  private def consumerSettings() = {
    ConsumerSettings(context.system, new ByteArrayDeserializer, new StringDeserializer)
      .withBootstrapServers(config.server)
      // .withClientId(config.clientId.getOrElse("play-demo-kafka"))
      .withGroupId(java.util.UUID.randomUUID().toString)
  }

  type KafkaMessage = ConsumerRecord[Array[Byte], String]

  private case object KafkaConsumerSubscribe

}

object KafkaConsumerActor {
  case class Subscribe(topic: String)
  case class Unsubscribe(topic: String)
}
