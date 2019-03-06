package com.umana.corso.rental.domain.repository

import scala.concurrent.Future

trait ReserveRepository {
  def reserveMovie(IdMovie:String,idUser:String,IdShop:String): Future[Unit]
}
