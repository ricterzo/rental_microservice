package com.umana.corso.rental.domain.repository

import scala.concurrent.Future

trait RentRepository {
  def rentMovie(IdMovie:String,idUser:String,IdShop:String): Future[Unit]
  def checkPrenotation(IdMovie:String,idUser:String,IdShop:String): Future[Unit]
}
