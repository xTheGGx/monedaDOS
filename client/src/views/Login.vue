<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const email = ref('');
const password = ref('');
const errorMsg = ref('');
const router = useRouter();

const handleLogin = async () => {
    try {
        const response = await api.post('/auth/login', {
            username: email.value,
            password: password.value
        });
        
        // Guardar token y redirigir
        localStorage.setItem('token', response.data.token);
        router.push('/cuentas');
    } catch (error) {
        errorMsg.value = 'Credenciales inválidas. Intenta de nuevo.';
    }
};
</script>

<template>
  <div class="login-wrapper">
    <div class="login-container">
        <div class="login-header">
            <img alt="Stitch waving" class="login-logo" src="/images/dos_happy.png" />
            <h1>¡Aloha! Qué bueno verte</h1>
            <p>¡Vamos a poner en orden esas monedas!</p>
        </div>

        <div class="card">
            <form @submit.prevent="handleLogin" class="login-form">
                <div v-if="errorMsg" class="login-error">
                    <span class="material-symbols-outlined">warning</span>
                    <span>{{ errorMsg }}</span>
                </div>

                <div class="form-group">
                    <label for="username">Email</label>
                    <div class="input-with-icon">
                        <span class="material-symbols-outlined">mail</span>
                        <input v-model="email" type="email" id="username" class="form-input" placeholder="tu@email.com" required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="password">Contraseña</label>
                    <div class="input-with-icon">
                        <span class="material-symbols-outlined">lock</span>
                        <input v-model="password" type="password" id="password" class="form-input" placeholder="••••••••" required>
                    </div>
                </div>

                <button class="button-primary" type="submit">Iniciar Sesión</button>
            </form>
        </div>
    </div>
  </div>
</template>