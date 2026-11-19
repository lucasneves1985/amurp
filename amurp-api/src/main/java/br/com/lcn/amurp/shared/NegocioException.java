package br.com.lcn.amurp.shared;

import org.springframework.http.HttpStatus;

/**
 * Base de exceções de negócio. A mensagem é enviada ao cliente:
 * nunca passe detalhe técnico, SQL ou dado sensível.
 */
public abstract class NegocioException extends RuntimeException {

    private final HttpStatus status;

    protected NegocioException(HttpStatus status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}