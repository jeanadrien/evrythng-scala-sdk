package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.EvtError

/**
  *
  */
class EvtRequestException(val evtError : EvtError) extends Exception(evtError.message)
