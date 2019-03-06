package com.umana.corso.rental.domain.usecase.message

import com.umana.corso.rental.domain.exception.{RentMovieException}

object RentalMessages {

  //region Rent

  case class RentMovie(idUser:String,IdMovie:String,IdShop:String)
  case class RentMovieResponse(result: Either[RentMovieException, Unit])

  //endregion

}
