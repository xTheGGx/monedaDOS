<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const logout = () => {
    localStorage.removeItem('token');
    router.push('/login');
};
</script>

<template>
  <header class="site-header">
    <div class="container header-inner">
        <a href="#" class="brand">
            <img src="/images/font_logo.png" alt="monedaDOS" width="120">
        </a>

        <nav class="main-nav">
            <router-link to="/panel" active-class="is-active">Panel</router-link>
            <router-link to="/transacciones" active-class="is-active">Transacciones</router-link>
            <router-link to="/cuentas" active-class="is-active">Cuentas</router-link>
        </nav>

        <div class="user-menu">
            <button @click="logout" class="button-secondary" style="padding: 0.5rem 1rem; font-size: 0.8rem;">
                <span class="material-symbols-outlined" style="font-size: 1rem;">logout</span> Salir
            </button>
        </div>
    </div>
  </header>
</template>