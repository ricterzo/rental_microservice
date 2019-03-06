package com.umana.corso.rental.domain.usecase.actor

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import akka.util.Timeout
import com.umana.corso.rental.domain.exception._

import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext
import com.umana.corso.rental.domain.repository.{CatalogueRepository, ReserveRepository, UserRepository}
import com.umana.corso.rental.domain.usecase.message.ReserveMessages._

class ReserveActor(rentRepository: ReserveRepository, userRepository: UserRepository, catalogueRepository: CatalogueRepository) extends Actor {

  private implicit val executionContext: ExecutionContext = context.system.dispatcher
  private implicit val timeout: Timeout = Timeout(5.seconds)

  override def receive: Receive = {

    case ReserveMovie(idUser,idMovie,idShop) =>
      catalogueRepository
        .getPriceByIdMovie(idMovie)
        .map(result => WrapGetPriceByIdMovieResponse(result,idUser,idMovie,idShop))
        .pipeTo(self)(sender)

    case WrapGetPriceByIdMovieResponse(None,_,_,_) =>
      sender() ! ReserveMovieResponse(Left(new MovieNotAvailableForReserve))

    case WrapGetPriceByIdMovieResponse(Some(price:Double),idUser,idMovie,idShop) =>
      userRepository
        .debit(idUser,price/2)
        .map(_ => WrapDebitResponse(Right(()),price,idUser,idMovie,idShop))
        .recover {
          case e: NotEnoughMoneyException => WrapDebitResponse(Left(e),price,idUser,idMovie,idShop)
        }
        .pipeTo(self)(sender)

    case WrapDebitResponse(Right(()),price:Double,idUser,idMovie,idShop) =>
      rentRepository
        .reserveMovie(idUser,idMovie,idShop)
        .map(_ => WrapReserveMovieResponse(Right(Unit),idUser,price))
        .recover {
          case e: ReserveMovieException => WrapReserveMovieResponse(Left(e),idUser,price)
        }
        .pipeTo(self)(sender())

    case WrapDebitResponse(Left(_:NotEnoughMoneyException),_,_,_,_) =>
      sender() ! Left(new NotEnoughMoneyException())

    case WrapReserveMovieResponse(Right(()),_,_) =>
      sender() ! ReserveMovieResponse(Right(Unit))

    case WrapReserveMovieResponse(Left(_:ReserveMovieException),idUser,price:Double) =>
      userRepository
        .credit(idUser,price/2)
        .map(_ => ReserveMovieResponse(Right(Unit)))
        .recover {
          case e: CreditException => ReserveMovieResponse(Left(new MovieNotAvailableForReserve))
        }
        .pipeTo(sender)

  }
  private case class WrapGetPriceByIdMovieResponse(result:Option[Double],idUser:String,idMovie:String,idShop:String)
  private case class WrapDebitResponse(result:Either[NotEnoughMoneyException,Unit], price:Double, idUser:String, idMovie:String, idShop:String)
  private case class WrapReserveMovieResponse(result:Either[ReserveMovieException,Unit],idUser:String, price:Double)
}
object ReserveActor {
  def props(reserveRepository: ReserveRepository, userRepository: UserRepository, catalogueRepository: CatalogueRepository): Props = Props(classOf[ReserveActor], reserveRepository,userRepository,catalogueRepository)
}

