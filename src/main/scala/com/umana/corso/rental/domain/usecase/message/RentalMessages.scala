package com.umana.corso.rental.domain.usecase.message

import com.umana.corso.rental.domain.exception.{RentMovieException, ReserveMovieException}
import com.umana.corso.rental.domain.model.Shop

object RentalMessages {

  //region Reserve

  case class ReserveMovie(idUser:String,IdMovie:String,IdShop:String)
  case class ReserveMovieResponse(result: Either[ReserveMovieException, Unit])

  //endregion

  //region Rent

  case class RentMovie(idUser:String,IdMovie:String,IdShop:String)
  case class RentMovieResponse(result: Either[RentMovieException, Unit])

  //endregion

}
