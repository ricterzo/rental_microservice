package com.umana.corso.rental.domain.exception

sealed trait DebitException extends RuntimeException
class NotEnoughMoneyException extends DebitException
class IdNotFoundException extends DebitException
