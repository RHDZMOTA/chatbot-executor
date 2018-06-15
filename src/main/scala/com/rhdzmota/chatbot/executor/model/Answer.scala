package com.rhdzmota.chatbot.executor.model

import com.rhdzmota.fbmessenger.send.model.message.quickreply._

case class Answer(quickReply: QuickReply, next: Option[Node])
