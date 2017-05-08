package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.config.TestSettings
import com.typesafe.scalalogging.LazyLogging
import org.specs2._
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll


/**
  *
  */
abstract class TestOperatorContext extends Specification with BeforeAll with TestSettings with LazyLogging {
    implicit val ee : ExecutionEnv

    val env = Environment

    val operatorKey = test.operator.apiKey

    val operator : OperatorContext =
        env.operatorApi(operatorKey)

    override def beforeAll() : Unit = {
        // nop
    }

}

