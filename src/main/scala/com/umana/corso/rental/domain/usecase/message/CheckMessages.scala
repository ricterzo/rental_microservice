package com.umana.corso.rental.domain.usecase.message


object CheckMessages {

  case class CheckReserveDate()
  case class CheckReserveDateResponse(result: Unit)

}