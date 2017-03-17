package actors

import javax.inject._
import play.api.Configuration

class KafkaConsumerConfiguration @Inject()(
  configuration: Configuration
) {

  private val defaultServer = "localhost:9092"

  lazy val server = kafkaconf.flatMap(_.getString("server")).getOrElse(defaultServer)
  lazy val clientId = kafkaconf.flatMap(_.getString("clientId"))

  private val kafkaconf = configuration.getConfig("kafka.consumer")

}
