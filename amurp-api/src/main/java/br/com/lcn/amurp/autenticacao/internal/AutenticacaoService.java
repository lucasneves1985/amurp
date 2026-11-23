package br.com.lcn.amurp.autenticacao.internal;

import br.com.lcn.amurp.autenticacao.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class AutenticacaoService {

    // BCrypt considera só os primeiros 72 bytes. Acima disso a senha é truncada em silêncio.
    private static final int BCRYPT_MAX_BYTES = 72;

    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    // Hash de referência: equaliza o tempo quando o login não existe ou está inativo.
    private final String hashReferencia;

    AutenticacaoService(UsuarioRepository usuarios,
                        PasswordEncoder passwordEncoder,
                        TokenService tokenService) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.hashReferencia = passwordEncoder.encode("referencia-sem-uso");
    }

    @Transactional(readOnly = true)
    public Optional<TokenResponse> autenticar(String login, String senha) {
        Optional<Usuario> encontrado = usuarios.findByDsLogin(login);

        if (encontrado.isEmpty()
                || !encontrado.get().isAtivo()
                || encontrado.get().getDsPassword() == null) {
            passwordEncoder.matches(senha, hashReferencia);
            return Optional.empty();
        }

        Usuario usuario = encontrado.get();

        if (!passwordEncoder.matches(senha, usuario.getDsPassword())) {
            return Optional.empty();
        }

        return Optional.of(tokenService.gerar(paraAutenticado(usuario)));
    }

    @Transactional
    public TokenResponse trocarSenha(Integer idUsuario, String senhaAtual, String novaSenha) {
        Usuario usuario = usuarios.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!usuario.isAtivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (usuario.getDsPassword() == null
                || !passwordEncoder.matches(senhaAtual, usuario.getDsPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta.");
        }

        if (novaSenha.equals(senhaAtual)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A nova senha deve ser diferente da atual.");
        }

        if (novaSenha.getBytes(StandardCharsets.UTF_8).length > BCRYPT_MAX_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A nova senha excede o limite de 72 bytes.");
        }

        usuario.trocarSenha(passwordEncoder.encode(novaSenha));

        // Token novo, sem SENHA_PROVISORIA. O token anterior mantém a authority até expirar.
        return tokenService.gerar(paraAutenticado(usuario));
    }

    private UsuarioAutenticado paraAutenticado(Usuario usuario) {
        return new UsuarioAutenticado(
                usuario.getIdUsuario(),
                usuario.getDsLogin(),
                usuario.getDsPassword(),
                usuario.isAtivo(),
                usuario.isSenhaProvisoria());
    }
}