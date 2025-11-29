<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Mail, Lock, Eye, EyeOff } from 'lucide-vue-next';
import api from '../services/api';

const email = ref('');
const password = ref('');
const errorMsg = ref('');
const isLoading = ref(false);
const showPassword = ref(false);
const router = useRouter();

// --- Validaciones ---
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

// --- Manejadores ---
const handleLogin = async () => {
  if (!isFormValid.value) return;

  isLoading.value = true;
  errorMsg.value = '';

  try {
    await api.post('/auth/login', {
      username: email.value,
      password: password.value
    });

    localStorage.setItem('isAuthenticated', 'true');
    router.push('/dashboard');
  } catch (error) {
    if (error.response && error.response.status === 401) {
      errorMsg.value = 'Credenciales inválidas. Verifica tu correo.';
    } else {
      errorMsg.value = 'Error de conexión. Intenta más tarde.';
    }
    console.error('Error de login:', error);
  } finally {
    isLoading.value = false;
  }
};

const clearError = () => {
  if (errorMsg.value) errorMsg.value = '';
};

const onNavigate = (screen) => {
  if (screen === 'register') {
    router.push('/register');
  }
};
</script>

<template>
  <div class="min-h-screen flex items-center justify-center p-4 relative overflow-hidden">
    
    <div class="auth-bg-icon top-10 left-10 text-8xl" style="animation-duration: 3s">☕</div>
    <div class="auth-bg-icon bottom-20 right-20 text-8xl" style="animation-duration: 4s; animation-delay: 1s">🍪</div>
    <div class="auth-bg-icon top-1/2 right-10 text-6xl" style="animation-duration: 3.5s; animation-delay: 0.5s">🥐</div>
    <div class="auth-bg-icon bottom-10 left-20 text-7xl" style="animation-duration: 4.5s; animation-delay: 1.5s">🍩</div>
    <div class="auth-bg-icon top-5 left-1/2 text-5xl opacity-10" style="animation-duration: 5s">🪙</div>
    <div class="auth-bg-icon top-1/3 left-5 text-4xl" style="animation-duration: 2.5s; animation-delay: 0.2s">🍞</div>
    
    <div class="w-full max-w-md relative z-10">
      
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-20 h-20 rounded-full bg-cookie-dough shadow-lg mb-4 animate-pulse">
          <span class="text-5xl">🍪</span>
        </div>
        <h1 class="text-4xl font-bold text-dark-chocolate mb-2">monedaDOS</h1>
        <p class="text-brown-muted">Tu cafetería financiera</p>
      </div>

      <div class="auth-card">
        <h2 class="text-2xl font-bold text-dark-chocolate text-center mb-2">¡Bienvenido de vuelta!</h2>
        <p class="text-sm text-brown-muted text-center mb-6">
          Ingresa para ver tu menú del día
        </p>

        <form @submit.prevent="handleLogin" class="space-y-5">
          <div v-if="errorMsg" class="msg-error">
            <span class="text-xl">⚠️</span>
            <span>{{ errorMsg }}</span>
          </div>

          <div>
            <label class="form-label" for="username">Email</label>
            <div class="relative">
              <Mail class="input-icon-left" :size="20" />
              <input 
                v-model.trim="email" 
                @input="clearError" 
                type="email" 
                id="username"
                class="input-pill" 
                placeholder="tu@email.com" 
                :disabled="isLoading" 
                required 
                autocomplete="email"
              >
            </div>
            <span v-if="email && !isEmailValid" class="field-error-text">
              Email inválido
            </span>
          </div>

          <div>
            <label class="form-label" for="password">Contraseña</label>
            <div class="relative">
              <Lock class="input-icon-left" :size="20" />
              <input 
                v-model="password" 
                @input="clearError" 
                :type="showPassword ? 'text' : 'password'" 
                id="password"
                class="input-pill input-pill-password"
                placeholder="••••••••" 
                :disabled="isLoading" 
                required 
                autocomplete="current-password"
              >
              <button 
                type="button" 
                @click="showPassword = !showPassword"
                class="input-icon-right"
                :disabled="isLoading"
              >
                <component :is="showPassword ? EyeOff : Eye" :size="20" />
              </button>
            </div>
            <span v-if="password && !isPasswordValid" class="field-error-text">
              Mínimo 6 caracteres
            </span>
          </div>

          <button 
            class="btn-primary" 
            type="submit" 
            :disabled="!isFormValid"
          >
            <span v-if="!isLoading">Entrar a la Cafetería ☕</span>
            <span v-else>Iniciando...</span>
          </button>
        </form>

        <div class="flex items-center gap-4 my-6">
          <div class="flex-1 h-px bg-cookie-dough opacity-50"></div>
          <span class="text-sm text-brown-muted">o</span>
          <div class="flex-1 h-px bg-cookie-dough opacity-50"></div>
        </div>

        <p class="text-center text-sm text-brown-muted">
          ¿Primera vez aquí?
          <button @click="onNavigate('register')" class="link-inline font-bold">
            Crea tu cuenta
          </button>
        </p>
      </div>

      <div class="mt-6 flex items-center justify-center gap-6 text-sm text-brown-muted font-medium">
        <div class="flex items-center gap-2">
          <span class="text-xl">🔒</span><span>Seguro</span>
        </div>
        <div class="flex items-center gap-2">
          <span class="text-xl">🍪</span><span>Delicioso</span>
        </div>
        <div class="flex items-center gap-2">
          <span class="text-xl">💰</span><span>Gratis</span>
        </div>
      </div>
    </div>
  </div>
</template>