package com.ryancui.akka.access.remote

import akka.actor.ActorSystem
import scala.concurrent.duration._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Send}
import com.typesafe.config.Config
import akka.pattern.ask
import akka.util.Timeout
import scala.util.{Failure, Success}


/**
  * Created by Ryan Cui on 2017/6/7.
  */
class AccessAgent private (system: ActorSystem, config: Config) {

  private val mediator = DistributedPubSub(system).mediator

  private val askDuration = 3000

  implicit private var timeout = Timeout(askDuration millisecond)

  def publish(topic: String, message: Object): Unit ={
    mediator ! Publish(topic, message)
  }

  /**
    * 发送消息并等待回执
    * @param message 要发送的消息
    */
  def sendMessage[T](message: Message): Option[T] = {
    var result: Option[T] = None

    // 如果message的过期时间小于1000毫秒则使用系统默认过期时间
    if (message.timeout >= 1000)
      timeout = Timeout(message.timeout millisecond)

    //    val feature = mediator.ask(message.body)
    val future = mediator ? Send(path = message.actorPath, msg = message.body, localAffinity = true)
    future.onComplete{
      case Success(value: T) ⇒ {
        result = Some[T](value)
      }
      case Failure(e) ⇒ e.printStackTrace()
    }
    result
  }
}