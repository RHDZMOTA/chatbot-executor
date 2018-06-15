package com.rhdzmota.chatbot.executor.model.config

import org.mongodb.scala.bson.ObjectId
import com.rhdzmota.chatbot.executor.model.{MongoModel, Node}

case class FacebookConfig(_id: String, apiKeyMap: Map[String, String], replyKeyMap: Map[String, Node], subscription: String, serviceId: String) extends MongoModel

object FacebookConfig {

  def createId: String = new ObjectId().toString

  def apply(apiKeyMap: Map[String, String], replyKeyMap: Map[String, Node], subscription: String, serviceId: String): FacebookConfig = {
    FacebookConfig(createId, apiKeyMap, replyKeyMap, subscription, serviceId)
  }
}