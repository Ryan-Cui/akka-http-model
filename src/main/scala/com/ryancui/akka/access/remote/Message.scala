package com.ryancui.akka.access.remote

/**
  * Created by Ryan Cui on 2017/6/9.
  */
case class Message(actorPath: String, body: Object, timeout: Long)

object Message {
  def create(uri: String, body: Object): Message = {
    new Message(uri, body, 3000)
  }
}