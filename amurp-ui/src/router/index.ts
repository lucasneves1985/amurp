import { createRouter, createWebHistory } from 'vue-router'
import { encerrarSessao, senhaProvisoria, tokenValido } from '@/auth/sessao'

declare module 'vue-router' {
  interface RouteMeta {
    publica?: boolean
  }
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/inicio' },
    {
      path: '/login',
      name: 'login',
      meta: { publica: true },
      component: () => import('@/views/LoginView.vue'),
    },
    {
      path: '/trocar-senha',
      name: 'trocar-senha',
      component: () => import('@/views/TrocarSenhaView.vue'),
    },
    {
      path: '/inicio',
      name: 'inicio',
      component: () => import('@/views/InicioView.vue'),
    },
    { path: '/:pathMatch(.*)*', redirect: '/inicio' },
  ],
})

// Guarda de interface. A autorização real é do backend.
router.beforeEach((to) => {
  if (to.meta.publica) {
    if (tokenValido() && to.name === 'login') {
      return { name: senhaProvisoria() ? 'trocar-senha' : 'inicio' }
    }
    return true
  }

  if (!tokenValido()) {
    encerrarSessao()
    return { name: 'login' }
  }

  if (senhaProvisoria() && to.name !== 'trocar-senha') {
    return { name: 'trocar-senha' }
  }

  return true
})

export default router
