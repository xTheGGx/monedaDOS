<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
// Iconos
import { Mail, Lock, User, Eye, EyeOff, BadgeCheck } from 'lucide-vue-next';
import api from '../services/api';

// --- Estado ---
const form = ref({
  nombre: '',
  apellidoPaterno: '',
  apellidoMaterno: '', // Opcional según tu DTO, pero lo incluimos
  email: '',
  password: '',
  confirmPassword: ''
});

const showPassword = ref(false);
const acceptTerms = ref(false);
const isLoading = ref(false);
const errorMsg = ref('');
const successMsg = ref('');

const router = useRouter();

// --- Validaciones ---
const passwordsMatch = computed(() => {
  return form.value.password === form.value.confirmPassword;
});

const isFormValid = computed(() => {
  return (
    form.value.nombre &&
    form.value.email &&
    form.value.password.length >= 8 &&
    passwordsMatch.value &&
    acceptTerms.value
  );
});

// --- Manejadores ---
const handleRegister = async () => {
  errorMsg.value = '';
  successMsg.value = '';

  if (!acceptTerms.value) {
    errorMsg.value = 'Debes aceptar los términos y condiciones.';
    return;
  }

  if (!passwordsMatch.value) {
    errorMsg.value = 'Las contraseñas no coinciden.';
    return;
  }

  isLoading.value = true;

  try {
    // Llamada a tu endpoint real de Spring Boot
    await api.post('/auth/register', {
      nombre: form.value.nombre,
      apellidoPat: form.value.apellidoPaterno, // Ajustado al DTO RegisterRequest.java
      apellidoMat: form.value.apellidoMaterno, // Ajustado al DTO RegisterRequest.java
      email: form.value.email,
      password: form.value.password
    });

    successMsg.value = '¡Cuenta creada! Redirigiendo...';
    
    // Esperar 1.5s para que el usuario lea el mensaje antes de ir al login
    setTimeout(() => {
      router.push('/login');
    }, 1500);

  } catch (error) {
    console.error(error);
    if (error.response?.data?.message) {
        // Mensaje específico del backend (ej: "Email ya en uso")
        errorMsg.value = error.response.data.message;
    } else {
        errorMsg.value = 'Error al registrar. Intenta nuevamente.';
    }
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div class="min-h-screen flex items-center justify-center p-4 relative overflow-hidden">
    
    <div class="auth-bg-icon top-10 right-10 text-8xl" style="animation-duration: 3s">🥐</div>
    <div class="auth-bg-icon bottom-20 left-20 text-8xl" style="animation-duration: 4s; animation-delay: 1s">🍩</div>
    <div class="auth-bg-icon top-1/2 left-10 text-6xl" style="animation-duration: 3.5s; animation-delay: 0.5s">🧁</div>
    <div class="auth-bg-icon bottom-10 right-32 text-7xl" style="animation-duration: 5s">🥨</div>

    <div class="w-full max-w-md relative z-10">
      
      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center w-20 h-20 rounded-full bg-cookie-dough shadow-lg mb-4 animate-pulse">
          <span class="text-5xl">🍪</span>
        </div>
        <h1 class="text-4xl font-bold text-dark-chocolate mb-2">Únete a monedaDOS</h1>
        <p class="text-brown-muted">Crea tu cuenta en segundos</p>
      </div>

      <div class="auth-card">
        <h2 class="text-2xl font-bold text-dark-chocolate text-center mb-2">¡Hora de empezar!</h2>
        <p class="text-sm text-brown-muted text-center mb-6">
          Completa tus datos para gestionar tu dinero
        </p>

        <form @submit.prevent="handleRegister" class="space-y-4">
          
          <div v-if="errorMsg" class="msg-error">
            <span class="text-xl">⚠️</span>
            <span>{{ errorMsg }}</span>
          </div>
          <div v-if="successMsg" class="msg-success">
            <span class="text-xl">🎉</span>
            <span>{{ successMsg }}</span>
          </div>

          <div>
            <label class="form-label">Nombre</label>
            <div class="relative">
              <User class="input-icon-left" :size="20" />
              <input 
                v-model="form.nombre" 
                type="text" 
                class="input-pill" 
                placeholder="Ej. Juan" 
                required 
              />
            </div>
          </div>

          <div class="grid grid-cols-2 gap-3">
            <div>
              <label class="form-label">Apellido P.</label>
              <div class="relative">
                <input 
                  v-model="form.apellidoPaterno" 
                  type="text" 
                  class="input-pill !pl-4" 
                  placeholder="Pérez" 
                />
              </div>
            </div>
            <div>
              <label class="form-label">Apellido M.</label>
              <div class="relative">
                <input 
                  v-model="form.apellidoMaterno" 
                  type="text" 
                  class="input-pill !pl-4" 
                  placeholder="López" 
                />
              </div>
            </div>
          </div>

          <div>
            <label class="form-label">Correo Electrónico</label>
            <div class="relative">
              <Mail class="input-icon-left" :size="20" />
              <input 
                v-model="form.email" 
                type="email" 
                class="input-pill" 
                placeholder="tu@email.com" 
                required 
              />
            </div>
          </div>

          <div>
            <label class="form-label">Contraseña</label>
            <div class="relative">
              <Lock class="input-icon-left" :size="20" />
              <input 
                v-model="form.password" 
                :type="showPassword ? 'text' : 'password'" 
                class="input-pill input-pill-password" 
                placeholder="Mínimo 8 caracteres" 
                required 
                minlength="8"
              />
              <button type="button" @click="showPassword = !showPassword" class="input-icon-right">
                <component :is="showPassword ? EyeOff : Eye" :size="20" />
              </button>
            </div>
          </div>

          <div>
            <label class="form-label">Confirmar Contraseña</label>
            <div class="relative">
              <BadgeCheck class="input-icon-left" :size="20" />
              <input 
                v-model="form.confirmPassword" 
                :type="showPassword ? 'text' : 'password'" 
                class="input-pill" 
                placeholder="Repite tu contraseña" 
                required 
              />
            </div>
            <span v-if="form.password && !passwordsMatch" class="field-error-text">
              Las contraseñas no coinciden
            </span>
          </div>

          <div class="flex items-center gap-3 pt-2">
            <input 
              v-model="acceptTerms" 
              type="checkbox" 
              id="terms"
              class="w-5 h-5 rounded border-2 border-cookie-dough text-cookie-dough focus:ring-cookie-dough cursor-pointer"
            />
            <label for="terms" class="text-sm text-brown-muted cursor-pointer select-none">
              Acepto los <a href="#" class="link-inline">términos y condiciones</a>
            </label>
          </div>

          <button type="submit" class="btn-primary" :disabled="!isFormValid || isLoading">
            <span v-if="!isLoading">Crear mi Cuenta 🎉</span>
            <span v-else>Registrando...</span>
          </button>

        </form>

        <div class="flex items-center gap-4 my-6">
          <div class="flex-1 h-px bg-cookie-dough opacity-50"></div>
          <span class="text-sm text-brown-muted">o</span>
          <div class="flex-1 h-px bg-cookie-dough opacity-50"></div>
        </div>

        <p class="text-center text-sm text-brown-muted">
          ¿Ya tienes cuenta? 
          <router-link to="/login" class="link-inline">
            Inicia sesión
          </router-link>
        </p>
      </div>

      <div class="mt-6 grid grid-cols-3 gap-4">
        <div class="bg-oatmeal-paper rounded-2xl p-4 text-center shadow-md">
          <div class="text-3xl mb-2">🎯</div>
          <p class="text-xs text-brown-muted font-bold">Control total</p>
        </div>
        <div class="bg-oatmeal-paper rounded-2xl p-4 text-center shadow-md">
          <div class="text-3xl mb-2">📊</div>
          <p class="text-xs text-brown-muted font-bold">Reportes</p>
        </div>
        <div class="bg-oatmeal-paper rounded-2xl p-4 text-center shadow-md">
          <div class="text-3xl mb-2">🔒</div>
          <p class="text-xs text-brown-muted font-bold">100% Seguro</p>
        </div>
      </div>

    </div>
  </div>
</template>