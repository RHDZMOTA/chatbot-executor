package com.rhdzmota.chatbot.executor.service.facebook.reply

import akka.stream.alpakka.googlecloud.pubsub.ReceivedMessage
import io.circe.syntax._

import com.rhdzmota.chatbot.executor.model._
import com.rhdzmota.chatbot.executor.service.facebook._
import com.rhdzmota.fbmessenger.send.model.message.quickreply._
import com.rhdzmota.fbmessenger.send.model.implicits.Encoders._
import com.rhdzmota.fbmessenger.send.model.reply._

trait ReplyService extends Facebook {

  // Implement this method with the text message reply logic
  def textMessageResponse(recipient: Recipient, text: String, mainQuickReplyList: Option[List[QuickReply]]): List[Reply]

  // This method is called when a payload is received without providing a ReplyGraph
  def somethingWeirdResponse(recipient: Recipient): List[Reply]


  def receivedMessageOps(messageType: String, recipient: Recipient, replyGraphOption: Option[Node], attributes: Map[String, String]): List[Reply] = messageType match {
    case textType if textType contains "text" =>
      val mainQuickReplyList: Option[List[QuickReply]] = replyGraphOption.map(node => node.answers.map(list => list.map(_.quickReply))).flatten
      textMessageResponse(recipient, attributes.getOrElse("text", ""), mainQuickReplyList)
    case quickReplyType if quickReplyType contains "rply" => replyGraphOption match {
      case None             => somethingWeirdResponse(recipient)
      case Some(replyGraph) => (for {
        quickReplyPayload <- attributes.get("payload")
        nextNode          <- replyGraph.getNext(quickReplyPayload)
      } yield {
        nextNode.toMessageList.flatMap {possibleMessage =>
          possibleMessage match {
            case None          => List(None)
            case Some(message) =>
              // Note that the Reply object can return an Option[Reply]
              List(Some(Reply.withDefaultConfigMessage(recipient, message)))
          }
        }
      }.flatten).getOrElse(Nil)
    }
    case _ => somethingWeirdResponse(recipient)
  }

  val transform: ReceivedMessage => Unit = (receivedMessage: ReceivedMessage) => {
    val replyElements: Option[(List[Reply], String)] = for {
      attributes <- receivedMessage.message.attributes
      recipient  <- attributes.get("sender").map(Recipient)
      pageId     <- attributes.get("receiver")
      apiKey     <- facebookConfig.apiKeyMap.get(pageId)
    } yield receivedMessageOps(
      messageType = receivedMessage.message.data,
      recipient   = recipient,
      replyGraphOption = facebookConfig.replyKeyMap.get(pageId),
      attributes  = attributes
    ) -> apiKey
    replyElements match {
      case Some((replyList, apiKey)) =>
        println("replyList" + replyList.asJson.toString)
        replyList.foreach(reply => send(reply.asJson.toString, apiKey))
      case None => println("ReplyService >> replyElements >> None")
    }
  }

}