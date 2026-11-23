<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ApiError } from '@/api/http'
import { login } from '@/auth/authService'
import { senhaProvisoria } from '@/auth/sessao'

const router = useRouter()

const usuario = ref('')
const senha = ref('')
const carregando = ref(false)
const erro = ref<string | null>(null)

const podeEnviar = computed(
  () => usuario.value.trim() !== '' && senha.value !== '' && !carregando.value,
)

async function entrar() {
  if (!podeEnviar.value) return

  carregando.value = true
  erro.value = null
  try {
    await login(usuario.value.trim(), senha.value)
  } catch (e) {
    senha.value = ''
    erro.value = mensagemDeErro(e)
    carregando.value = false
    return
  }

  senha.value = ''
  carregando.value = false
  await router.push(senhaProvisoria() ? { name: 'trocar-senha' } : { name: 'inicio' })
}

function mensagemDeErro(e: unknown): string {
  if (e instanceof ApiError) {
    if (e.status === 401) return 'Login ou senha inválidos.'
    if (e.status >= 500) {
      return e.traceId
        ? `Erro no servidor. Código para suporte: ${e.traceId}`
        : 'Erro no servidor. Tente novamente.'
    }
    return 'Não foi possível entrar. Verifique os dados informados.'
  }
  return 'Não foi possível conectar ao servidor.'
}
</script>

<template>
  <main class="login">
    <form class="login__card" novalidate @submit.prevent="entrar">
      <h1>Entrar</h1>

      <label>
        Login
        <input
          v-model="usuario"
          type="text"
          autocomplete="username"
          maxlength="30"
          required
          :disabled="carregando"
        />
      </label>

      <label>
        Senha
        <input
          v-model="senha"
          type="password"
          autocomplete="current-password"
          required
          :disabled="carregando"
        />
      </label>

      <p v-if="erro" class="login__erro" role="alert">{{ erro }}</p>

      <button type="submit" :disabled="!podeEnviar">
        {{ carregando ? 'Entrando...' : 'Entrar' }}
      </button>
    </form>
  </main>
</template>

<style scoped>
.login {
  min-height: 100vh;
  display: grid;
  place-items: center;
}
.login__card {
  width: min(360px, 90vw);
  display: grid;
  gap: 1rem;
}
label {
  display: grid;
  gap: 0.25rem;
}
input {
  padding: 0.5rem;
}
.login__erro {
  color: #b00020;
  margin: 0;
}
</style>
