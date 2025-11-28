<script setup>
import { RouterView } from 'vue-router'
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import Navbar from './components/Navbar.vue';
import Footer from './components/Footer.vue';

const route = useRoute();
const showLayout = computed(() => {
  // Si la ruta aún no se ha resuelto (carga inicial), no mostrar nada
  if (!route.name) return false; 
  
  // Ocultar en Login y Register
  return !route.meta.requiresGuest;
});
</script>

<template>
  <div class="page-wrapper flex flex-col min-h-screen">
    <Navbar v-if="showLayout" />

    <div class="flex-grow">
      <RouterView />
    </div>

    <Footer v-if="showLayout" />
  </div>
</template>

<style>
/* Aseguramos que el html/body ocupen el 100% para que el footer se pegue abajo */
html, body {
  height: 100%;
  margin: 0;
}
</style>