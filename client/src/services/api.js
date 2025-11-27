import axios from 'axios';
import router from '../router';

// URL base de  Backend Spring Boot
const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json'
    },
    timeout: 10000 // 10 segundos de timeout
});

const isTokenExpired = (token) => {
    if (!token) return true;

    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const expiry = payload.exp * 1000;
        return Date.now() > expiry;
    } catch (e) {
        console.error('Error decodificando token: ', e);
        return true;
    }
}

// Interceptor: Antes de cada petición, inyecta el Token si existe
api.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        // Validar token antes de enviarlo
        if (token && !isTokenExpired(token)) {
            config.headers.Authorization = `Bearer ${token}`;
        } else if (token && isTokenExpired(token)) {
            // Token expiro, limpiar y redirigir
            localStorage.removeItem('token');
            router.push('/login');
            return Promise.reject(new Error('La sesión caducó'));
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// Interceptor de respuesta: Maneja errores globalmente
api.interceptors.response.use(
    response => response,
    error => {
        // Error 401: No autorizado
        if (error.response?.status === 401) {
            localStorage.removeItem('token');
            router.push('/login');
            return Promise.reject(new Error('Sesión expirada'));
        }
        
        // Error 403: Sin permisos
        if (error.response?.status === 403) {
            return Promise.reject(new Error('No tienes permisos para esta acción'));
        }
        
        // Error 500: Error del servidor
        if (error.response?.status >= 500) {
            return Promise.reject(new Error('Error del servidor. Intenta más tarde'));
        }
        
        // Error de red
        if (!error.response) {
            return Promise.reject(new Error('No se pudo conectar con el servidor'));
        }
        
        return Promise.reject(error);
    }
);

export default api;