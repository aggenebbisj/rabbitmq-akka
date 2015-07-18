package com.example

import java.util.concurrent.{Executors, TimeUnit}

import akka.actor.ActorSystem
import akka.util.Timeout
import com.github.sstone.amqp.Amqp._
import com.github.sstone.amqp.{ChannelOwner, ConnectionOwner}
import com.rabbitmq.client.ConnectionFactory
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
 * Simple Producer sample. Start MyConsumer first, then run this class.
 */
object MyProducer extends App {
  implicit val system = ActorSystem("mySystem")
  implicit val timeout = Timeout(5 seconds)
  implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(50))

  val config = ConfigFactory.load().getConfig("amqp")
  val uri = s"amqp://${config.getString("user")}:${config.getString("pass")}@${config.getString("host")}"
  system.log.info(s"Connecting to uri: $uri")

  // create an AMQP connection
  val connFactory = new ConnectionFactory()
  connFactory.setUri(uri)
  val conn = system.actorOf(ConnectionOwner.props(connFactory, 1 second), "ConnectionOwner")
  val producer = ConnectionOwner.createChildActor(conn, ChannelOwner.props(), Some("ChannelOwner"))

  // wait till everyone is actually connected to the broker
  waitForConnection(system, conn, producer).await(5, TimeUnit.SECONDS)

  producer ! Publish("amq.direct", "my_key", "yo!!".getBytes, properties = None, mandatory = true, immediate = false)

  // give it some time before shutting everything down
  Thread.sleep(500)
  system.shutdown()
}