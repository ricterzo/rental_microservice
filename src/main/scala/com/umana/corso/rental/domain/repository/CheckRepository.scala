package com.umana.corso.rental.domain.repository

import scala.concurrent.Future

trait CheckRepository {
  def check(): Future[Unit]
}
