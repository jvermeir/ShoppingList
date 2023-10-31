package nl.vermeir.shopapi

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

class Datum(
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @JsonDeserialize(using = DateConversions.Deserializer::class)
  val date: LocalDate
) {
  constructor(kotlinDate: kotlinx.datetime.LocalDate) : this(LocalDate.parse(kotlinDate.toString()))
  constructor(dateAsString: String) : this(LocalDate.parse(dateAsString))
}

object DateConversions {
  object Serializer : JsonSerializer<LocalDate>() {
    override fun serialize(value: LocalDate, gen: JsonGenerator, serializers: SerializerProvider) {
      with(gen) {
        writeString(value.toString())
      }
    }
  }

  object Deserializer : JsonDeserializer<LocalDate>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDate {
      val node = p.readValueAsTree<JsonNode>()
      return LocalDate.parse(node.textValue())
    }
  }
}
