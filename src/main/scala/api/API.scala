package api

import play.api.libs.ws.WSClient
import play.api.libs.ws.WSResponse

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

trait API {

  implicit def ctx: ExecutionContext
  def ws: WSClient

  def baseUrl: String
  def headers: Seq[(String, String)]

  private def get(path: String): Future[WSResponse] =
    ws.url(s"$baseUrl/$path").withHeaders(headers: _*).get()

  lazy val traders: Future[Seq[Trader]] = get("/prod/traders").map(_.json.as[Seq[Trader]])

  lazy val transactions: Future[Seq[Transaction]] = get("/prod/transactions").map(_.json.as[Seq[Transaction]])
}
