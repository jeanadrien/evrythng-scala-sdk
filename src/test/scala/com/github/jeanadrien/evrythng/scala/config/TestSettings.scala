package com.github.jeanadrien.evrythng.scala.config

import com.typesafe.config.{Config, ConfigFactory}

/**
  *
  */
trait TestSettings {

    import TestSettings._

    val test = new {
        val operator = new {
            val apiKey = config.getString("test.operator.apiKey")
        }
    }
}

object TestSettings {

    lazy val config: Config = ConfigFactory.load("test.conf")

}
