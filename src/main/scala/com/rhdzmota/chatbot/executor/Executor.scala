package com.rhdzmota.chatbot.executor

import com.rhdzmota.chatbot.executor.model.implicits.Decoders._
import com.rhdzmota.chatbot.executor.model.config.{FacebookConfig, MongoConfig}
import com.rhdzmota.pubsub.PubSubConfig
import io.circe.parser.decode
import akka.stream.scaladsl.RunnableGraph
import akka.NotUsed
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import scala.concurrent.duration.Duration

import com.rhdzmota.chatbot.executor.service.facebook.reply.impl._

object Executor extends App with Context {

  // PUBSUB Configuration
  val pubSubConfigOpt: Option[PubSubConfig] = PubSubConfig.fromEnv(
    Settings.PubSub.privateKeyLabel,
    Settings.PubSub.projectIdLabel,
    Settings.PubSub.apiIdLabel,
    Settings.PubSub.serviceAccountEmailLabel)

  // Mongo Configuration
  val mongoConfigOpt: Option[MongoConfig] = MongoConfig.fromEnv

  // TODO Create a database service
  def getFacebookConfigSeq(mongoConfig: MongoConfig): (MongoClient, Future[Seq[Either[io.circe.Error, FacebookConfig]]]) = {
    System.setProperty("org.mongodb.async.type", "netty")
    val client: MongoClient = MongoClient(mongoConfig.getUri)
    val db: MongoDatabase   = client.getDatabase(mongoConfig.database)
    val collection: MongoCollection[Document] = db.getCollection("Facebook")
    client -> collection.find().toFuture().map { seqDocument => seqDocument.map {
        doc => decode[FacebookConfig](doc.toJson.toString)
      }
    }
  }

  for {
    pubSubConfig <- pubSubConfigOpt
    mongoConfig  <- mongoConfigOpt
  } yield {
    val (client, facebookConfigSeq) = getFacebookConfigSeq(mongoConfig)
    Await.ready(
      facebookConfigSeq.map(seqFbConfigEither => seqFbConfigEither.map(fbConfigEither => startService(fbConfigEither, pubSubConfig))), Duration.Inf)
      .onComplete {
      case Success(_) =>
        println("Success")
        client.close()
      case Failure(e) =>
          println("Future failure: " + e.toString)
          client.close()
    }
  }

  def runnableGraph(configService: FacebookConfig, pubsubConfig: PubSubConfig): (String, Option[RunnableGraph[NotUsed]]) = configService.serviceId match {
    case mirror if mirror contains "mirror" => mirror -> Some(Mirror(configService).runnableGraph(pubsubConfig))
    case seen   if seen   contains "seen"   => seen   -> Some(Seen(configService).runnableGraph(pubsubConfig))
    case invalid => invalid -> None
  }

  def startService(fbConfigEither: Either[io.circe.Error, FacebookConfig], pubsubConfig: PubSubConfig): NotUsed = fbConfigEither match {
    case Left(e) =>
      println("error: " + e.toString)
      NotUsed
    case Right(facebookConfig) => runnableGraph(facebookConfig, pubsubConfig) match {
      case (serviceId, None)   =>
        println(s"Service $serviceId not initialized")
        NotUsed
      case (_, Some(runnable)) => runnable.run()
    }
  }

}

