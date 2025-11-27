import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Cuentas from '../views/Cuentas.vue';
import Register from '../views/Register.vue';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        { 
            path: '/', 
            redirect: '/login' 
        },
        { 
            path: '/login', 
            component: Login,
            meta: { requiresGuest: true } // Solo para usuarios no autenticados
        },
        { 
            path: '/register', 
            component: Register,
            meta: { requiresGuest: true } // Solo para usuarios no autenticados
        },
        { 
            path: '/cuentas', 
            component: Cuentas,
            meta: { requiresAuth: true } // Requiere autenticación
        },
        // Agrega aquí Panel, Transacciones, etc.
    ]
});

// Guard de navegación global
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token');
    const isAuthenticated = !!token;

    // Si la ruta requiere autenticación y no hay token
    if (to.meta.requiresAuth && !isAuthenticated) {
        next('/login');
    } 
    // Si la ruta es para invitados y ya está autenticado
    else if (to.meta.requiresGuest && isAuthenticated) {
        next('/cuentas');
    } 
    // Permitir navegación normal
    else {
        next();
    }
});

export default router;