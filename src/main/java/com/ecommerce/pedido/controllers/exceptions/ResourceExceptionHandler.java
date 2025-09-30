package com.ecommerce.pedido.controllers.exceptions;

import com.ecommerce.pedido.dtos.excpetions.ValidationError;
import com.ecommerce.pedido.services.exceptions.EmailCadastradoExcption;
import com.ecommerce.pedido.services.exceptions.EntidadeNaoEncontradaException;
import com.ecommerce.pedido.services.exceptions.ValidacaoNegocioException;
import com.ecommerce.pedido.services.exceptions.dtos.StandardError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice(basePackages = "com.ecommerce.pedido")
public class ResourceExceptionHandler {

    // Este metodo irá capturar qualquer exceção do tipo EntidadeNaoEncontradaException
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<StandardError> entidadeNaoEncontrada(EntidadeNaoEncontradaException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND; // 404
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Recurso não encontrado",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    // Este metodo irá capturar qualquer exceção do tipo ValidacaoNegocioException
    @ExceptionHandler(ValidacaoNegocioException.class)
    public ResponseEntity<StandardError> validacaoNegocio(ValidacaoNegocioException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Erro na validação",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    /**
     * Captura os erros de validação dos DTOs (@Valid).
     * Retorna HTTP 422 Unprocessable Entity (ou 400 Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> methodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY; // 422
        ValidationError err = new ValidationError(
                Instant.now(),
                status.value(),
                "Erro de validação",
                "Um ou mais campos estão inválidos.",
                request.getRequestURI()
        );

        // Pega cada erro de campo da exceção e adiciona à nossa lista de erros personalizada
        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            err.addError(f.getField(), f.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(err);
    }
}