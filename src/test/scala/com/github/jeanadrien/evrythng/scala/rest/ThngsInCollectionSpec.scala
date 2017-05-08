package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.{Collection, Thng}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

/**
  *
  */
class ThngsInCollectionSpec(implicit val ee : ExecutionEnv) extends TestOperatorContext with BeforeAll {

    // test data

    var thngA : Thng = null
    var thngB : Thng = null
    var thngC : Thng = null
    var collectionA : Collection = null

    def randomThng : Thng = Thng(
        name = Some(Random.nextString(20))
    )

    def randomCollection : Collection = Collection(
        name = Some(Random.nextString(20))
    )

    override def beforeAll() : Unit = {
        super.beforeAll()

        Await.ready(for {
            thngA <- operator.thngs.create(randomThng).exec
            thngB <- operator.thngs.create(randomThng).exec
            thngC <- operator.thngs.create(randomThng).exec
            collectionA <- operator.collections.create(randomCollection).exec
        } yield {
            this.thngA = thngA
            this.thngB = thngB
            this.thngC = thngC
            this.collectionA = collectionA

            logger.debug(s"ThngA ${thngA}")
            logger.debug(s"ThngB ${thngB}")
            logger.debug(s"ThngC ${thngC}")
            logger.debug(s"CollectionA ${collectionA}")
        }, 10 seconds)
    }

    override def is = sequential ^
        s2""""
            Collection thng manipulation API allows to
                add a thng to a collection                      ${addSomeThngs(thngA :: Nil)}
                and see it's there                              $checkCollectionContent
                add multiple thngs to a collection              ${addSomeThngs(thngA :: thngB :: thngC :: Nil)}
                and see they're all there but unique            $checkCollectionContent
                remove a thng from a collection                 ${removeFromCollection(thngB)}
                and see it has been removed                     $checkCollectionContent
                remove all thngs from a collection              $removeAllFromCollection
                and see it is now empty                         $checkCollectionContent
          """

    private var contentSet = Set[Thng]()

    def addSomeThngs(thngs : List[Thng]) =
        operator.collection(collectionA.id.get).thngs.add(thngs.map(_.id.get)).exec.map { coll =>
            contentSet = contentSet ++ thngs
            coll
        } must beEqualTo(collectionA).await


    def checkCollectionContent = operator.collection(collectionA.id.get).thngs.read.exec.map { page =>
        page.items.map(_.copy(properties = None, collections = None, updatedAt = None))
    } must containTheSameElementsAs(contentSet.toList
        .map(_.copy(properties = None, collections = None, updatedAt = None))).await

    def removeFromCollection(thng : Thng) = operator.collection(collectionA.id.get).thngs.remove(thng.id.get).exec
        .map { _ =>
            contentSet = contentSet - thng
            ()
        } must beEqualTo(()).await

    def removeAllFromCollection = operator.collection(collectionA.id.get).thngs.removeAll.exec.map { _ =>
        contentSet = contentSet.empty
        ()
    } must beEqualTo(()).await
}
