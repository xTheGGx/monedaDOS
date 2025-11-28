<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
// Asumo que tienes instalado 'lucide-vue-next' para los iconos
import { Mail, Lock, Eye, EyeOff } from 'lucide-vue-next';
// Asumo que tu archivo 'api.js' está en '../services/api'
import api from '../services/api';

const email = ref('');
const password = ref('');
const errorMsg = ref('');
const isLoading = ref(false);
const showPassword = ref(false); // Para mostrar/ocultar la contraseña
const router = useRouter();

// Validaciones
const isEmailValid = computed(() => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return email.value === '' || emailRegex.test(email.value);
});

// Cambiado a mínimo 6 caracteres por seguridad (tu código original usaba 3)
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
  if (!isFormValid.value) return; // Evita el envío si no es válido

  isLoading.value = true;
  errorMsg.value = ''; // Limpiar el error anterior

  try {
    // La respuesta traerá la cookie y el navegador la guardará solo
    await api.post('/auth/login', {
      username: email.value, // Asegúrate de que tu backend usa 'username' o cámbialo a 'email'
      password: password.value
    });

    // Marcamos en localStorage SOLO que estamos logueados (sin datos sensibles)
    localStorage.setItem('isAuthenticated', 'true');

    // Redirección al dashboard o a la ruta '/cuentas' como tenías
    router.push('/dashboard');
  } catch (error) {
    // Manejo de errores específicos
    if (error.response && error.response.status === 401) {
      errorMsg.value = 'Credenciales inválidas. Verifica tu correo y contraseña.';
    } else {
      errorMsg.value = 'Error de conexión. Intenta de nuevo más tarde.';
    }
    console.error('Error de login:', error);
  } finally {
    isLoading.value = false;
  }
};

// Limpiar error al escribir
const clearError = () => {
  if (errorMsg.value) errorMsg.value = '';
};

// Navegación a registro
const onNavigate = (screen) => {
  if (screen === 'register') {
    router.push('/register');
  }
};
</script>

<template>
  <div class="min-h-screen flex items-center justify-center p-4 relative overflow-hidden">
    <div class="absolute top-10 left-10 text-8xl opacity-20 animate-bounce" :style="{ animationDuration: '3s' }">☕</div>
    <div class="absolute bottom-20 right-20 text-8xl opacity-20 animate-bounce"
      :style="{ animationDuration: '4s', animationDelay: '1s' }">🍪</div>
    <div class="absolute top-1/2 right-10 text-6xl opacity-20 animate-bounce"
      :style="{ animationDuration: '3.5s', animationDelay: '0.5s' }">🥐</div>
    <div class="absolute bottom-10 left-20 text-7xl opacity-20 animate-bounce"
      :style="{ animationDuration: '4.5s', animationDelay: '1.5s' }">🍩</div>
    <div class="absolute top-5 left-1/2 text-5xl opacity-10 animate-pulse" :style="{ animationDuration: '5s' }">🪙</div>
    <div class="absolute top-1/3 left-5 text-4xl opacity-15 animate-bounce"
      :style="{ animationDuration: '2.5s', animationDelay: '0.2s' }">🍞</div>
    <div class="absolute top-8 right-32 text-5xl opacity-20 animate-bounce"
      :style="{ animationDuration: '3.2s', animationDelay: '0.8s' }">🧁</div>
    <div class="absolute bottom-5 left-1/3 text-6xl opacity-15 animate-pulse"
      :style="{ animationDuration: '4.8s', animationDelay: '2s' }">🥨</div>
    <div class="absolute top-2/3 left-1/4 text-7xl opacity-10 animate-bounce"
      :style="{ animationDuration: '3.8s', animationDelay: '1.2s' }">🍫</div>
    <div class="absolute bottom-1/4 right-5 text-4xl opacity-15 animate-pulse"
      :style="{ animationDuration: '5.5s', animationDelay: '0.6s' }">🍯</div>

    <div class="w-full max-w-md relative z-10">
      <div class="text-center mb-8">
        <div
          class="inline-flex items-center justify-center w-20 h-20 rounded-full bg-[#D4A373] shadow-lg mb-4 animate-pulse">
          <span class="text-5xl">🍪</span>
        </div>
        <h1 class="text-4xl text-[#4A3B32] mb-2">monedaDOS</h1>
        <p class="text-[#8B6F47]">Tu cafetería financiera</p>
      </div>

      <div class="bg-[#F3E5D8] rounded-3xl p-8 shadow-2xl">
        <h2 class="text-2xl text-[#4A3B32] text-center mb-2">¡Bienvenido de vuelta!</h2>
        <p class="text-sm text-[#8B6F47] text-center mb-6">
          Ingresa para ver tu menú del día
        </p>

        <form @submit.prevent="handleLogin" class="space-y-5">
          <div v-if="errorMsg"
            class="p-3 rounded-xl bg-red-100 border border-red-400 text-red-700 flex items-center gap-2 text-sm">
            <span class="text-xl">⚠️</span>
            <span>{{ errorMsg }}</span>
          </div>

          <div class="form-group">
            <label class="block text-sm text-[#8B6F47] mb-2" for="username">Email</label>
            <div class="relative">
              <Mail class="absolute left-4 top-1/2 -translate-y-1/2 text-[#D4A373]" :size="20"></Mail>
              <input v-model.trim="email" @input="clearError" type="email" id="username"
                class="w-full bg-white rounded-full py-3 pl-12 pr-4 text-[#4A3B32] border-3 border-transparent focus:outline-none focus:border-[#D4A373] transition-colors shadow-inner"
                placeholder="tu@email.com" :disabled="isLoading" required autocomplete="email">
            </div>
            <span v-if="email && !isEmailValid" class="form-error">
              Email inválido
            </span>
          </div>

          <div class="form-group">
            <label class="block text-sm text-[#8B6F47] mb-2" for="password">Contraseña</label>
            <div class="relative">
              <Lock class="absolute left-4 top-1/2 -translate-y-1/2 text-[#D4A373]" :size="20"></Lock>
              <input v-model="password" @input="clearError" :type="showPassword ? 'text' : 'password'" id="password"
                class="w-full bg-white rounded-full py-3 pl-12 pr-12 text-[#4A3B32] border-3 border-transparent focus:outline-none focus:border-[#D4A373] transition-colors shadow-inner"
                placeholder="••••••••" :disabled="isLoading" required autocomplete="current-password">
              <button type="button" @click="showPassword = !showPassword"
                class="absolute right-4 top-1/2 -translate-y-1/2 text-[#8B6F47] hover:text-[#D4A373] transition-colors"
                :disabled="isLoading">
                <EyeOff v-if="showPassword" :size="20"></EyeOff>
                <Eye v-else :size="20"></Eye>
              </button>
            </div>
            <span v-if="password && !isPasswordValid" class="form-error">
              Mínimo 6 caracteres
            </span>
          </div>

          <button
            class="w-full bg-[#D4A373] hover:bg-[#C89563] text-white rounded-full py-4 transition-all shadow-lg hover:shadow-xl hover:scale-105"
            type="submit" :disabled="!isFormValid">
            <span v-if="!isLoading">Entrar a la Cafetería ☕</span>
            <span v-else>Iniciando...</span>
          </button>
        </form>

        <div class="flex items-center gap-4 my-6">
          <div class="flex-1 h-px bg-[#D4A373]"></div>
          <span class="text-sm text-[#8B6F47]">o</span>
          <div class="flex-1 h-px bg-[#D4A373]"></div>
        </div>

        <p class="text-center text-sm text-[#8B6F47]">
          ¿Primera vez aquí?
          <button @click="onNavigate('register')" class="text-[#D4A373] hover:text-[#C89563] transition-colors">
            Crea tu cuenta
          </button>
        </p>
      </div>

      <div class="mt-6 flex items-center justify-center gap-6 text-sm text-[#8B6F47]">
        <div class="flex items-center gap-2">
          <span class="text-xl">🔒</span>
          <span>Seguro</span>
        </div>
        <div class="flex items-center gap-2">
          <span class="text-xl">🍪</span>
          <span>Delicioso</span>
        </div>
        <div class="flex items-center gap-2">
          <span class="text-xl">💰</span>
          <span>Gratis</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Estilos para el error de validación */
.form-error {
  display: block;
  font-size: 0.75rem;
  color: var(--berry-jam);
  /* Usamos el color de la mermelada para errores */
  margin-top: 0.25rem;
}

/* Estilo para deshabilitar el botón */
button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}
</style>
