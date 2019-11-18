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

// This is the companion object of the class Product
object Product {
  implicit val format: Format[Product]  = Json.format[Product]

  // Factory method : used to create an instance of a particular class
  def fromDTO(dto: Product): Product =
    new Product(
      dto.id,
      dto.name,
      dto.category,
      dto.code,
      dto.price
    ) 
}