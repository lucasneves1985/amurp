package br.com.lcn.amurp.autenticacao.internal;

import br.com.lcn.amurp.autenticacao.LoginRequest;
import br.com.lcn.amurp.autenticacao.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return autenticacaoService.autenticar(request.login(), request.senha());
    }

    @PostMapping("/trocar-senha")
    public TokenResponse trocarSenha(@AuthenticationPrincipal Jwt jwt,
                                     @Valid @RequestBody TrocarSenhaRequest request) {
        return autenticacaoService.trocarSenha(
                Integer.valueOf(jwt.getSubject()),
                request.senhaAtual(),
                request.novaSenha());
    }

    // ATENÇÃO: não invalida o token. Ver seção "Logout" abaixo.
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}