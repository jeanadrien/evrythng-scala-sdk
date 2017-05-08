package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.{Action, ActionType}
import org.specs2.specification.BeforeAll

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

/**
  *
  */
abstract class ActionSpecBase extends TestOperatorInScopeContext with BeforeAll {

    def ctx : ContextWithActions

    val ctxRoot : String

    val timeout = 2500 milliseconds

    def is = sequential ^
        s2"""
            The Action CRD API at '${ctxRoot}' can, without problem
                        create an Action                        $createAction1
                        create another action of another type   $createAction2
                        then see by type                        $listActionOfType1
                        or list them all                        $listAllActions
                        or get just one                         $loadSingleAction2
                        delete an action                        $deleteAction2
                        miss it                                 $missAction2
          """

    var actionType1 : ActionType = null;
    var actionType2 : ActionType = null;

    private def alphanumStr(length : Int) : String = Random.alphanumeric.take(length).mkString

    private def generateRandomActionType() = ActionType(
        name = Some(s"_n${alphanumStr(10)}")
    )

    override def beforeAll() : Unit = {
        super.beforeAll()
        Await.ready(for {
            actionType1 <- operator.actions.create(generateRandomActionType()).exec
            actionType2 <- operator.actions.create(generateRandomActionType()).exec
        } yield {
            this.actionType1 = actionType1
            this.actionType2 = actionType2

            logger.debug(s"actionType1 ${actionType1}")
            logger.debug(s"actionType2 ${actionType2}")

        }, 10 seconds)

    }

    var action1 : Action = null
    var action2 : Action = null

    val normalizeComparison = (a : Action) => a.copy(location = None, context = None)

    def createAction1 = {
        ctx(actionType1.name.get).create(Action()).exec map { action =>
            action1 = action
            action.`type`.get
        } must beEqualTo(actionType1.name.get).await
    }

    def createAction2 = {
        ctx(actionType2.name.get).create(Action()).exec map { action =>
            action2 = action
            action.`type`.get
        } must beEqualTo(actionType2.name.get).await
    }

    def listAllActions = ctx.all.list.exec map { page =>
        page.items.map(normalizeComparison)
    } must containTheSameElementsAs((action1 :: action2 :: Nil).map(normalizeComparison)).await

    def listActionOfType1 = ctx(actionType1.name.get).list.exec map { page =>
        page.items.map(normalizeComparison)
    } must containTheSameElementsAs((action1 :: Nil).map(normalizeComparison)).await

    def loadSingleAction2 = ctx(actionType2.name.get).read(action2.id.get).exec map { action =>
        normalizeComparison(action)
    } must beEqualTo(normalizeComparison(action2)).await

    def deleteAction2 = operator.actions(actionType2.name.get).remove(action2.id.get).exec must beEqualTo(()).await

    def missAction2 = ctx.all.read(action2.id.get).exec must throwAn[EvtRequestException].like {
        case e : EvtRequestException => e.evtError.status must beEqualTo(404)
    }.awaitFor(timeout)

}

