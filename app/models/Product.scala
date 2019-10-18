package models

import javax.inject.Inject
import play.api.libs.json.{Json, Format}

case class Product(
    id: Int,
    name: String,
    category: String,
    code: Int,
    price: Double
)

object Product {
  implicit val format: Format[Product] = Json.format[Product]

  def fromDTO(dto: ProductDTO): Product =
    new Product(
      dto.id,
      dto.name,
      dto.category,
      dto.code,
      dto.price
    )
}

case class ProductDTO(
    id: Int,
    name: String,
    category: String,
    code: Int,
    price: Double,
)

object ProductDTO {
  implicit val format: Format[ProductDTO] = Json.format[ProductDTO]
}