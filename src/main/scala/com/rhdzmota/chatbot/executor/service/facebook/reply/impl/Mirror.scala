package com.rhdzmota.chatbot.executor.service.facebook.reply.impl

import com.rhdzmota.chatbot.executor.model.config.FacebookConfig
import com.rhdzmota.chatbot.executor.service.facebook.reply.ReplyService
import com.rhdzmota.fbmessenger.send.model.message.quickreply._
import com.rhdzmota.fbmessenger.send.model.message._
import com.rhdzmota.fbmessenger.send.model.reply._

final case class Mirror(facebookConfig: FacebookConfig) extends ReplyService {
  def textMessageResponse(recipient: Recipient, text: String, mainQuickReplyList: Option[List[QuickReply]]): List[Reply] =
    List(Reply.withDefaultConfigMessage(recipient, WithText(text, mainQuickReplyList)))

  def somethingWeirdResponse(recipient: Recipient): List[Reply] =
    List(Reply.withDefaultConfigMessage(recipient, WithText("Something weird happened.", None)))
}
