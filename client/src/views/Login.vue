<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const email = ref('');
const password = ref('');
const errorMsg = ref('');
const isLoading = ref(false);
const router = useRouter();

// Validaciones
const isEmailValid = computed(() => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return email.value === '' || emailRegex.test(email.value);
});

const isPasswordValid = computed(() => {
    return password.value === '' || password.value.length >= 3;
});

const isFormValid = computed(() => {
    return email.value && 
           password.value && 
           isEmailValid.value && 
           isPasswordValid.value &&
           !isLoading.value;
});

const handleLogin = async () => {
    // Reset error
    errorMsg.value = '';
    
    // Validación final
    if (!isFormValid.value) {
        errorMsg.value = 'Por favor completa todos los campos correctamente';
        return;
    }

    isLoading.value = true;

    try {
        const response = await api.post('/auth/login', {
            username: email.value,
            password: password.value
        });
        
        // Validar que el backend envió un token
        if (!response.data?.token) {
            throw new Error('Respuesta inválida del servidor');
        }

        // Guardar token
        localStorage.setItem('token', response.data.token);
        
        // Opcional: Guardar info del usuario
        if (response.data.usuario) {
            localStorage.setItem('usuario', JSON.stringify(response.data.usuario));
        }

        // Redirigir
        router.push('/cuentas');
        
    } catch (error) {
        console.error('Error en login:', error);
        
        // Mensajes de error específicos
        if (error.response?.status === 401) {
            errorMsg.value = 'Email o contraseña incorrectos';
        } else if (error.response?.status === 403) {
            errorMsg.value = 'Cuenta bloqueada. Contacta al administrador';
        } else if (!error.response) {
            errorMsg.value = 'No se pudo conectar con el servidor';
        } else {
            errorMsg.value = 'Error al iniciar sesión. Intenta de nuevo';
        }
    } finally {
        isLoading.value = false;
    }
};

// Limpiar error al escribir
const clearError = () => {
    if (errorMsg.value) errorMsg.value = '';
};
</script>

<template>
  <div class="login-wrapper">
    <div class="login-container">
        <div class="login-header">
            <img alt="MonedaDOS Logo" class="login-logo" src="/images/dos_happy.png" />
            <h1>¡Aloha! Qué bueno verte</h1>
            <p>¡Vamos a poner en orden esas monedas!</p>
        </div>

        <div class="card">
            <form @submit.prevent="handleLogin" class="login-form">
                <!-- Error Message -->
                <div v-if="errorMsg" class="login-error">
                    <span class="material-symbols-outlined">warning</span>
                    <span>{{ errorMsg }}</span>
                </div>

                <!-- Email Field -->
                <div class="form-group">
                    <label for="username">Email</label>
                    <div class="input-with-icon">
                        <span class="material-symbols-outlined">mail</span>
                        <input 
                            v-model.trim="email" 
                            @input="clearError"
                            type="email" 
                            id="username" 
                            class="form-input" 
                            placeholder="tu@email.com" 
                            :disabled="isLoading"
                            required
                            autocomplete="email"
                        >
                    </div>
                    <span v-if="email && !isEmailValid" class="form-error">
                        Email inválido
                    </span>
                </div>

                <!-- Password Field -->
                <div class="form-group">
                    <label for="password">Contraseña</label>
                    <div class="input-with-icon">
                        <span class="material-symbols-outlined">lock</span>
                        <input 
                            v-model="password" 
                            @input="clearError"
                            type="password" 
                            id="password" 
                            class="form-input" 
                            placeholder="••••••••" 
                            :disabled="isLoading"
                            required
                            autocomplete="current-password"
                        >
                    </div>
                    <span v-if="password && !isPasswordValid" class="form-error">
                        Mínimo 6 caracteres
                    </span>
                </div>

                <!-- Submit Button -->
                <button 
                    class="button-primary" 
                    type="submit"
                    :disabled="!isFormValid"
                >
                    <span v-if="!isLoading">Iniciar Sesión</span>
                    <span v-else>Iniciando...</span>
                </button>
            </form>
        </div>
    </div>
  </div>
</template>

<style scoped>
.form-error {
    display: block;
    font-size: 0.75rem;
    color: var(--expense);
    margin-top: 0.25rem;
}

.button-primary:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}
</style>