# Evrythng Scala SDK

This is *Evrythng Scala SDK*:
An unofficial Scala library for the [EVRYTHNG](http://evrythng.com) API. 

Its features include

* _Case Classes_ for the EVRYTHNG model, built with [spray-json](https://github.com/spray/spray-json).
* Low level asynchronous REST http wrapper.
* Higher level API browsing dsl.
* Abstraction on http client, and ready-to-use adapters for [Apache HttpComponents](https://hc.apache.org/httpcomponents-client-ga/) 
and Play Framework [WS Api](https://www.playframework.com/documentation/2.5.x/ScalaWS)

# Documentation

Evrythng Scala SDK gives and structures access to the resources available on the EVRYTHNG IoT Platform.
It allows a type safe interaction with the EVRYTHNG model, and an easy integration within Scala projects.

For a comprehensive documentation of the EVRYTHNG Api, its _Resources_ and corresponding REST endpoints, please refer to 
the official [EVRYTHNG Developer Hub](https://developers.evrythng.com/).

## Library dependency

Import Evrythng Scala SDK into your project with _Maven_ or _sbt_. 

For Maven:

``` 
<dependency>
    <groupId>com.github.jeanadrien</groupId>
    <artifactId>evrythng-scala-sdk_2.11</artifactId>
    <version>0.1.0</version>
</dependency>
```

For Sbt:

```
libraryDependencies += "com.github.jeanadrien" %% "evrythng-scala-sdk" % "0.1.0"
```

Don't forget to add the selected _http-client_ implementation, 
as well as a logger implementation. E.g. to use Play WS:


```
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"
libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.5.14"
```

*Note*: The library is currently only available for Scala 2.11.  

## Examples

The entry point of the sdk is the `Environment` object. It provides the basic REST request methods.
All model classes are available in the `com.github.jeanadrien.evrythng.scala.json` package.

### Make an http REST request

The simplest way to use the sdk is to use the REST method directly on your _Authenticated_ environment object.

The `get` `put` `post` and `delete` methods takes the expected json type as a _type parameter_, 
the target _endpoint_ and if applicable an _input_ as function parameter. 

They return a `EvtRequest` which can then
be manipulated e.g. to add query parameter. Then call the `exec` function to obtain a `Future` on its execution.
 
Note that you can use the model provided in the `json` pacakge as well as the spray `JsValue` class.

Here is an example using an _Operator API key_. 
You can create your own account and get your _Operation API key_ via the [EVRYTHNG Dashboard](https://dashboard.evrythng.com)


```scala

import com.github.jeanadrien.evrythng.scala.rest.Environment
import com.github.jeanadrien.evrythng.scala.json._
import EvtJsonProtocol._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

val env = Environment.operatorApi("MY-OPERATOR-API-KEY")

// Create an read a thng
(for {
    create <- env.post[Thng, Thng]("/thngs", Thng(
                  name = Some("This is a test")
              )).exec 
    read <- env.get[Thng](s"/thngs/${create.id.get.id}").exec
} yield read) onComplete {
    case Success(t) =>
        println(s"Loaded Thng '${t}'")
    case Failure(error) =>
        println(s"Got error: ${error}")    
}

```

### Structured API

Instead of building the _endpoint_ urls and possibly provides wrong json Type, 
you can use the structured API which builds the `EvtRequest` with the correct url type parameters. 
Each endpoints can be constructed by a natural method chaining, following the API endpoint structure.
 
* E.g. to access `/thngs/<id>/properties`

```
env.thng(Ref("Uk7h9Qb2BgPw9pwRwgcAhqDq")).properties.list.exec onComplete {
    case Success(t) =>
        println(s"Loaded ${t.items}")
    case Failure(error) =>
        println(s"Cannot load thng properties: ${error}")    
}
```

* Create a product _scan_ action `/products/<id>/actions/scans`

```
env.product(Ref("Uk7FRRBwBgsaQpRaRYHQ2Akn")).actions("scans").create(Action()).exec onComplete {
    case Success(a) =>
        println(s"Created ${a}")
    case Failure(error) =>
        println(s"Cannot create action: ${error}")    
}
```

The _Structured API_ logic is:

* The resource API is accessible by chaining the function named as the _path elements_ of the resource
* The resource API provides the http methods for _CRUD_ operations: `create`, `read`, `update` and `delete`. 
* Additionally to support the pagination a `list` operation returns a `EvtRequest` to iterate through pages 
(see example here below)

### Pagination

When a _GET_ requests returns a Json array. The EVRYTHNG Api limits the amount of results and provides
the next page link into the _Link_ http header.

The `list` method of the Structured API provides a `Page` object which encapsulates both the results and the possible
next page _link_. Simply pass the page to the `nextPage` method on your _Environment_ to get the next results:
 
```

(for {
  page1 <- env.thngs.list.perPage(2).exec
  page2 <- env.nextPage(page1).get.exec
} yield (page1, page2)) onComplete { case Success((page1, page2)) =>
        println(s"Page one contains ${page1.items}. Page two contains ${page2.items}")
    case Failure(error) =>
        println(s"Cannot list thngs: ${error}") 
}

```

### Http client implementation

By default the Evrythng Scala SDK uses the Play WS API as an http clients. It expects the corresponding jar to be available
 at runtime. 
  
If you wish to use an alternative implementation, inject the implementation class name in the property
 `sdk.httpClient`. You can use any other http client as long as it implements the _trait_
 `com.github.jeanadrien.evrythng.scala.rest.HttpRestClient`

E.g. if you want to use the packaged _Apache Http Components_ adapter:

```
$ sbt -Dsdk.httpClient="com.github.jeanadrien.evrythng.scala.httpclient.ApacheHttpClient"
```

### More examples

More examples can be found in the tests code.

Also the _Scala REPL_ tool and its autocompletion feature are useful to interact with the Evryhtng API using the sdk:

```console

$ sbt console

scala> import com.github.jeanadrien.evrythng.scala.rest._
import com.github.jeanadrien.evrythng.scala.rest._

scala> import com.github.jeanadrien.evrythng.scala.json._
import com.github.jeanadrien.evrythng.scala.json._

scala> val operator = Environment.operatorApi("MY-OPERATOR-API-KEY")
operator: com.github.jeanadrien.evrythng.scala.rest.OperatorContext = com.github.jeanadrien.evrythng.scala.rest.OperatorContext@22daf48c

scala> operator.thngs.read(Ref("Uk7h9Qb2BgPw9pwRwgcAhqDq")).exec.onSuccess { case thng => println(thng) }
Thng(Some(This is a test),None,None,None,None,Some(Map()),None,None,None,Some(Uk7h9Qb2BgPw9pwRwgcAhqDq),Some(1490550552982),Some(1490550552982))

```

## Contributing

All suggestions, contributions and questions are welcome via any channel, including Gitter.
Feature requests to support particular use cases are also welcome.

Please use this [code style](evrythng-scala-sdk.xml) file for IntelliJ

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

See LICENSE-2.0.txt.
