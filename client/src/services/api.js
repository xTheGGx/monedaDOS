import axios from 'axios';
import router from '../router';

// URL base de  Backend Spring Boot
const api = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: {
        'Content-Type': 'application/json'
    },
    withCredentials: true // Pemite que las cookies HttpOnly viajen en las peticiones
});

// --- INTERCEPTOR DE RESPUESTA ---
api.interceptors.response.use(
    (response) => {
        // Si la respuesta es exitosa (200-299), la dejamos pasar sin cambios
        return response;
    },
    (error) => {
        // Si hay error, verificamos el código de estado
        if (error.response) {
            const status = error.response.status;

            // CASO 1: Sesión expirada o Token inválido (401)
            if (status === 401 && !error.config.url.includes('/auth/login')) {
                console.warn('Sesión expirada. Redirigiendo al login...');
                
                // Limpiamos la bandera local
                localStorage.removeItem('isAuthenticated');
                
                // Redirigimos al usuario al Login
                // Usamos window.location para forzar una recarga limpia o router.push
                router.push('/login');
            }
            
            // CASO 2: Error del servidor (500)
            if (status >= 500) {
                console.error('Error crítico del servidor:', error.response.data);
                alert('Ocurrió un error en el servidor. Por favor intenta más tarde.');
            }
        } else if (error.request) {
            // CASO 3: No hubo respuesta (Backend caído o sin internet)
            console.error('No se recibió respuesta del servidor.');
            alert('No se pudo conectar con el servidor. Verifica tu conexión.');
        }

        // Rechazamos la promesa para que el componente sepa que falló
        return Promise.reject(error);
    }
);

// Servicios de Transacciones
export const transaccionService = {
  // Obtener todas las transacciones del usuario
  getAll: (params) => api.get('/transacciones', { params }),
  
  // Crear nueva transacción
  create: (data) => api.post('/transacciones', data),
  // Actualizar transacción existente
  update: (id, data) => api.put(`/transacciones/${id}`, data),

  // Eliminar transacción
  delete: (id) => api.delete(`/transacciones/${id}`),
  
  // Obtener por cuenta
  getByCuenta: (cuentaId) => api.get(`/transacciones/cuenta/${cuentaId}`)
};

// Servicios de Categorías
export const categoriaService = {
  getAll: () => api.get('/categorias'),
  getByTipo: (tipo) => api.get(`/categorias/tipo/${tipo}`) // INGRESO o EGRESO
};

// Servicios de Cuentas
export const cuentaService = {
  getAll: () => api.get('/cuentas'),
  getById: (id) => api.get(`/cuentas/${id}`)
};

export default api;