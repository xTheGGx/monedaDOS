<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';
import Navbar from '../components/Navbar.vue';

// --- Estado ---
const cuentas = ref([]);
const saldoTotal = ref(0);
const isSidebarOpen = ref(false);
const isLoading = ref(false);
const errorMsg = ref('');

// Formulario reactivo
const nuevaCuenta = ref({
    nombre: '',
    tipo: 'EFECTIVO',
    moneda: 'MXN',
    diaCorte: null,
    diaPago: null,
    limiteCredito: null
});

// --- API Calls ---
const cargarCuentas = async () => {
    try {
        const res = await api.get('/cuentas');
        cuentas.value = res.data;
        saldoTotal.value = cuentas.value.reduce((sum, c) => sum + c.saldo, 0);
    } catch (e) {
        console.error("Error específico cargando cuentas", e);
    }
};

const crearCuenta = async () => {
    if (!nuevaCuenta.value.nombre.trim()) {
        alert('El nombre de la cuenta es obligatorio');
        return;
    }

    isLoading.value = true;
    try {
        await api.post('/cuentas', nuevaCuenta.value);
        isSidebarOpen.value = false;
        await cargarCuentas();
        // Reset form
        nuevaCuenta.value = { 
            nombre: '', 
            tipo: 'EFECTIVO', 
            moneda: 'MXN',
            diaCorte: null,
            diaPago: null,
            limiteCredito: null
        };
    } catch (e) {
        alert("Error al crear cuenta: " + (e.response?.data?.mensaje || e.message));
    } finally {
        isLoading.value = false;
    }
};

// Ciclo de vida
onMounted(() => {
    cargarCuentas();
});
</script>

<template>
  <div class="page-wrapper">
    <Navbar />
    
    <main class="site-main">
      <div class="container main-content">
        <div class="main-headline">
            <div>
                <h1>Gestión de Cuentas</h1>
                <p class="subtitle">Tus cuentas organizadas.</p>
            </div>
            <div style="text-align: right;">
                <span style="display: block; font-size: 0.875rem; color: var(--text-muted);">Saldo Total</span>
                <strong style="font-size: 1.5rem; font-weight: 700;">${{ saldoTotal.toFixed(2) }}</strong>
            </div>
        </div>

        <!-- Loading State -->
        <div v-if="isLoading" style="text-align: center; padding: 2rem;">
            <p>Cargando...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="errorMsg" class="login-error" style="margin-bottom: 1rem;">
            <span class="material-symbols-outlined">warning</span>
            <span>{{ errorMsg }}</span>
        </div>

        <!-- Cuentas Grid -->
        <div v-else class="accounts-grid">
            <div v-for="c in cuentas" :key="c.idCuenta" class="card account-card">
                <div class="account-card-header">
                    <h3>{{ c.nombre }}</h3>
                    <span class="badge" :class="c.tipo === 'CREDITO' ? 'badge-yellow' : 'badge-blue'">
                        {{ c.tipo }}
                    </span>
                </div>
                <p class="account-bank">{{ c.moneda }}</p>
                <p class="account-balance" :class="{ 'text-expense': c.saldo < 0, 'text-income': c.saldo >= 0 }">
                    ${{ c.saldo.toFixed(2) }}
                </p>
                
                <!-- Info adicional para crédito -->
                <div v-if="c.tipo === 'CREDITO' && c.limiteCredito" class="account-credit-details">
                    <div>
                        <span>Límite:</span>
                        <strong>${{ c.limiteCredito.toFixed(2) }}</strong>
                    </div>
                    <div>
                        <span>Disponible:</span>
                        <strong>${{ (c.limiteCredito - c.saldo).toFixed(2) }}</strong>
                    </div>
                </div>
            </div>

            <!-- Botón agregar cuenta -->
            <div class="card account-card-add" @click="isSidebarOpen = true">
                <span class="material-symbols-outlined">add_circle</span>
                <strong>Agregar Nueva Cuenta</strong>
            </div>
        </div>
      </div>
    </main>

    <!-- Overlay -->
    <div class="sidebar-overlay" :class="{ active: isSidebarOpen }" @click="isSidebarOpen = false"></div>

    <!-- Sidebar Form -->
    <aside id="add-account-sidebar" class="sidebar-form" :class="{ active: isSidebarOpen }">
        <div class="sidebar-header">
            <h3>Añadir Cuenta</h3>
            <button class="sidebar-close" @click="isSidebarOpen = false">&times;</button>
        </div>
        
        <form @submit.prevent="crearCuenta" class="sidebar-content">
            <div class="form-group">
                <label>Nombre de la Cuenta</label>
                <input v-model="nuevaCuenta.nombre" type="text" class="form-input" required placeholder="Ej. Billetera, Banco Principal">
            </div>

            <div class="form-group">
                <label>Tipo de Cuenta</label>
                <select v-model="nuevaCuenta.tipo" class="form-select">
                    <option value="EFECTIVO">Efectivo</option>
                    <option value="DEBITO">Débito</option>
                    <option value="CREDITO">Crédito</option>
                </select>
            </div>

            <div class="form-group">
                <label>Moneda</label>
                <select v-model="nuevaCuenta.moneda" class="form-select">
                    <option value="MXN">MXN - Peso Mexicano</option>
                    <option value="USD">USD - Dólar</option>
                    <option value="EUR">EUR - Euro</option>
                </select>
            </div>

            <!-- Campos específicos para Crédito -->
            <div v-if="nuevaCuenta.tipo === 'CREDITO'" class="credit-details-optional">
                <div class="form-group">
                    <label>Límite de Crédito</label>
                    <input v-model.number="nuevaCuenta.limiteCredito" type="number" class="form-input" step="0.01" placeholder="10000.00">
                </div>

                <div class="form-group">
                    <label>Día de Corte</label>
                    <input v-model.number="nuevaCuenta.diaCorte" type="number" class="form-input" min="1" max="31" placeholder="15">
                </div>

                <div class="form-group">
                    <label>Día de Pago</label>
                    <input v-model.number="nuevaCuenta.diaPago" type="number" class="form-input" min="1" max="31" placeholder="20">
                </div>
            </div>

            <button type="submit" class="button-primary" :disabled="isLoading">
                <span v-if="!isLoading">Crear Cuenta</span>
                <span v-else>Guardando...</span>
            </button>
        </form>
    </aside>
  </div>
</template>

<style scoped>
.accounts-grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 1.5rem;
    margin-top: 1.5rem;
}

@media (min-width: 768px) {
    .accounts-grid {
        grid-template-columns: repeat(2, 1fr);
    }
}

@media (min-width: 1024px) {
    .accounts-grid {
        grid-template-columns: repeat(3, 1fr);
    }
}
</style>