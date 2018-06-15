package com.rhdzmota.chatbot.executor

import com.typesafe.config.{Config, ConfigFactory}

object Settings {
  private val app: Config = ConfigFactory.load().getConfig("application")

  object PubSub {
    private val pubsub: Config = app.getConfig("pubsub")
    val privateKeyLabel: String = pubsub.getString("privateKeyLabel")
    val projectIdLabel: String = pubsub.getString("projectIdLabel")
    val apiIdLabel: String = pubsub.getString("apiIdLabel")
    val serviceAccountEmailLabel: String = pubsub.getString("serviceAccountEmailLabel")
    val fbTopic = pubsub.getString("fbTopic")
  }

  object Facebook {
    private val fb: Config = app.getConfig("facebook")
    val sendApiUrl: String = fb.getString("sendApiUrl")
  }
}
