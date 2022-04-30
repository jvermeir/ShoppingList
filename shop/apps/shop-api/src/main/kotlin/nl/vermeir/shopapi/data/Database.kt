package nl.vermeir.shopapi.data

import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository

@Table("MESSAGES")
data class Message(@Id val id: String?, val text: String)
interface MessageRepository : CrudRepository<Message, String> {

  @Query("select * from messages")
  fun findMessages(): List<Message>
}

