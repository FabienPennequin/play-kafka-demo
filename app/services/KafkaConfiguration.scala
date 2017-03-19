package services

import javax.inject._
import play.api.Configuration

@Singleton
class KafkaConfiguration @Inject()(
  configuration: Configuration
) {

  private val defaultServer = "localhost:9092"

  lazy val producerServer = consumerConfig.flatMap(_.getString("server")).getOrElse(defaultServer)

  lazy val consumerServer = consumerConfig.flatMap(_.getString("server")).getOrElse(defaultServer)
  lazy val consumerClientId = consumerConfig.flatMap(_.getString("clientId"))

  private val producerConfig = configuration.getConfig("kafka.producer")
  private val consumerConfig = configuration.getConfig("kafka.consumer")

}
