package com.github.jeanadrien.evrythng.scala.rest

/**
  *
  */
trait ContextWithActions {

    def apply(actionType : String) : ActionContext

    def all = apply("all")

}
