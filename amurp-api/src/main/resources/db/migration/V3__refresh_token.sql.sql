-- V4__refresh_token.sql (PROPOSTA, depende de aprovação)
CREATE TABLE refresh_token (
  id_refresh_token BIGINT NOT NULL AUTO_INCREMENT,
  id_usuario INT(11) NOT NULL,
  token_hash CHAR(64) NOT NULL,
  dt_criacao DATETIME NOT NULL,
  dt_expiracao DATETIME NOT NULL,
  dt_revogacao DATETIME NULL,
  PRIMARY KEY (id_refresh_token),
  UNIQUE KEY uk_refresh_token_hash (token_hash),
  KEY idx_refresh_usuario (id_usuario),
  CONSTRAINT fk_refresh_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;