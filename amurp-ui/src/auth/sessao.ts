export interface RespostaToken {
  token: string
  tipo: string
  expiresInSeconds: number
}

// Token mantido só em memória: não vai para localStorage/sessionStorage.
// Ao recarregar a página, a sessão se perde até existir refresh token.
let tokenAtual: string | null = null
let authorities: string[] = []

export function guardarToken(resposta: RespostaToken): void {
  tokenAtual = resposta.token
  authorities = lerAuthorities(resposta.token)
}

export function obterToken(): string | null {
  return tokenAtual
}

export function senhaProvisoria(): boolean {
  return authorities.includes('SENHA_PROVISORIA')
}

export function encerrarSessao(): void {
  tokenAtual = null
  authorities = []
}

// Decodifica o payload só para decidir a rota na interface.
// Não verifica assinatura: quem decide acesso é sempre o backend.
function lerAuthorities(jwt: string): string[] {
  try {
    const partes = jwt.split('.')
    const payloadB64 = partes[1]
    if (!payloadB64) return []

    const base64 = payloadB64.replace(/-/g, '+').replace(/_/g, '/')
    const pad = (4 - (base64.length % 4)) % 4
    const payload = JSON.parse(atob(base64.padEnd(base64.length + pad, '=')))
    return Array.isArray(payload.authorities) ? payload.authorities : []
  } catch {
    return []
  }
}
