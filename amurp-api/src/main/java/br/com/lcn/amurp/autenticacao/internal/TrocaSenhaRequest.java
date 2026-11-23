package br.com.lcn.amurp.autenticacao.internal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record TrocarSenhaRequest(
        @NotBlank String senhaAtual,
        @NotBlank @Size(min = 8, max = 72) String novaSenha) {
}