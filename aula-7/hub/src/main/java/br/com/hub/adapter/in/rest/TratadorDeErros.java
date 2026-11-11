package br.com.hub.adapter.in.rest;

import br.com.hub.domain.exception.FaturaNaoEncontradaException;
import br.com.hub.domain.exception.RegraFaturamentoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(RegraFaturamentoException.class)
    public ProblemDetail regraDeNegocio(RegraFaturamentoException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(FaturaNaoEncontradaException.class)
    public ProblemDetail naoEncontrada(FaturaNaoEncontradaException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
