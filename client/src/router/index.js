import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Cuentas from '../views/Cuentas.vue';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        { path: '/', redirect: '/login' },
        { path: '/login', component: Login },
        { path: '/cuentas', component: Cuentas },
        // Agrega aquí Panel, Transacciones, etc.
    ]
});

export default router;