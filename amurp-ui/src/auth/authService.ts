import { requisitar } from '@/api/http'
import { guardarToken, type RespostaToken } from './sessao'

export async function login(login: string, senha: string): Promise<void> {
  const resposta = await requisitar<RespostaToken>('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ login, senha }),
  })
  guardarToken(resposta)
}
