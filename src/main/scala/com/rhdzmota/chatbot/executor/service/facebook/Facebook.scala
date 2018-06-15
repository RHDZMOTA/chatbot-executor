package com.rhdzmota.chatbot.executor.service.facebook

import com.rhdzmota.pubsub.{PubSubConfig, PubSubConsumer}
import com.rhdzmota.chatbot.executor.model.config.FacebookConfig
import com.rhdzmota.chatbot.executor.util.ClientHttp
import com.rhdzmota.chatbot.executor.Settings

import akka.NotUsed
import akka.stream.scaladsl.RunnableGraph
import akka.stream.alpakka.googlecloud.pubsub.ReceivedMessage


import scala.util.{Failure, Success}

trait Facebook extends ClientHttp {
  def facebookConfig: FacebookConfig

  def transform: ReceivedMessage => Unit

  def send(data: String, apiKey: String): Unit =
    postRequest(Settings.Facebook.sendApiUrl + apiKey)(data).onComplete {
      case Failure(ex)       => println(s"- Failed to post:\n$data\n- Reason:\n$ex")
      case Success(response) => println(s"- Server responded with:\n$response")
    }

  def runnableGraph(pubSubConfig: PubSubConfig): RunnableGraph[NotUsed] =
    PubSubConsumer(pubSubConfig).subscribe(transform)(facebookConfig.subscription)
}
