package com.rhdzmota.chatbot.executor.model

import com.rhdzmota.fbmessenger.send.model.message._
import com.rhdzmota.fbmessenger.send.model.attachment._
import com.rhdzmota.fbmessenger.send.model.message.quickreply._

sealed trait MessageType {
  def toMessage(quickReplies: Option[List[QuickReply]]): Option[Message]
}

case class TextType(content: String) extends MessageType {
  def toMessage(quickReplies: Option[List[QuickReply]]): Option[Message] =
    Message.withText(content, quickReplies)
}

case class AttachmentType(content: Attachment) extends MessageType {
  def toMessage(quickReplies: Option[List[QuickReply]]): Option[Message] =
    Message.withAttachment(content, quickReplies)
}
