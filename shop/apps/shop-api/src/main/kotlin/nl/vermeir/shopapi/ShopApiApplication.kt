package nl.vermeir.shopapi

import nl.vermeir.shopapi.data.Category
import nl.vermeir.shopapi.data.CategoryRepository
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
  @GetMapping("/messages")
  fun index(): List<Message> = messageService.findMessages()

  @PostMapping("/message")
  fun post(@RequestBody message: Message) {
    messageService.post(message)
  }
}

@RestController
class CategoryResource(val categoryService: CategoryService) {
  @GetMapping("/categories")
  fun index(): List<Category> = categoryService.findCategories()

  @PostMapping("/category")
  fun post(@RequestBody category: Category) {
    categoryService.post(category)
  }
}

@Service
class CategoryService(val db: CategoryRepository) {

  fun findCategories(): List<Category> = db.findAll().toList()

  fun post(category: Category){
    db.save(category)
  }
}

@Service
class MessageService(val db: MessageRepository) {

  fun findMessages(): List<Message> = db.findAll().toList()

  fun post(message: Message){
    db.save(message)
  }
}
