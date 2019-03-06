package com.umana.corso.rental.data.repository

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.umana.corso.rental.domain.model.{ErrorCode, Price}
import com.umana.corso.rental.domain.repository.UserRepository

import scala.concurrent.{ExecutionContext, Future}

class ApiUserRepository(url: String)(implicit system: ActorSystem) extends UserRepository with JsonSupport {

  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  override def debit(id: String, money: Double): Future[Unit] = {
    Http()
      .singleRequest(HttpRequest(uri = Uri(s"$url/users/debit").withQuery(Query("id" -> id, "cash" -> money.toString))))
      .flatMap {
        case HttpResponse(StatusCodes.OK, _, _, _) => Future.successful(Unit)
        case HttpResponse(StatusCodes.NotFound, _, entity, _) => {
          Unmarshal(entity).to[ErrorCode].flatMap(result =>
            if (result.code.equals("NotEnoughMoneyException"))
              Future.successful(Unit)
          else
          Future.failed(new RuntimeException("internal server error"))
          )
        }
        case HttpResponse(_, _, _, _) => Future.failed(new RuntimeException("internal server error"))
      }
  }

  override def credit(id: String, money: Double): Future[Unit] = {
    Http()
      .singleRequest(HttpRequest(uri = Uri(s"$url/users/credit").withQuery(Query("id" -> id, "cash" -> money.toString))))
      .flatMap {
        case HttpResponse(StatusCodes.OK, _, _, _) =>Future.successful(Unit)
        case HttpResponse(StatusCodes.NotFound, _, entity, _) =>
          Unmarshal(entity)
            .to[ErrorCode]
            .flatMap(result=>
          if (result.code=="InvalidIdException")
            Future.successful(Unit)
          else
              Future.failed(new RuntimeException("internal server error")))
        case HttpResponse(_, _, _, _) => Future.failed(new RuntimeException("internal server error"))
      }
  }


}
