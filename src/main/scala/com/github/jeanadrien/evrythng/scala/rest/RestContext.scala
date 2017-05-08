package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.Ref

/**
  *
  */
trait RestContext {

    def projectScope : Option[Ref]
}
