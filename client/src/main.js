import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// Importa tus estilos antiguos aquí
import './assets/css/styles.css' 
import './assets/css/cuentas.css' // Opcional: cárgalo solo en la vista Cuentas si prefieres

createApp(App).use(router).mount('#app')