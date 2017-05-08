package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.Thng
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Random, Success}

/**
  *
  */
class ThngPaginationSpec(implicit val ee : ExecutionEnv) extends TestOperatorInScopeContext with BeforeAll {

    private var thngs : Seq[Thng] = Seq[Thng]()

    val timeout = 2500 milliseconds

    private def createThngs(howMuch : Int) = Seq.fill(howMuch) {
        operator.thngs.create {
            Thread.sleep(50)
            Thng(name = Some(Random.nextString(12)))
        }.exec andThen {
            case Success(t) =>
                thngs = t +: thngs
        }
    }

    override def beforeAll() : Unit = {
        // Create some thngs to iterate
        super.beforeAll()
        Await.ready(Future.sequence(
            createThngs(5)
        ), timeout)
    }

    private def normalizeComparison(t : Thng) = t.copy(properties = None, updatedAt = None)

    def is = sequential ^
        s2"""
        The Thng Pagination
            returns all 5 results by default                            $allResults
            understands perPage query parameter                         $perPageQp
            allows to iterate through the pages with Link header        $allowsToIterate
    """

    def allResults() = {
        operator.thngs.list().exec.map(_.items.map(normalizeComparison)) must containTheSameElementsAs(thngs
            .map(normalizeComparison)).await
    }

    def perPageQp() = {
        operator.thngs.list().perPage(2).exec.map(_.items.map(normalizeComparison)) must containTheSameElementsAs(
            thngs.sortBy(_.createdAt).reverse.take(2).map(normalizeComparison)
        ).await
    }

    def allowsToIterate() = {
        def iteratePages(current : EvtGetPageRequest[Thng], acc : Seq[Thng]) : Future[Seq[Thng]] = current
            .exec flatMap { page =>
            operator.nextPage(page) match {
                case Some(req) =>
                    iteratePages(req, acc ++ page.items)
                case None =>
                    Future.successful(acc ++ page.items)
            }
        }

        iteratePages(operator.thngs.list().perPage(2), Seq())
            .map(_.map(normalizeComparison)) must containTheSameElementsAs(thngs.map(normalizeComparison)).await
    }


}
