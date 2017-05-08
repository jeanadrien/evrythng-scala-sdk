package com.github.jeanadrien.evrythng.scala.rest

import org.specs2._
import org.specs2.concurrent.ExecutionEnv


/**
  *
  */
class AnonymousRestApiSpec(implicit val ee : ExecutionEnv) extends Specification {

    val anonymousApi = Environment.anonymousApi

    def is = sequential ^
        s2"""
        The Anonymous API allows to
            know what time is it           $whatTimeIsIt
    """

    def whatTimeIsIt = anonymousApi.time.read(tz = "Europe/Zurich").exec.map { t =>
        t.localTime.isDefined && t.nextChange.isDefined && t.offset.isDefined
    } must beTrue.await
}
