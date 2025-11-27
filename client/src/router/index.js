import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Cuentas from '../views/Cuentas.vue';
import Register from '../views/Register.vue';

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        component: Login,
        meta: {
            requiresGuest: true,
            tittle: 'Iniciar Sesión - monedaDOS'
        } // Solo para usuarios no autenticados
    },
    {
        path: '/register',
        component: Register,
        meta: {
            requiresGuest: true,
            tittle: 'Registrar Usuario - monedaDOS'
        } // Solo para usuarios no autenticados
    },
    {
        path: '/cuentas',
        component: Cuentas,
        meta: {
            requiresAuth: true,
            tittle: 'Cuentas - monedaDOS'
        } // Requiere autenticación
    },
    // A Panel, Transacciones, etc.

    // Ruta para manejar 404 - Not Found
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        redirect: '/error404' // Redirige rutas no definidas a error404
    }

];

const isValidToken = (token) => {
    if (!token) return false;
    
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return Date.now() < payload.exp * 1000;
    } catch {
        return false;
    }
};

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
});


// Guard de navegación global
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token');
    const isAuthenticated = isValidToken(token);

    // Actualizar título de la página
    document.title = to.meta.title || 'MonedaDOS';

    // Ruta requiere autenticación
    if (to.meta.requiresAuth && !isAuthenticated) {
        return next('/login');
    }
    
    // Ruta es solo para invitados (login/register)
    if (to.meta.requiresGuest && isAuthenticated) {
        return next('/cuentas');
    }

    

    next();
});

export default router;