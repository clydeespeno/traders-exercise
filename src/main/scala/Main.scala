import java.time.Instant

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.ws.WSClient
import play.api.libs.ws.ahc.AhcWSClient
import api.API
import api.Transaction

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration._

object Main extends App with ConfiguredAPI with Challenges {
  override implicit val ctx: ExecutionContext = ExecutionContext.global

  executeChallenges().map(_ => {
    println("Exit!")
    sys.exit()
    System.exit(0)
  })
}

trait Challenges { this: API =>

  def allTraders: Future[Unit] =
    traders.map(ts => challenge("Find all traders from Singapore and sort them by name."){
      ts.filter(_.city == "Singapore").sortWith(_.name < _.name).foreach(t => {
        println(t.name)
      })
    })

  def highestTransaction: Future[Unit] =
    transactions.map(ts => challenge("Find the transaction with the highest value."){
      ts.foldLeft(None.asInstanceOf[Option[Transaction]]) { (highest, t) => highest match {
        case Some(h) if h.value > t.value => Some(h)
        case _ => Some(t)
      }}.foreach(h => {
        println(s"Transaction: ${h.traderId} at ${h.instant}")
        println(s"Value: ${h.value}")
      })
    })

  def allTransactionsSorted: Future[Unit] =
    transactions.map(ts => challenge("Find all transactions in the year 2016 and sort them by value (high to small).") {
      val year2016 = Instant.ofEpochMilli(1451606400000L - 1)
      ts.filter(_.instant.isAfter(year2016)).sortWith(_.value > _.value).foreach(t => {
        println(s"${t.value} [${t.traderId}] at [${t.instant}]")
      })
    })

  def averageTransactions: Future[Unit] =
    traders.flatMap(traders => transactions.map(trans => (traders, trans))).map { case (trds, trans) =>
      challenge("Find the average of transactions' values from the traders living in Beijing.") {
        val transValues = trds.filter(_.city == "Beijing")
          .flatMap(t => trans.filter(_.traderId == t.id)).map(_.value)
        if (transValues.nonEmpty) {
          println(s"ave: ${transValues.sum / transValues.length}")
        } else {
          println(s"ave: 0")
        }
      }
    }

  def executeChallenges(): Future[Unit] =
    allTraders
      .flatMap(_ => highestTransaction)
      .flatMap(_ => allTransactionsSorted)
      .flatMap(_ => averageTransactions)

  private def challenge(c: String)(solution: => Unit): Unit = {
    println("******************************************************************")
    println(s"Challenge: $c")
    println("------------------------------------------------------------------")
    solution
    println("******************************************************************")
    println()
  }
}

trait ConfiguredAPI extends API {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  override val ws: WSClient = AhcWSClient()

  override val baseUrl: String = "https://fvjkpkflnc.execute-api.us-east-1.amazonaws.com"
  override val headers: Seq[(String, String)] = Seq("x-api-key" -> "gaqcRZE4bd58gSAJH3XsLYBo1EvwIQo88IfYL1L5")

  sys.addShutdownHook(() => {
    Await.result(system.terminate(), Duration.Inf)
  })
}
