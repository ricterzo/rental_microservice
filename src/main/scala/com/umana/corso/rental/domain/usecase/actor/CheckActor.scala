package com.umana.corso.rental.domain.usecase.actor


import com.umana.corso.rental.domain.repository.CheckRepository
import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.umana.corso.rental.domain.usecase.message.CheckMessages.{CheckReserveDate, CheckReserveDateResponse}
import akka.pattern.pipe
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext


class CheckActor(checkRepository: CheckRepository) extends Actor {
  private implicit val executionContext: ExecutionContext = context.system.dispatcher
  private implicit val timeout: Timeout = Timeout(60.seconds)


  override def preStart(): Unit = {
    context.system.scheduler.scheduleOnce(5 seconds, self, CheckReserveDate())
  }

  override def receive: Receive = {
    case CheckReserveDate() =>
      checkRepository
        .check()
        .map(_ => CheckReserveDateResponse(Unit))
        .pipeTo(self)

    case CheckReserveDateResponse(()) =>
      context.system.scheduler.scheduleOnce(60 seconds, self, CheckReserveDate())
  }
}

object CheckActor {
  def props(checkRepository: CheckRepository): Props = Props(classOf[CheckActor], checkRepository)
}