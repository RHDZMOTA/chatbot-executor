package com.rhdzmota.chatbot.executor.model.implicits


import com.rhdzmota.chatbot.executor.model._
import com.rhdzmota.chatbot.executor.model.config.FacebookConfig
import com.rhdzmota.fbmessenger.send.model.implicits.Decoders._

import io.circe.Decoder
import io.circe.generic.extras.Configuration
import io.circe.generic.extras.semiauto.deriveDecoder

object Decoders {
  implicit val customConfig: Configuration = Configuration.default.withSnakeCaseMemberNames.withDefaults
  // Answer
  implicit val decodeAnswer: Decoder[Answer] = deriveDecoder[Answer]
  // Node
  implicit val decodeNode: Decoder[Node] = deriveDecoder[Node]
  // Message Type
  implicit val decodeTextType: Decoder[TextType] = deriveDecoder[TextType]
  implicit val decodeAttachmentType: Decoder[AttachmentType] = deriveDecoder[AttachmentType]
  implicit val decodeMessageType: Decoder[MessageType] =
    Decoder[TextType].map[MessageType](identity).or(Decoder[AttachmentType].map[MessageType](identity))
  // FacebookConfig
  implicit val decodeFacebookConfig: Decoder[FacebookConfig] = deriveDecoder[FacebookConfig]
}