package com.umana.corso.rental.domain.usecase.message

import com.umana.corso.rental.domain.exception.{ReserveMovieException}

object ReserveMessages {

  //region Reserve

  case class ReserveMovie(idUser: String, IdMovie: String, IdShop: String)
  case class ReserveMovieResponse(result: Either[ReserveMovieException, Unit])

  //endregion

}