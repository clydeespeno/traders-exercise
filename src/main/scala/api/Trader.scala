package api

import play.api.libs.json.Json

case class Trader(
  name: String,
  city: String,
  id: String
)

object Trader {
  implicit val reads = Json.reads[Trader]
}
