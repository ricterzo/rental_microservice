package com.umana.corso.rental.data.repository
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.umana.corso.rental.domain.model.Price
import com.umana.corso.rental.domain.repository.CatalogueRepository

import scala.concurrent.{ExecutionContext, Future}

class ApiCatalogueRepository(url: String)(implicit system: ActorSystem) extends CatalogueRepository with JsonSupport{
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  override def getPriceByIdMovie(id: String): Future[Option[Double]] = {
    Http()
      .singleRequest(HttpRequest(uri = Uri(s"$url/catalog/price").withQuery(Query("id" -> id))))
      .flatMap {
        case HttpResponse(StatusCodes.OK, _, entity, _) => Unmarshal(entity).to[Price].map{price => Some(price.value)}
        case HttpResponse(StatusCodes.NotFound, _, _, _) => Future.successful(None)
        case HttpResponse(_, _, _, _) => Future.failed(new RuntimeException("internal server error"))
      }
  }


}
