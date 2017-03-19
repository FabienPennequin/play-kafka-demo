package services

import javax.inject._

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._

import akka.kafka._
import akka.kafka.scaladsl._
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization._

import actors.KafkaConsumerActor._

@Singleton
class Kafka @Inject()(
  system: ActorSystem,
  @Named(actors.Names.KafkaConsumer) consumerActor: ActorRef
)(implicit materializer: Materializer) {

  def subscribe(topic: String) = {
    consumerActor ! Subscribe(topic)
  }

  def unsubscribe(topic: String) = {
    consumerActor ! Unsubscribe(topic)
  }

  def sendMessage(topic: String, message: String) = {
    val producerSettings = ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers("localhost:9092")

    Source.single(message)
      .map { elem =>
        new ProducerRecord[Array[Byte], String](topic, elem)
      }
      .runWith(Producer.plainSink(producerSettings))
  }

}
