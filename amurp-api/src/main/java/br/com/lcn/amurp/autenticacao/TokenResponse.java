package br.com.lcn.amurp.autenticacao;

public record TokenResponse(String token, String tipo, long expiresInSeconds) {}