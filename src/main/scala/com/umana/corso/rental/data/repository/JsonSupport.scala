package com.umana.corso.rental.data.repository

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.umana.corso.rental.domain.model.{ErrorCode, Price}
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val errorCodeJsonFormat = jsonFormat1(ErrorCode)
  implicit val priceJsonFormat = jsonFormat1(Price)
}
