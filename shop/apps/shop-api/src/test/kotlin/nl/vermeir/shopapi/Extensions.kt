import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*

fun LocalDateTime.format(): String = this.format(dateFormatter)

private val dateFormatter = DateTimeFormatterBuilder()
  .appendPattern("yyyy-MM-dd")
  .toFormatter(Locale.ENGLISH)

class GsonLocalDateAdapter : JsonSerializer<LocalDate?>, JsonDeserializer<LocalDate?> {
  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDate? {
    return LocalDate.parse(json?.getAsString(), formatter)
  }

  override fun serialize(src: LocalDate?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
    return JsonPrimitive(src?.format(formatter))
  }
}
