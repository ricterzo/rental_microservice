package com.umana.corso.rental.domain.repository

import scala.concurrent.Future

trait UserRepository {

  def debit(id: String, money: Double): Future[Unit]
  def credit(id: String, money: Double): Future[Unit]

}
