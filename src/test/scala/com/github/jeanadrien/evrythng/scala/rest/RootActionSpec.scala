package com.github.jeanadrien.evrythng.scala.rest
import org.specs2.concurrent.ExecutionEnv

/**
  *
  */
class RootActionSpec(implicit val ee : ExecutionEnv) extends ActionSpecBase {

    override def ctx = operator.actions

    override val ctxRoot : String = "/actions"

}
