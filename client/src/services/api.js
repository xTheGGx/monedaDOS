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

// Interceptor: Antes de cada petición, inyecta el Token si existe
api.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
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
        // Si el token expiró o es inválido (401)
        if (error.response?.status === 401) {
            localStorage.removeItem('token');
            router.push('/login');
            return Promise.reject(new Error('Sesión expirada. Por favor, inicia sesión nuevamente.'));
        }
        
        // Si hay error de red
        if (!error.response) {
            console.error('Error de red:', error);
            return Promise.reject(new Error('No se pudo conectar con el servidor. Verifica tu conexión.'));
        }
        
        return Promise.reject(error);
    }
);

export default api;