package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.Collection
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

/**
  *
  */
class CollectionsInCollectionSpec (implicit val ee : ExecutionEnv) extends TestOperatorContext with BeforeAll {

    // test data

    var collectionA : Collection = null
    var collectionB : Collection = null
    var collectionC : Collection = null
    var parentCollection : Collection = null

    def randomCollection : Collection = Collection(
        name = Some(Random.nextString(20))
    )

    override def beforeAll() : Unit = {
        super.beforeAll()

        Await.ready(for {
            collectionA <- operator.collections.create(randomCollection).exec
            collectionB <- operator.collections.create(randomCollection).exec
            collectionC <- operator.collections.create(randomCollection).exec
            parentCollection <- operator.collections.create(randomCollection).exec
        } yield {
            this.collectionA = collectionA
            this.collectionB = collectionB
            this.collectionC = collectionC
            this.parentCollection = parentCollection

            logger.debug(s"CollectionA ${collectionA}")
            logger.debug(s"CollectionB ${collectionB}")
            logger.debug(s"CollectionC ${collectionC}")
            logger.debug(s"parentCollection ${parentCollection}")
        }, 10 seconds)
    }

    override def is = sequential ^
        s2""""
            Collections in collection manipulation API allows to
                add a collection to a collection                      ${addSomeCollections(collectionA::Nil)}
                and see it's there                                    $checkCollectionContent
                add multiple collections to a collection              ${addSomeCollections(collectionA::collectionB::collectionC::Nil)}
                and see they're all there but unique                  $checkCollectionContent
                remove a collection from a collection                 ${removeFromCollection(collectionB)}
                and see it has been removed                           $checkCollectionContent
                remove all collections from a collection              $removeAllFromCollection
                and see it is now empty                               $checkCollectionContent
          """

    private var contentSet = Set[Collection]()

    def addSomeCollections(collections : List[Collection]) =
        operator.collection(parentCollection.id.get).collections.add(collections.map(_.id.get)).exec.map { coll =>
            contentSet = contentSet ++ collections
            coll
        } must beEqualTo(()).await


    def checkCollectionContent = operator.collection(parentCollection.id.get).collections.read.exec.map { page =>
        page.items.map(_.copy(collections = None, updatedAt = None))
    } must containTheSameElementsAs(contentSet.toList.map(_.copy(collections = None, updatedAt = None))).await

    def removeFromCollection(collection : Collection) = operator.collection(parentCollection.id.get).collections.remove(collection.id.get).exec.map { _ =>
        contentSet = contentSet - collection
        ()
    } must beEqualTo(()).await

    def removeAllFromCollection = operator.collection(parentCollection.id.get).collections.removeAll.exec.map { _ =>
        contentSet = contentSet.empty
        ()
    } must beEqualTo(()).await
}
