package com.umana.corso.rental.data.repository

import com.umana.corso.rental.domain.repository.CheckRepository
import java.sql.DriverManager

import akka.actor.ActorSystem
import com.umana.corso.rental.domain.model.Shop
import com.umana.corso.rental.domain.repository.ShopRepository

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

class MySqlCheckRepository (
                            url: String,
                            user: String,
                            password: String,
                            system: ActorSystem
                           ) extends CheckRepository {

  private implicit val executionContext: ExecutionContext = system.dispatchers.lookup("mysql-dispatcher")

  override def check(): Future[Unit] = Future{
    try {
      val con = DriverManager.getConnection(url, user, password)
      try {
        val stmt = con.createStatement
        // esecuzione della query
        val sql = "UPDATE moviecopy set reservationDate=null,status=0 WHERE reservationDate>date_add(NOW(),INTERVAL -5 DAY)"

        if (stmt.executeUpdate(sql) == 1)
          println("Azzerata Reserve Date")
      }
      finally if (con != null) con.close()
    }
    Unit
  }

}