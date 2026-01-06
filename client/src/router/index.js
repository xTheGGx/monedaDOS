import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Cuentas from '../views/Cuentas.vue';
import Register from '../views/Register.vue';
import NotFound from '../views/NotFound.vue';
import Transacciones from '../views/Transacciones.vue';

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        component: Login,
        name : 'Login',
        meta: {
            requiresGuest: true,
            tittle: 'Iniciar Sesión - monedaDOS'
        } // Solo para usuarios no autenticados
    },
    {
        path: '/register',
        component: Register,
        name : 'Register',
        meta: {
            requiresGuest: true,
            tittle: 'Registrar Usuario - monedaDOS'
        } // Solo para usuarios no autenticados
    },
    {
        path: '/cuentas',
        component: Cuentas,
        name : 'Cuentas',
        meta: {
            requiresAuth: true,
            tittle: 'Cuentas - monedaDOS'
        } // Requiere autenticación
    },
    {
        path: '/transacciones',
        component: Transacciones,
        name : 'Transacciones',
        meta: {
            requiresAuth: true, // Requiere autenticación
            tittle: 'Transacciones - monedaDOS'
        } // Requiere autenticación
    },
    /* {
        path: '/dashboard',
        component: Dashboard,
        meta: {
            requiresAuth: true,
            tittle: 'Dashboard - monedaDOS'
        } // Requiere autenticación
    }, */
    // A Panel, Transacciones, etc.

    // Ruta para manejar 404 - Not Found
    {
        path: '/:pathMatch(.*)*', 
            name: 'NotFound', 
            component: NotFound,
            meta: {
                requiresAuth: true,
                tittle: '404 - Not Found - monedaDOS'
            }   
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
    // Verificamos la bandera booleana, NO el token real
    const isAuth = localStorage.getItem('isAuthenticated') === 'true';

    if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!isAuth) {
            next({ path: '/login' });
        } else {
            next();
        }
    } else {
        next();
    }
});

export default router;