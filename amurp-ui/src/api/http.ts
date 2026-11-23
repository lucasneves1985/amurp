import { encerrarSessao, obterToken } from '@/auth/sessao'

export class ApiError extends Error {
  constructor(
    public readonly status: number,
    public readonly detail?: string,
    public readonly traceId?: string,
  ) {
    super(detail ?? `HTTP ${status}`)
  }
}

export async function requisitar<T>(caminho: string, init: RequestInit = {}): Promise<T> {
  const token = obterToken()

  const headers = new Headers(init.headers)
  if (!headers.has('Content-Type')) headers.set('Content-Type', 'application/json')
  headers.set('Accept', 'application/problem+json, application/json')
  if (token) headers.set('Authorization', `Bearer ${token}`)

  const resposta = await fetch(`/api${caminho}`, { ...init, headers })

  if (!resposta.ok) {
    // 401 com token presente: token expirado ou revogado. Encerra a sessão local.
    if (resposta.status === 401 && token) {
      encerrarSessao()
      window.location.assign('/login')
    }

    let corpo: { detail?: string; traceId?: string } = {}
    try {
      corpo = await resposta.json()
    } catch {
      // resposta sem corpo: segue apenas com o status
    }
    throw new ApiError(resposta.status, corpo.detail, corpo.traceId)
  }

  if (resposta.status === 204) return undefined as T
  return (await resposta.json()) as T
}
