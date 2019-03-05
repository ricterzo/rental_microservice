package com.umana.corso.rental.domain.exception

sealed trait CheckReserveDateException extends RuntimeException
class CheckException extends CheckReserveDateException
