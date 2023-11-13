package nl.vermeir.shopapi

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.springframework.data.annotation.Id
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

//@JsonSerialize(using = DateConversions.Serializer::class)
//@JsonDeserialize(using = DateConversions.Deserializer::class)
@Serializable
data class Menu(
  @Serializable(with = UUIDSerializer::class)
  @Id val id: UUID? = null,
  @Serializable(with = LocalDateSerializer::class)
  val firstDay: LocalDate
)

//@JsonSerialize(using = DateConversions.Serializer::class)
//@JsonDeserialize(using = DateConversions.Deserializer::class)
@Serializable
data class MenuItem(
  @Serializable(with = UUIDSerializer::class)
  @Id val id: UUID? = null,
  @Serializable(with = UUIDSerializer::class)
  val menuId: UUID,
  @Serializable(with = UUIDSerializer::class)
  val recipeId: UUID,
  @Serializable(with = LocalDateSerializer::class)
  val theDay: LocalDate
)

@Serializable
data class Ingredient(
  @Serializable(with = UUIDSerializer::class)
  @Id val id: UUID? = null,
  var name: String,
  @Serializable(with = UUIDSerializer::class)
  var categoryId: UUID
)

@Serializable
data class Category(
  @Serializable(with = UUIDSerializer::class)
  @Id val id: UUID? = null,
  var name: String,
  var shopOrder: Int
)

@Serializable
data class RecipeIngredient(
  @Serializable(with = UUIDSerializer::class)
  @Id val id: UUID? = null,
  @Serializable(with = UUIDSerializer::class)
  val recipeId: UUID,
  @Serializable(with = UUIDSerializer::class)
  val ingredientId: UUID
)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDate::class)
class LocalDateSerializer : KSerializer<LocalDate> {
  private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

  override fun serialize(encoder: Encoder, value: LocalDate) {
    encoder.encodeString(value.format(formatter))
  }

  override fun deserialize(decoder: Decoder): LocalDate {
    return LocalDate.parse(decoder.decodeString(), formatter)
  }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = UUID::class)
class UUIDSerializer : KSerializer<UUID> {
  override fun serialize(encoder: Encoder, value: UUID) {
    encoder.encodeString(value.toString())
  }

  override fun deserialize(decoder: Decoder): UUID {
    return UUID.fromString(decoder.decodeString())
  }
}
