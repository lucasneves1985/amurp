package br.com.lcn.amurp.autenticacao.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByDsLogin(String dsLogin);
}