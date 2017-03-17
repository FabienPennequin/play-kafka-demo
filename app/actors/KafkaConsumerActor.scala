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

import KafkaConsumerActor._

class KafkaConsumerActor @Inject()(implicit materializer: Materializer) extends Actor with ActorLogging {

  type KafkaMessage = ConsumerRecord[Array[Byte], String]

  override def preStart() = {
    Consumer.plainSource(consumerSettings(), Subscriptions.topics("topic1"))
      .runWith(Sink.actorRef(self, Status.Success(())))
    log.info("Started Kafka consumer actor")
  }

  def receive = {
    case Start =>
      // nothing to do, see pre-start

    case r: KafkaMessage =>
      log.info("Received kafka message: {}", r.value)
      log.debug("{}", r)

    case msg =>
      log.warning("Received unknown message:\n\t{}", msg)
  }

  private def consumerSettings() = {
    ConsumerSettings(context.system, new ByteArrayDeserializer, new StringDeserializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId(java.util.UUID.randomUUID().toString)
  }

}

object KafkaConsumerActor {
  case object Start
}
