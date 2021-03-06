package com.umana.corso.rental.data.repository

import com.umana.corso.rental.domain.repository.CheckRepository
import java.sql.DriverManager

import akka.actor.ActorSystem
import scala.concurrent.{ExecutionContext, Future}
import java.util.Calendar

class MySqlCheckRepository (
                            url: String,
                            user: String,
                            password: String,
                            system: ActorSystem
                           ) extends CheckRepository {

  private implicit val executionContext: ExecutionContext = system.dispatchers.lookup("mysql-dispatcher")

  override def check(): Future[Unit] = Future {
    try {
      val con = DriverManager.getConnection(url, user, password)
      try {
        val stmt = con.createStatement

        val sql = "UPDATE moviecopy set reservationDate=null,idUser=null,status=0 WHERE reservationDate<date_add(NOW(),INTERVAL -5 DAY)"
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR)
        val currentMinute = now.get(Calendar.MINUTE)
        val currentSecond = now.get(Calendar.SECOND)
        println("Check Reserve Date "+currentHour+":"+currentMinute+":"+currentSecond)

        if (stmt.executeUpdate(sql) == 1)
          println("Azzerata Reserve Date")
      }
      finally if (con != null) con.close()
    }
    Unit
  }

}