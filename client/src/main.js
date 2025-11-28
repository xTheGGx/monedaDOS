import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
 
import './assets/css/styles.css' 
import './assets/css/cuentas.css' 
import './assets/css/monedados-theme.css'

createApp(App).use(router).mount('#app')