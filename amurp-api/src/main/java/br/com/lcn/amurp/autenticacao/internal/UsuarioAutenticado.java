package br.com.lcn.amurp.autenticacao.internal;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

record UsuarioAutenticado(
        Integer idUsuario,
        String login,
        String senhaHash,
        boolean ativo,
        boolean senhaProvisoria) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return senhaProvisoria
                ? List.of(new SimpleGrantedAuthority("SENHA_PROVISORIA"))
                : List.of(new SimpleGrantedAuthority("USUARIO"));
    }

    @Override
    public String getPassword() {
        return senhaHash;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }
}