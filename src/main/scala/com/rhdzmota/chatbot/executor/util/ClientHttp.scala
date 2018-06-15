package com.rhdzmota.chatbot.executor.util

import akka.http.scaladsl.model._
import akka.http.scaladsl.Http
import akka.stream.alpakka.googlecloud.pubsub.ReceivedMessage

import io.circe.syntax._

import com.rhdzmota.fbmessenger.send.model._
import com.rhdzmota.fbmessenger.send.model.message.quickreply._
import com.rhdzmota.fbmessenger.send.model.implicits.Encoders._
import com.rhdzmota.fbmessenger.send.model.reply._
import com.rhdzmota.fbmessenger.send.model.message._
import com.rhdzmota.chatbot.executor.Context

import scala.concurrent.Future
import scala.util.{Failure, Success}


trait ClientHttp extends Context{
  def postRequest(targetUrl: String)(data: String): Future[HttpResponse] =
    Http(actorSystem).singleRequest(
      HttpRequest(
        HttpMethods.POST,
        targetUrl,
        entity = HttpEntity(ContentTypes.`application/json`, data)
      ))
}
