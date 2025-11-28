<script setup>
import { ref, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { Menu, X, User, LogOut, Home, Banknote, Wallet, BarChart3, Settings } from 'lucide-vue-next';
import api from '../services/api';

const router = useRouter();
const route = useRoute();
const isMobileMenuOpen = ref(false);

// Obtener usuario (Mejorarlo con un store de Pinia  o leerlo de localStorage)
const userName = ref('Usuario'); 

const menuItems = [
  { path: '/dashboard', label: 'Inicio', icon: Home },
  { path: '/transacciones', label: 'Movimientos', icon: Banknote },
  { path: '/cuentas', label: 'Cuentas', icon: Wallet },
  { path: '/reports', label: 'Reportes', icon: BarChart3 },
  // { path: '/settings', label: 'Configuración', icon: Settings },
];

const isActive = (path) => route.path === path;

const navigateTo = (path) => {
  router.push(path);
  isMobileMenuOpen.value = false;
};

const logout = async () => {
  try {
    await api.post('/auth/logout');
  } catch (e) {
    console.error(e);
  } finally {
    localStorage.removeItem('isAuthenticated');
    router.push('/login');
    isMobileMenuOpen.value = false;
  }
};
</script>

<template>
  <nav class="sticky top-0 z-50 bg-oatmeal-paper border-b-4 border-cookie-dough shadow-lg">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-20">
        
        <div class="flex items-center gap-3 cursor-pointer group" @click="navigateTo('/dashboard')">
          <div class="w-12 h-12 rounded-full bg-cookie-dough flex items-center justify-center shadow-md group-hover:scale-110 transition-transform">
            <span class="text-2xl select-none">🍪</span>
          </div>
          <div class="hidden sm:block">
            <h1 class="text-2xl font-bold text-dark-chocolate leading-none">monedaDOS</h1>
            <p class="text-xs text-brown-muted font-medium">Tu cafetería financiera</p>
          </div>
        </div>

        <div class="hidden md:flex items-center gap-2">
          <button
            v-for="item in menuItems"
            :key="item.path"
            @click="navigateTo(item.path)"
            class="px-4 py-2 rounded-full transition-all flex items-center gap-2 font-medium text-sm"
            :class="isActive(item.path) 
              ? 'bg-cookie-dough text-white shadow-md' 
              : 'text-dark-chocolate hover:bg-cookie-dough/10'"
          >
            <component :is="item.icon" :size="18" />
            {{ item.label }}
          </button>
        </div>

        <div class="hidden md:flex items-center gap-4">
          <div class="text-right hidden lg:block">
            <p class="text-sm font-bold text-dark-chocolate">Hola, {{ userName }}!</p>
            <p class="text-xs text-brown-muted">Bienvenido de vuelta</p>
          </div>
          
          <div class="w-10 h-10 rounded-full bg-matcha-tea flex items-center justify-center shadow-sm">
            <User :size="20" class="text-white" />
          </div>
          
          <button
            @click="logout"
            class="w-10 h-10 rounded-full bg-berry-jam hover:bg-red-600 flex items-center justify-center transition-colors shadow-md text-white"
            title="Cerrar sesión"
          >
            <LogOut :size="18" />
          </button>
        </div>

        <button
          @click="isMobileMenuOpen = !isMobileMenuOpen"
          class="md:hidden w-12 h-12 rounded-full bg-cookie-dough hover:bg-cookie-hover flex items-center justify-center text-white transition-colors shadow-sm"
        >
          <component :is="isMobileMenuOpen ? X : Menu" :size="24" />
        </button>
      </div>
    </div>

    <div v-if="isMobileMenuOpen" class="md:hidden bg-milk-cream border-t-2 border-cookie-dough">
      <div class="px-4 py-4 space-y-2">
        
        <div class="flex items-center gap-3 p-3 bg-oatmeal-paper rounded-2xl mb-4 border border-cookie-dough/20">
          <div class="w-12 h-12 rounded-full bg-matcha-tea flex items-center justify-center shadow-sm">
            <User :size="24" class="text-white" />
          </div>
          <div>
            <p class="font-bold text-dark-chocolate">Hola, {{ userName }}!</p>
            <p class="text-xs text-brown-muted">Bienvenido de vuelta</p>
          </div>
        </div>

        <button
          v-for="item in menuItems"
          :key="item.path"
          @click="navigateTo(item.path)"
          class="w-full text-left px-4 py-3 rounded-2xl transition-all flex items-center gap-3 font-medium"
          :class="isActive(item.path)
            ? 'bg-cookie-dough text-white shadow-md'
            : 'bg-white text-dark-chocolate hover:bg-oatmeal-paper border border-transparent hover:border-cookie-dough/20'"
        >
          <component :is="item.icon" :size="20" />
          {{ item.label }}
        </button>

        <button
          @click="logout"
          class="w-full text-left px-4 py-3 rounded-2xl bg-berry-jam/10 text-berry-jam hover:bg-berry-jam hover:text-white transition-all flex items-center gap-3 font-bold mt-4 border border-berry-jam/20"
        >
          <LogOut :size="20" />
          Cerrar Sesión
        </button>
      </div>
    </div>
  </nav>
</template>