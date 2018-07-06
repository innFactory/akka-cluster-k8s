package de.innfactory.akkaclusterk8s

import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor.ActorLogging
import akka.actor.Actor

case class GetEvents()
case class Result(events: Vector[String])

class SimpleClusterListener extends Actor with ActorLogging {

  private var events : Vector[String] = Vector.empty

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case e: MemberEvent => log.info(s"EVENT: $e"); events = events :+ e.toString
    case _: GetEvents => sender() ! Result(events)
  }
}
