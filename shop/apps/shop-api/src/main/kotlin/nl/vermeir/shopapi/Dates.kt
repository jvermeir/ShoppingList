package nl.vermeir.shopapi

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import java.time.LocalDate

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
