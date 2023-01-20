package com.example.plugins

import com.example.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.categoryRouting() {
    val categoryRepository = CategoryRepository()

    route("/category") {
        get {
            call.respond(categoryRepository.listCategories())
        }
        get("{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            val category = categoryRepository.findCategoryById(id)
            call.respond(category?: "Category $id not found")
        }
        post {
            val category = call.receive<Category>()
            categoryRepository.putItemInTable(category)

            call.respondText("Category stored correctly", status = HttpStatusCode.Created)
        }
        delete("{id?}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
//            if (categoryStorage.removeIf { it.id == id }) {
//                call.respondText("Category removed correctly", status = HttpStatusCode.Accepted)
//            } else {
//                call.respondText("Not Found", status = HttpStatusCode.NotFound)
//            }
        }
    }
}