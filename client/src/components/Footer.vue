<script setup>
import { Heart, Github, Mail } from 'lucide-vue-next';

const currentYear = new Date().getFullYear();
</script>

<template>
  <footer class="bg-oatmeal-paper border-t-4 border-cookie-dough mt-auto">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8 mb-8">
        
        <div>
          <div class="flex items-center gap-3 mb-4">
            <div class="w-10 h-10 rounded-full bg-cookie-dough flex items-center justify-center shadow-sm">
              <span class="text-xl select-none">🍪</span>
            </div>
            <h3 class="text-xl font-bold text-dark-chocolate">monedaDOS</h3>
          </div>
          <p class="text-sm text-brown-muted mb-4 leading-relaxed">
            Tu cafetería financiera donde gestionar tu dinero es tan dulce como disfrutar un café.
          </p>
          <div class="flex items-center gap-2 text-sm text-brown-muted font-medium">
            <span>Hecho con</span>
            <Heart :size="16" class="text-berry-jam fill-current animate-pulse" />
            <span>y mucho café</span>
          </div>
        </div>

        <div>
          <h4 class="text-dark-chocolate font-bold mb-4">Menú Principal</h4>
          <ul class="space-y-2">
            <li v-for="(label, index) in ['🏠 Inicio', '💸 Movimientos', '💰 Cuentas', '📊 Reportes']" :key="index">
              <a href="#" class="text-sm text-brown-muted hover:text-cookie-dough transition-colors font-medium">
                {{ label }}
              </a>
            </li>
          </ul>
        </div>

        <div>
          <h4 class="text-dark-chocolate font-bold mb-4">Soporte</h4>
          <ul class="space-y-2">
            <li v-for="(label, index) in ['📚 Guía de Usuario', '❓ FAQ', '🐛 Reportar Bug', '💡 Sugerencias']" :key="index">
              <a href="#" class="text-sm text-brown-muted hover:text-cookie-dough transition-colors font-medium">
                {{ label }}
              </a>
            </li>
          </ul>
        </div>
      </div>

      <div class="border-t-2 border-cookie-dough border-dashed mb-6 opacity-50"></div>

      <div class="flex flex-col md:flex-row justify-between items-center gap-4">
        <p class="text-sm text-brown-muted">
          © {{ currentYear }} monedaDOS. Todos los derechos reservados.
        </p>

        <div class="flex items-center gap-4">
          <a href="#" class="w-10 h-10 rounded-full bg-cookie-dough hover:bg-cookie-hover flex items-center justify-center transition-all text-white shadow-sm hover:scale-110" title="GitHub">
            <Github :size="18" />
          </a>
          <a href="#" class="w-10 h-10 rounded-full bg-matcha-tea hover:bg-green-600 flex items-center justify-center transition-all text-white shadow-sm hover:scale-110" title="Contacto">
            <Mail :size="18" />
          </a>
        </div>
      </div>

      <div class="mt-8 text-center select-none opacity-30">
        <div class="flex items-center justify-center gap-4 text-2xl">
          <span>☕</span><span>🍪</span><span>🥐</span><span>🧁</span><span>🍩</span>
        </div>
      </div>
    </div>
  </footer>
</template>