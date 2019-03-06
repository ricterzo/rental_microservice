package com.umana.corso.rental.domain.usecase.actor

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import akka.util.Timeout
import com.umana.corso.rental.domain.exception._

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import com.umana.corso.rental.domain.repository.{CatalogueRepository, RentRepository, UserRepository}
import com.umana.corso.rental.domain.usecase.message.RentalMessages._

class RentalActor(rentRepository: RentRepository, userRepository: UserRepository, catalogueRepository: CatalogueRepository) extends Actor {

  private implicit val executionContext: ExecutionContext = context.system.dispatcher
  private implicit val timeout: Timeout = Timeout(5.seconds)

  override def receive: Receive = {


    case RentMovie(idUser,idMovie,idShop) =>
      catalogueRepository
        .getPriceByIdMovie(idMovie)
        .map(result => WrapGetPriceByIdMovieResponse(result,idUser,idMovie,idShop))
        .pipeTo(self)(sender)

    case WrapGetPriceByIdMovieResponse(None,_,_,_) =>
      sender() ! RentMovieResponse(Left(new MovieNotAvailableForRent))

    case WrapGetPriceByIdMovieResponse(Some(price:Double),idUser,idMovie,idShop) =>
      rentRepository
        .checkPrenotation(idUser,idMovie,idShop)
        .map(_=>WrapCheckReservationResponse(price/2,idUser,idMovie,idShop))
        .recover{
          case e:NoReservationWithThisId=>WrapCheckReservationResponse(price,idUser,idMovie,idShop)
        }
        .pipeTo(self)(sender)

    case WrapCheckReservationResponse(price:Double,idUser,idMovie,idShop) =>
      userRepository
        .debit(idUser,price)
        .map(_ => WrapDebitResponse(Right(()),price,idUser,idMovie,idShop))
        .recover {
          case e: NotEnoughMoneyException => WrapDebitResponse(Left(e),price,idUser,idMovie,idShop)
        }
        .pipeTo(self)(sender)

    case WrapDebitResponse(Right(()),price:Double,idUser,idMovie,idShop) =>
      rentRepository
        .rentMovie(idUser,idMovie,idShop)
        .map(_ => WrapRentMovieResponse(Right(Unit),price,idUser))
        .recover {
          case e: MovieNotAvailableForRent => WrapRentMovieResponse(Left(e),price,idUser)
        }
        .pipeTo(self)(sender())

    case WrapDebitResponse(Left(_:NotEnoughMoneyException),_,_,_,_) =>
      sender() ! Left(new NotEnoughMoneyException())

    case WrapRentMovieResponse(Right(()),_,_) =>
      sender() ! RentMovieResponse(Right(Unit))

    case WrapRentMovieResponse(Left(_:RentMovieException),price:Double,idUser) =>
      userRepository
        .credit(idUser,price)
        .map(_ => RentMovieResponse(Right(Unit)))
        .recover {
          case e: CreditException => RentMovieResponse(Left(new MovieNotAvailableForRent))
        }
        .pipeTo(sender)
  }
  private case class WrapGetPriceByIdMovieResponse(result:Option[Double],idUser:String,idMovie:String,idShop:String)
  private case class WrapCheckReservationResponse(result:Double,idUser:String,idMovie:String,idShop:String)
  private case class WrapDebitResponse(result:Either[NotEnoughMoneyException,Unit], price:Double, idUser:String, idMovie:String, idShop:String)
  private case class WrapRentMovieResponse(result:Either[MovieNotAvailableForRent,Unit],price:Double,idUser:String)
}
  object RentalActor {
    def props(rentRepository: RentRepository, userRepository: UserRepository, catalogueRepository: CatalogueRepository): Props = Props(classOf[RentalActor], rentRepository,userRepository,catalogueRepository)
  }

