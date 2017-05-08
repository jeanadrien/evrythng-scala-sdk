package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.EvtJsonProtocol._
import com.github.jeanadrien.evrythng.scala.json._
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

/**
  *
  */
class BootstrapKeyApi(val apiKey : String) extends Environment with AuthorizedEnvironment {

    def createIntegrationTestOperator() : Future[Ref] = {
        val customOperator =
            s"""
               |{
               |     "firstName" : "Integration",
               |     "lastName" : "Scala",
               |     "email" : "is.${Random.nextInt(200000)}@null.nwr",
               |     "password" : "Abc123123123%"
               |}
            """.stripMargin.parseJson
        post[JsValue, JsValue]("/operators", customOperator).exec.map { jsValue =>
            jsValue.asJsObject.fields.get("id").get.convertTo[Ref]
        }
    }

    def createIntegrationTestAccount() : Future[Ref] = {
        val customAccount =
            s"""
               |{
               |     "name" : "ScalaAccount-${Random.nextInt(2000)}"
               |}
            """.stripMargin.parseJson
        post[JsValue, JsValue]("/accounts", customAccount).exec.map { jsValue =>
            jsValue.asJsObject.fields.get("id").get.convertTo[Ref]
        }
    }

    def enableIntegrationTestModule(accountId : Ref) = {
        val json =
            """
              |{ "enabled" : true }
            """.stripMargin.parseJson
        put[JsValue, JsValue](s"/accounts/${accountId}/modules/base", json).exec
    }

    def createIntegrationTestContext(accountId : Ref, operatorId : Ref) : Future[AccountAccess] = {
        val url = s"/accounts/${accountId}/accesses"
        post[AccountAccess, AccountAccess](url, AccountAccess(
            operator = Some(operatorId),
            role = Some("admin")
        )).exec
    }


    def createIntegrationTestContext() : Future[AccountAccess] = for {
        operator <- createIntegrationTestOperator()
        account <- createIntegrationTestAccount()
        module <- enableIntegrationTestModule(account)
        access <- createIntegrationTestContext(account, operator)
    } yield {
        access
    }

}
