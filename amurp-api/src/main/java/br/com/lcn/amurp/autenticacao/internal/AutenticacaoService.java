package br.com.lcn.amurp.autenticacao.internal;

import br.com.lcn.amurp.autenticacao.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public TokenResponse autenticar(String login, String senha) {
        Authentication auth = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(login, senha));
        return tokenService.gerar((UsuarioAutenticado) auth.getPrincipal());
    }
}