package com.rhdzmota.chatbot.executor

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}

import scala.concurrent.ExecutionContext

trait Context {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val executionContext = actorSystem.dispatcher
  implicit val materializer: Materializer = ActorMaterializer()
}
