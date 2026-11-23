package br.com.lcn.amurp.autenticacao.internal;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

// ds_senha é intencionalmente NÃO mapeada. Será removida do banco após a migração.
@Entity
@Table(name = "usuario")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Usuario {

    // Confirmado: in_status = 1 é ativo.
    private static final int IN_STATUS_ATIVO = 1;

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario; // sem @GeneratedValue: a coluna não é AUTO_INCREMENT

    @Column(name = "ds_login", nullable = false, length = 30)
    private String dsLogin;

    @Column(name = "ds_usuario", nullable = false, length = 100)
    private String dsUsuario;

    @Column(name = "in_status", nullable = false)
    private Integer inStatus;

    @Column(name = "ds_password", length = 255)
    private String dsPassword;

    @Column(name = "in_senha_provisoria", nullable = false)
    private Integer inSenhaProvisoria;

    public boolean isAtivo() {
        return Objects.equals(inStatus, IN_STATUS_ATIVO);
    }

    public boolean isSenhaProvisoria() {
        return Objects.equals(inSenhaProvisoria, 1);
    }

    public void trocarSenha(String hash) {
        this.dsPassword = hash;
        this.inSenhaProvisoria = 0;
    }
}