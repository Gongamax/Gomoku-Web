package pt.isel.daw.gomoku.http.util

import org.slf4j.LoggerFactory
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import pt.isel.daw.gomoku.http.media.Problem
import java.net.URI

@ControllerAdvice
class CustomExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        log.info("Handling MethodArgumentNotValidException: {}", ex.message)
        val errors = ex.bindingResult.fieldErrors.map { it.defaultMessage }.joinToString(", ")
        return Problem(
            typeUri = Problem.invalidRequestContent,
            title = "Problem.invalidRequestContent",
            status = 400,
            detail = errors ?: "Invalid request content",
        ).toResponse()
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        log.info("Handling HttpMessageNotReadableException: {}", ex.httpInputMessage)
        return Problem(
            typeUri = Problem.invalidRequestContent,
            title = "Problem.invalidRequestContent",
            status = 400,
            detail = "Invalid request content",
        ).toResponse()
    }

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        log.info("Handling ExceptionInternal: {}", ex.message)
        return Problem(
            typeUri = Problem.internalServerError,
            title = "Problem.internalServerError",
            status = 500,
            detail = "Internal server error",
        ).toResponse()
    }

    override fun handleMissingPathVariable(
        ex: MissingPathVariableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        log.info("Handling MissingPathVariableException: {}", ex.message)
        return Problem(
            typeUri = Problem.invalidRequestContent,
            title = "Problem.invalidRequestContent",
            status = 400,
            detail = "Invalid request content",
        ).toResponse()
    }

    override fun handleTypeMismatch(
        ex: TypeMismatchException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        log.info("Handling TypeMismatchException: {}", ex.message)
        val type = ex.value?.let { it::class.java.simpleName } ?: "null"
        val detail = "The value '${ex.value}' of type '$type' could not be converted to ${ex.requiredType?.name}"
        val uri = URI(
            request.toString()
                .substringAfter("uri=")
                .substringBefore("}")
                .substringBefore(";")
        )
        return Problem(
            typeUri = Problem.invalidRequestContent,
            title = "Invalid Argument",
            status = HttpStatus.UNPROCESSABLE_ENTITY.value(),
            detail = detail,
            instance = uri
        ).toResponse()
    }

    @ExceptionHandler(
        Exception::class,
    )
    fun handleAll(ex: Exception): ResponseEntity<Unit> {
        log.info("Handling Exception: ${ex.message}")
        return ResponseEntity.status(500).build()
    }

    companion object {
        private val log = LoggerFactory.getLogger(CustomExceptionHandler::class.java)
    }
}