package com.umana.corso.rental.domain.exception

sealed trait RentMovieException extends RuntimeException
class MovieNotAvailableForRent extends RentMovieException


