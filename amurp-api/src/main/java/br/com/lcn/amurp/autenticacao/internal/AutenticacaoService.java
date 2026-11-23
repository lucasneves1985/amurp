package br.com.lcn.amurp.autenticacao.internal;

import br.com.lcn.amurp.autenticacao.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    // BCrypt considera só os primeiros 72 bytes da senha. Acima disso, a senha é truncada em silêncio.
    private static final int BCRYPT_MAX_BYTES = 72;

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;

    public TokenResponse autenticar(String login, String senha) {
        Authentication auth = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(login, senha));
        return tokenService.gerar((UsuarioAutenticado) auth.getPrincipal());
    }

    @Transactional
    public TokenResponse trocarSenha(Integer idUsuario, String senhaAtual, String novaSenha) {
        Usuario usuario = usuarios.findById(idUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("usuário não encontrado"));

        // Token emitido antes da inativação ainda é válido até expirar. Não permitir troca.
        if (!usuario.isAtivo()) {
            throw new DisabledException("usuário inativo");
        }

        if (usuario.getDsPassword() == null
                || !passwordEncoder.matches(senhaAtual, usuario.getDsPassword())) {
            throw new SenhaException("Senha atual incorreta.");
        }

        if (novaSenha.equals(senhaAtual)) {
            throw new SenhaException("A nova senha deve ser diferente da atual.");
        }

        if (novaSenha.getBytes(StandardCharsets.UTF_8).length > BCRYPT_MAX_BYTES) {
            throw new SenhaException("A nova senha excede o limite de 72 bytes.");
        }

        usuario.trocarSenha(passwordEncoder.encode(novaSenha));

        // Token novo sem SENHA_PROVISORIA. O anterior mantém essa authority até expirar.
        return tokenService.gerar(new UsuarioAutenticado(
                usuario.getIdUsuario(),
                usuario.getDsLogin(),
                usuario.getDsPassword(),
                true,
                false));
    }
}