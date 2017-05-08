package com.github.jeanadrien.evrythng.scala.rest

import java.net.URI

import com.github.jeanadrien.evrythng.scala.json.{Redirection, Thng}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

/**
  *
  */
class RedirectionCrudSpec(implicit val ee: ExecutionEnv) extends TestOperatorContext with BeforeAll {

    def is = sequential ^ s2"""
        The Redirection API offers to
            create a Redirection                            $createARedirection
            and read it                                     $readARedirection
            obtain its qrCode                               $obtainAQrCode
            update it                                       $updateARedirection
            see the changes                                 $readARedirection
            perform a lookup                                $performALookup
            anonymously see a redirection                   $anonSeeARedirection
            delete the redirection                          $deleteARedirection
    """

    var aThng : Thng = null

    override def beforeAll() : Unit = {
        super.beforeAll()

        Await.ready(
            operator.thngs.create(
                Thng(name = Some(Random.nextString(20)))
            ).exec.map(aThng = _),
            5 seconds
        )
    }

    var theRedirection : Redirection = null

    var compare = (r : Redirection) => r.copy(updatedAt = None, hits = None)

    def createARedirection = {
        val redirectionUrl = s"http://github.com/${Random.alphanumeric.take(5).mkString}"
        val expectedShortDomain = new URI(env.settings.evrythng.shortDomain).getHost

        operator.redirections.create(Redirection(
            evrythngId = aThng.id,
            defaultRedirectUrl = Some(redirectionUrl)
        )).exec.map { r =>
            theRedirection = r
            (r.shortDomain.get, r.defaultRedirectUrl.get)
        } must beEqualTo((expectedShortDomain,redirectionUrl)).await
    }

    def readARedirection =
        operator.redirections.read(theRedirection.shortId.get).exec.map(compare) must beEqualTo(compare(theRedirection)).await

    def obtainAQrCode =
        operator.redirections.qr(theRedirection.shortId.get).exec must not be empty.await

    def updateARedirection = {
        val updatedUrl = s"http://perdu.com/${Random.alphanumeric.take(5).mkString}"
        operator.redirections.update(theRedirection.shortId.get, Redirection(
            defaultRedirectUrl = Some(updatedUrl)
        )).exec.map { updated =>
            theRedirection = updated
            updated.defaultRedirectUrl.get
        } must beEqualTo(updatedUrl).await
    }

    def performALookup =
        operator.redirections.lookup(aThng.id.get).exec map(_.items.map(compare)) must
            containTheSameElementsAs((theRedirection::Nil).map(compare)).await

    def anonSeeARedirection = env.anonymousApi.shortId(theRedirection.shortId.get).exec map(compare) must
        beEqualTo(compare(theRedirection)).await

    def deleteARedirection = operator.redirections.remove(theRedirection.shortId.get).exec must beEqualTo(()).await
}
