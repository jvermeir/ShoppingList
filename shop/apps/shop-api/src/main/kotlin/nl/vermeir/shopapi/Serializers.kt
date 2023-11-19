package nl.vermeir.shopapi

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

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
