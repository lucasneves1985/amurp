package br.com.lcn.amurp.shared.internal;

import br.com.lcn.amurp.shared.NegocioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NegocioException.class)
    ResponseEntity<ProblemDetail> negocio(NegocioException ex) {
        return resposta(ex.getStatus(), ex.getMessage());
    }

    // Senha errada, usuário inexistente e usuário inativo produzem a mesma resposta.
    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ProblemDetail> autenticacao(AuthenticationException ex) {
        log.debug("Falha de autenticação: {}", ex.getClass().getSimpleName());
        return resposta(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ProblemDetail> acessoNegado(AccessDeniedException ex) {
        log.debug("Acesso negado: {}", ex.getMessage());
        return resposta(HttpStatus.FORBIDDEN, "Acesso negado.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ProblemDetail> integridade(DataIntegrityViolationException ex) {
        // O detalhe do SQL fica só no log.
        log.warn("Violação de integridade", ex);
        return resposta(HttpStatus.CONFLICT, "A operação viola uma regra de integridade dos dados.");
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ProblemDetail> inesperado(Exception ex) {
        String traceId = UUID.randomUUID().toString();
        log.error("Erro não tratado. traceId={}", traceId, ex);

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno. Informe o traceId ao suporte.");
        pd.setProperty("traceId", traceId);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pd);
    }

    // Sobrescreve a versão padrão para listar os campos inválidos.
    // Não devolve o valor rejeitado: ele pode ser senha.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<Map<String, String>> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of(
                        "campo", e.getField(),
                        "mensagem", e.getDefaultMessage() == null ? "inválido" : e.getDefaultMessage()))
                .toList();

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Dados inválidos.");
        pd.setProperty("erros", erros);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    private static ResponseEntity<ProblemDetail> resposta(HttpStatus status, String detalhe) {
        return ResponseEntity.status(status)
                .body(ProblemDetail.forStatusAndDetail(status, detalhe));
    }
}