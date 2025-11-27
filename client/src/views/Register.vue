<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '../services/api';

const nombre = ref('');
const apellidoPaterno = ref('');
const apellidoMaterno = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');

const errorMsg = ref('');
const successMsg = ref('');
const router = useRouter();

const handleRegister = async () => {
  errorMsg.value = '';
  successMsg.value = '';

  if (password.value !== confirmPassword.value) {
    errorMsg.value = 'Las contraseñas no coinciden.';
    return;
  }

  try {
    await api.post('/api/auth/register', {
      nombre: nombre.value,
      apellidoPaterno: apellidoPaterno.value,
      apellidoMaterno: apellidoMaterno.value,
      email: email.value,
      password: password.value,
    });

    successMsg.value = '¡Cuenta creada con éxito! Ahora puedes iniciar sesión.';
    // Redirigir al login después de crear la cuenta
    router.push('/login');
  } catch (error) {
    errorMsg.value = 'No se pudo registrar el usuario. Revisa los datos o intenta más tarde.';
  }
};
</script>

<template>
  <div class="login-wrapper">
    <div class="login-container">
      <!-- HEADER-->
      <div class="login-header">
        <img
          alt="Stitch happy"
          class="login-logo"
          src="/images/dos_happy.png"
        />
        <h1>¡Bienvenido a monedaDOS!</h1>
        <p>Crea tu cuenta y empieza a domar tus finanzas.</p>
      </div>

      <div class="card">
        <form @submit.prevent="handleRegister" class="login-form">
          <div v-if="errorMsg" class="login-error">
            <span class="material-symbols-outlined">warning</span>
            <span>{{ errorMsg }}</span>
          </div>

          <div v-if="successMsg" class="login-success">
            <span class="material-symbols-outlined">check_circle</span>
            <span>{{ successMsg }}</span>
          </div>

          <div class="form-group">
            <label for="nombre">Nombre</label>
            <div class="input-with-icon">
              <span class="material-symbols-outlined">badge</span>
              <input
                v-model="nombre"
                type="text"
                id="nombre"
                class="form-input"
                placeholder="Tu nombre"
                required
              />
            </div>
          </div>

          <div class="form-group">
            <label for="apellidoPaterno">Apellido paterno</label>
            <div class="input-with-icon">
              <span class="material-symbols-outlined">badge</span>
              <input
                v-model="apellidoPaterno"
                type="text"
                id="apellidoPaterno"
                class="form-input"
                placeholder="Tu primer apellido"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="apellidoMaterno">Apellido materno</label>
            <div class="input-with-icon">
              <span class="material-symbols-outlined">badge</span>
              <input
                v-model="apellidoMaterno"
                type="text"
                id="apellidoMaterno"
                class="form-input"
                placeholder="Tu segundo apellido"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="email">Email</label>
            <div class="input-with-icon">
              <span class="material-symbols-outlined">mail</span>
              <input
                v-model="email"
                type="email"
                id="email"
                class="form-input"
                placeholder="tu@email.com"
                required
              />
            </div>
          </div>

          <div class="form-group">
            <label for="password">Contraseña</label>
            <div class="input-with-icon">
              <span class="material-symbols-outlined">lock</span>
              <input
                v-model="password"
                type="password"
                id="password"
                class="form-input"
                placeholder="••••••••"
                required
              />
            </div>
          </div>

          <div class="form-group">
            <label for="confirmPassword">Confirmar contraseña</label>
            <div class="input-with-icon">
              <span class="material-symbols-outlined">lock</span>
              <input
                v-model="confirmPassword"
                type="password"
                id="confirmPassword"
                class="form-input"
                placeholder="Repite tu contraseña"
                required
              />
            </div>
          </div>

          <button class="button-primary" type="submit">
            Crear cuenta
          </button>

          <p class="login-helper">
            ¿Ya tienes cuenta?
            <router-link to="/login" class="link-inline">
              Inicia sesión
            </router-link>
          </p>
        </form>
      </div>
    </div>
  </div>
</template>
