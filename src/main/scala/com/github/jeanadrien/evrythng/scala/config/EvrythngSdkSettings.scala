package com.github.jeanadrien.evrythng.scala.config

import com.typesafe.config.{Config, ConfigFactory}

/**
  *
  */
trait EvrythngSdkSettings {

    import EvrythngSdkSettings._

    val settings = new {
        val evrythng = new {
            val api = config.getString("evrythng.api")
            val shortDomain = config.getString("evrythng.shortDomain")
        }

        val sdk = new {
            val httpClient = config.getString("sdk.httpClient")
        }
    }
}

object EvrythngSdkSettings {

    lazy val config: Config = ConfigFactory.load()

}
