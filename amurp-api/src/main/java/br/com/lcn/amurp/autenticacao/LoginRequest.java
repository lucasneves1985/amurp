package br.com.lcn.amurp.autenticacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Size(max = 30) String login,
        @NotBlank @Size(max = 72) String senha // 72: limite de entrada do BCrypt
) {}