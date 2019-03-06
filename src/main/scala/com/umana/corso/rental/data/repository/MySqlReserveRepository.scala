package com.umana.corso.rental.data.repository

import com.umana.corso.rental.domain.exception._

import java.sql.{DriverManager}

import akka.actor.ActorSystem
import com.umana.corso.rental.domain.repository.ReserveRepository
import scala.concurrent.{ExecutionContext, Future}

class MySqlReserveRepository(
                           url: String,
                           user: String,
                           password: String,
                           system: ActorSystem
                         ) extends ReserveRepository {

  private implicit val executionContext: ExecutionContext = system.dispatchers.lookup("mysql-dispatcher")

  override def reserveMovie(idUser: String,idMovie: String,idShop: String): Future[Unit] = Future {
    var res = false
    try {
      val con = DriverManager.getConnection(url, user, password)
      try {
        val stmt = con.createStatement
        // esecuzione della query
        val sql = s"update moviecopy set status=1,reservationDate=NOW(),IdUser='$idUser' WHERE idMovie='$idMovie' AND idShop='$idShop' AND status=0 LIMIT 1"
        println(sql)
        if (stmt.executeUpdate(sql) == 1)
        // ciclo sul result set che contiene i risultati della query
          res = true
      }
      finally if (con != null) con.close()
    }

    if (res)
      Unit
    else
      throw new MovieNotAvailableForReserve()
  }


}