package nl.vermeir.shopapi

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@RestController
class MessageResource(val service: MessageService) {
  @GetMapping("/messages")
  fun index(): List<Message> = service.findMessages()

  @PostMapping("/message")
  fun post(@RequestBody message: Message) = service.post(message)


}

@Service
class MessageService(val db: MessageRepository) {
  fun deleteAll() = db.deleteAll()

  fun findMessages(): List<Message> = db.findMessages()

  fun post(message: Message):Message = db.save(message)
}

@Table("MESSAGES")
data class Message(@Id val id: String?, val text: String, val first_day: LocalDate)

interface MessageRepository : CrudRepository<Message, String> {
  @Query("select * from messages")
  fun findMessages(): List<Message>
}
