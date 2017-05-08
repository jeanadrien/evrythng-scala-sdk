package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.Product
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Random, Success}

/**
  *
  */
class ProductPaginationSpec(implicit val ee : ExecutionEnv) extends TestOperatorInScopeContext with BeforeAll {

    private var products : Seq[Product] = Seq[Product]()

    val timeout = 2500 milliseconds

    private def createProducts(howMuch : Int) = Seq.fill(5) {
        operator.products.create {
            Thread.sleep(50)
            Product(name = Some(Random.nextString(12)))
        }.exec andThen {
            case Success(t) =>
                products = t +: products
        }
    }

    override def beforeAll() : Unit = {
        // Create some products to iterate
        super.beforeAll()
        Await.ready(Future.sequence(
            createProducts(5)
        ), timeout)
    }

    private def normalizeComparison(t : Product) = t.copy(properties = None, updatedAt = None)


    def is = sequential ^
        s2"""
        The Product Pagination
            returns all 5 results by default                            $allResults
            understands perPage query parameter                         $perPageQp
            allows to iterate through the pages with Link header        $allowsToIterate
    """

    def allResults() = {
        operator.products.list().exec.map(_.items.map(normalizeComparison)) must containTheSameElementsAs(products
            .map(normalizeComparison)).await
    }

    def perPageQp() = {
        operator.products.list().perPage(2).exec.map(_.items.map(normalizeComparison)) must containTheSameElementsAs(
            products.sortBy(_.createdAt).reverse.take(2).map(normalizeComparison)
        ).await
    }

    def allowsToIterate() = {
        def iteratePages(current : EvtGetPageRequest[Product], acc : Seq[Product]) : Future[Seq[Product]] = current
            .exec flatMap { page =>
            operator.nextPage(page) match {
                case Some(req) =>
                    iteratePages(req, acc ++ page.items)
                case None =>
                    Future.successful(acc ++ page.items)
            }
        }

        iteratePages(operator.products.list().perPage(2), Seq())
            .map(_.map(normalizeComparison)) must containTheSameElementsAs(products.map(normalizeComparison)).await
    }
}
