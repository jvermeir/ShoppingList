package nl.vermeir.shopapi

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import nl.vermeir.shopapi.DateConversions
import org.springframework.data.annotation.Id

@JsonSerialize(using = DateConversions.Serializer::class)
@JsonDeserialize(using = DateConversions.Deserializer::class)
@Serializable
data class Menu(
  @Id val id: String?,
  val firstDay: LocalDate
)

@JsonSerialize(using = DateConversions.Serializer::class)
@JsonDeserialize(using = DateConversions.Deserializer::class)
@Serializable
data class MenuItem(
  @Id val id: String? = null, val menuId: String, val recipeId: String,
  val theDay: LocalDate
)
