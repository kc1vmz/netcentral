import { createRouter, createWebHistory } from 'vue-router'
import Init from '@/components/Init.vue'

const routes = [
  {
    path: '/',
    name: 'Root',
    component: Init
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue')
  },
  {
    path: '/monitor',
    name: 'Net Monitor',
    component: () => import('../views/Monitor.vue')
  },
  {
    path: '/archives',
    name: 'Archives',
    component: () => import('../views/Archives.vue')
  },
  {
    path: '/explorer',
    name: 'Explorer',
    component: () => import('../views/Explorer.vue')
  },
  {
    path: '/setup',
    name: 'Setup',
    component: () => import('../views/Setup.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/logout',
    name: 'Logout',
    component: () => import('../views/Logout.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/tools',
    name: 'Tools',
    component: () => import('../views/Tools.vue')
  },
  {
    path: '/license',
    name: 'License',
    component: () => import('../views/License.vue')
  },
  {
    path: '/mobile',
    name: 'Mobile',
    component: () => import('../views/Mobile.vue')
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

export default router