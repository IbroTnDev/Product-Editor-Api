package models

import javax.inject.Inject
import play.api.libs.json.{Json, Format}

case class Description(
    key: String,
    value: String,
    description_id: Option[Int],
    id: Int

)

object Description {
  implicit val format: Format[Description] = Json.format[Description]

  def fromDTO(dto: Description): Description =
    new Description(
      dto.key,
      dto.value,
      dto.description_id,
      dto.id
    )
}