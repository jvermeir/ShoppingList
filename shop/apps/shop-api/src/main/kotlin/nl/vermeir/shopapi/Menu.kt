package nl.vermeir.shopapi

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*


@RestController
class MenuResource(val menuService: MenuService) {
  @GetMapping("/menus")
  fun list(): List<Menu> = menuService.find()

  @GetMapping("/menu/{id}")
  fun findById(@PathVariable(name = "id") id: String) = ResponseEntity.ok(menuService.findById(id))

  @GetMapping("/menu")
  fun findByFirstDay(@RequestParam(name = "firstDay") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) firstDay: LocalDate) =
    ResponseEntity.ok(menuService.findByFirstDay(firstDay))

  @PostMapping("/menu")
  fun post(@RequestBody menu: Menu):ResponseEntity<Menu>  = ResponseEntity(menuService.save(menu), HttpStatus.CREATED)
}

@Service
class MenuService(val db: MenuRepository) {
  fun find(): List<Menu> = db.findAll().toList()

  fun findById(id: String): Menu = db.findById(id).orElseThrow { ResourceNotFoundException("Menu '${id}' not found") }

  fun findByFirstDay(firstDay: LocalDate): Menu =
    db.findByFirstDay(firstDay).orElseThrow { ResourceNotFoundException("Menu for '${firstDay}' not found") }

  fun save(menu: Menu): Menu = db.save(menu)

  fun deleteAll() = db.deleteAll()
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

@Table("MENUS")
data class Menu(
  @Id val id: String?,
  @JsonSerialize(using = DateConversions.Serializer::class)
  @JsonDeserialize(using = DateConversions.Deserializer::class)
  val firstDay: LocalDate
)

interface MenuRepository : CrudRepository<Menu, String> {
  @Query("SELECT * FROM menus WHERE first_day  = :first_day")
  fun findByFirstDay(first_day: LocalDate): Optional<Menu>
}
