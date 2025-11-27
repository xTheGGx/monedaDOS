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



export default api;