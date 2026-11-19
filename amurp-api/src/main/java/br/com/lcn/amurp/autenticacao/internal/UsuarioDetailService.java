package br.com.lcn.amurp.autenticacao.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
class UsuarioDetailsService implements UserDetailsService {

    // Valor confirmado: usuario.in_status = 1 é ativo. Outros valores tratados como inativo.
    private static final int IN_STATUS_ATIVO = 1;

    private final UsuarioRepository usuarios;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) {
        Usuario u = usuarios.findByDsLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("não encontrado"));

        // Sem ds_password não há como autenticar: trata como inativo.
        boolean ativo = Objects.equals(u.getInStatus(), IN_STATUS_ATIVO)
                && u.getDsPassword() != null;

        return new UsuarioAutenticado(
                u.getIdUsuario(),
                u.getDsLogin(),
                u.getDsPassword(),
                ativo,
                Objects.equals(u.getInSenhaProvisoria(), 1));
    }
}