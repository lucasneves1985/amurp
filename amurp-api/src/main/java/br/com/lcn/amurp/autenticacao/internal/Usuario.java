package br.com.lcn.amurp.autenticacao.internal;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// ds_senha NÃO é mapeada. Continua no banco até decisão sobre o Delphi.
@Entity
@Table(name = "usuario")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Usuario {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

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
}