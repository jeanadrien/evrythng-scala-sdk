package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.Collection
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  *
  */
class CollectionActionSpec(implicit val ee : ExecutionEnv) extends ActionSpecBase with BeforeAll {

    private var collection : Collection = null

    override def beforeAll() : Unit = {
        super.beforeAll()
        Await.ready({
            operator.collections.create(Collection(name = Some("acollection"))).exec map (collection = _)
        }, 10 seconds)
    }

    override def ctx = operator.collection(collection.id.get).actions

    override val ctxRoot : String = "/collections/<id>/actions"


}
