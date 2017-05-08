package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.Product
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  *
  */
class ProductActionSpec(implicit val ee : ExecutionEnv) extends ActionSpecBase with BeforeAll {

    private var product : Product = null

    override def beforeAll() : Unit = {
        super.beforeAll()
        Await.ready({
            operator.products.create(Product(name = Some("aproduct"))).exec map (product = _)
        }, 10 seconds)
    }

    override def ctx = operator.product(product.id.get).actions

    override val ctxRoot : String = "/products/<id>/actions"

}
