package nl.vermeir.shopapi

import nl.vermeir.shopapi.data.Message
import nl.vermeir.shopapi.data.MessageRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class ShopApiApplication

fun main(args: Array<String>) {
  runApplication<ShopApiApplication>(*args)
}

@RestController
class MessageResource(val messageService: MessageService) {
  @GetMapping("/api/messages")
  fun index(): List<Message> = messageService.findMessages()

  @PostMapping("/api/message")
  fun post(@RequestBody message: Message) {
    messageService.post(message)
  }
}

@Service
class MessageService(val db: MessageRepository) {

  fun findMessages(): List<Message> = db.findMessages()

  fun post(message: Message){
    db.save(message)
  }
}
