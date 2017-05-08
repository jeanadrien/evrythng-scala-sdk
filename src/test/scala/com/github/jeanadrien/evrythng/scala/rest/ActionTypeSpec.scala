package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.ActionType
import org.specs2.concurrent.ExecutionEnv

import scala.concurrent.duration._
import scala.util.Random

/**
  *
  */
class ActionTypeSpec(implicit val ee : ExecutionEnv) extends TestOperatorContext {

    val timeout = 2500 milliseconds

    def is = sequential ^
        s2"""
            The ActionTypes CRD API is able
                        to create an ActionType                 $createActionType
                        to see it                               $findActionType
                        to delete an ActionType                 $deleteActionType
                        not see it anymore                      $missActionType
          """

    var actionType : ActionType = null;

    private def alphanumStr(length : Int) : String = Random.alphanumeric.take(length).mkString

    def createActionType = {
        val actionTypeName = s"_n${alphanumStr(10)}"
        operator.actions.create(ActionType(
            name = Some(actionTypeName)
        )).exec map { at =>
            actionType = at
            at.name.get
        } must beEqualTo(actionTypeName).await
    }

    def findActionType = operator.actions.list.exec map { page =>
        page.items
    } must contain(actionType).await

    def deleteActionType = operator.actions.remove(actionType.name.get).exec must beEqualTo(()).await

    def missActionType = operator.actions.list.exec map { page =>
        page.items
    } must not(contain(actionType)).await

}
