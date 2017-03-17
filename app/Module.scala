package app

import javax.inject.Inject
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class Module extends AbstractModule with AkkaGuiceSupport {

  def configure() {
    bindActor[actors.KafkaConsumerActor](actors.Names.KafkaConsumer)
  }
}
