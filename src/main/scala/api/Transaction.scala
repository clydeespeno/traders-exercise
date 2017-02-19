package api

import java.time.Instant

import play.api.libs.json.Json

case class Transaction(
  timestamp: Long,
  traderId: String,
  value: BigDecimal
) {
  def instant: Instant = Instant.ofEpochSecond(timestamp)
}

object Transaction {
  implicit val reads = Json.reads[Transaction]
}
