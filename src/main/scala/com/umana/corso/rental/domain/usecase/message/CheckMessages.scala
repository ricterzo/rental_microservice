package com.umana.corso.rental.domain.usecase.message

object CheckMessages {

  //region CheckReserve Date

  case class CheckReserveDate()
  case class CheckReserveDateResponse(result: Unit)

  //endregion

}