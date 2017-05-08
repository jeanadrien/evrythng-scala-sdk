package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.Thng
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  *
  */
class ThngActionSpec(implicit val ee : ExecutionEnv) extends ActionSpecBase with BeforeAll {

    private var thng : Thng = null

    override def beforeAll() : Unit = {
        super.beforeAll()
        Await.ready({
            operator.thngs.create(Thng(name = Some("athng"))).exec map (thng = _)
        }, 10 seconds)
    }

    override def ctx = operator.thng(thng.id.get).actions

    override val ctxRoot : String = "/thngs/<id>/actions"

}
