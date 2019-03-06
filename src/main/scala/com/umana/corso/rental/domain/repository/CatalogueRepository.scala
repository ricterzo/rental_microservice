package com.umana.corso.rental.domain.repository

import scala.concurrent.Future

trait CatalogueRepository {
  def getPriceByIdMovie(id: String): Future[Option[Double]]
}