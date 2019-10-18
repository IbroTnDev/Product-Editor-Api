package models

import javax.inject.Inject
import play.api.libs.json.{Json, Format}
import models.{Product, Description}

case class ProductsInfo(
    product: Product,
    details: Seq[Description]
)

object ProductsInfo {
  implicit val format: Format[ProductsInfo] = Json.format[ProductsInfo]
}