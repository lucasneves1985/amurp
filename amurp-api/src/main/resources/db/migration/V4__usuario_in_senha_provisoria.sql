-- V4__usuario_in_senha_provisoria.sql
ALTER TABLE usuario ADD COLUMN in_senha_provisoria INT(11) NOT NULL DEFAULT 0;