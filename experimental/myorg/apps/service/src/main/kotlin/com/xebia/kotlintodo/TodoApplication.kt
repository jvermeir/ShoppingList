package com.xebia.kotlintodo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.relational.core.mapping.Table
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@SpringBootApplication
class SpaceApplication

fun main(args: Array<String>) {
	runApplication<SpaceApplication>(*args)
}


@Table("MESSAGES")
data class Message(@Id val id: String?, val text: String)

@RestController
class MessageResource(val service: MessageService) {
	@GetMapping("/kotlin/todos")
	fun index(): List<Message> = service.findMessages()

	@PostMapping("/kotlin/todo")
	fun post(@RequestBody message: Message):Message {
		return service.post(message)
	}
}
interface MessageRepository : CrudRepository<Message, String>{

	@Query("select * from messages")
	fun findMessages(): List<Message>
}


@Service
class MessageService(val db: MessageRepository) {

	fun findMessages(): List<Message> = db.findMessages()

	fun post(message: Message):Message{
		return db.save(message)
	}
}
