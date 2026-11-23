import { createRouter, createWebHistory } from 'vue-router'
import { obterToken, senhaProvisoria } from '@/auth/sessao'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/inicio' },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
    },
    {
      path: '/trocar-senha',
      name: 'trocar-senha',
      meta: { requerAuth: true },
      component: () => import('@/views/TrocarSenhaView.vue'),
    },
    {
      path: '/inicio',
      name: 'inicio',
      meta: { requerAuth: true },
      component: () => import('@/views/InicioView.vue'),
    },
  ],
})

// Guarda de interface. Não substitui a autorização do backend.
router.beforeEach((to) => {
  if (!to.meta.requerAuth) return true
  if (!obterToken()) return { name: 'login' }
  if (senhaProvisoria() && to.name !== 'trocar-senha') return { name: 'trocar-senha' }
  return true
})

export default router
