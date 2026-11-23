package br.com.lcn.amurp.autenticacao.internal;

import br.com.lcn.amurp.shared.NegocioException;
import org.springframework.http.HttpStatus;

class SenhaException extends NegocioException {

    SenhaException(String mensagem) {
        super(HttpStatus.BAD_REQUEST, mensagem);
    }
}