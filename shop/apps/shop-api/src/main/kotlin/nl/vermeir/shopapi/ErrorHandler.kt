package nl.vermeir.shopapi

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import java.util.*

// inspired by https://www.javaguides.net/2021/10/spring-boot-exception-handling-example.html

data class ErrorDetails(val timestamp: Date, val message: String, val details: String)

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class ResourceNotFoundException(message: String) : RuntimeException(message)

@ControllerAdvice
class GlobalExceptionHandler {
  var logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

  @ExceptionHandler(ResourceNotFoundException::class)
  fun handleResourceNotFoundException(
    exception: ResourceNotFoundException,
    webRequest: WebRequest
  ): ResponseEntity<ErrorDetails> {
    val errorDetails = exception.message?.let {
      ErrorDetails(
        Date(), it,
        webRequest.getDescription(false)
      )
    }
    logger.info(errorDetails.toString())
    return ResponseEntity(errorDetails, HttpStatus.NOT_FOUND)
  }

  @ExceptionHandler(NoSuchElementException::class)
  fun handleNoSuchElementException(
    exception: NoSuchElementException,
    webRequest: WebRequest
  ): ResponseEntity<ErrorDetails> {
    val errorDetails = exception.message?.let {
      ErrorDetails(
        Date(), it,
        webRequest.getDescription(false)
      )
    }
    logger.info(errorDetails.toString())
    return ResponseEntity(errorDetails, HttpStatus.NOT_FOUND)
  }

//  @ExceptionHandler(DbActionExecutionException::class)
//  fun handleDbActionException(
//    exception: DbActionExecutionException,
//    webRequest: WebRequest
//  ): ResponseEntity<ErrorDetails> {
//    val errorDetails = exception.message?.let {
//      ErrorDetails(
//        Date(), it,
//        webRequest.getDescription(false)
//      )
//    }
//    logger.info(errorDetails.toString())
//    return ResponseEntity(errorDetails, HttpStatus.CONFLICT)
//  }

  @ExceptionHandler(java.lang.Exception::class)
  fun handleGlobalException(
    exception: java.lang.Exception,
    webRequest: WebRequest
  ): ResponseEntity<ErrorDetails> {
    val errorDetails = ErrorDetails(
      Date(), exception.message!!,
      webRequest.getDescription(false)
    )
    logger.info(errorDetails.toString())
    return ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
  }
}
