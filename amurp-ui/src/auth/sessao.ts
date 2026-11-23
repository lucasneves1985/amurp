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

function lerPayload(jwt: string): Record<string, unknown> {
  try {
    const payloadB64 = jwt.split('.')[1]
    if (!payloadB64) return {}

    const base64 = payloadB64.replace(/-/g, '+').replace(/_/g, '/')
    const pad = (4 - (base64.length % 4)) % 4
    const payload: unknown = JSON.parse(atob(base64.padEnd(base64.length + pad, '=')))
    return typeof payload === 'object' && payload !== null
      ? (payload as Record<string, unknown>)
      : {}
  } catch {
    return {}
  }
}

export function tokenValido(): boolean {
  if (!tokenAtual) return false
  const exp = lerPayload(tokenAtual).exp
  // Margem de 5 segundos para não entrar com token prestes a expirar.
  return typeof exp === 'number' && exp * 1000 > Date.now() + 5000
}

function lerAuthorities(jwt: string): string[] {
  const authorities = lerPayload(jwt).authorities
  return Array.isArray(authorities) ? authorities.filter((a): a is string => typeof a === 'string') : []
}
