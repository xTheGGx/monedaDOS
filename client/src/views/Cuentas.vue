<script setup>
import { ref, onMounted, computed } from 'vue';
import api from '../services/api';
import Navbar from '../components/Navbar.vue';

// --- Estado ---
const cuentas = ref([]);
const saldoTotal = ref(0);
const isSidebarOpen = ref(false);

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
        // Calcular saldo total en frontend (o usar el endpoint /balance-total)
        saldoTotal.value = cuentas.value.reduce((sum, c) => sum + c.saldo, 0);
    } catch (e) {
        console.error("Error cargando cuentas", e);
    }
};

const crearCuenta = async () => {
    try {
        await api.post('/cuentas', nuevaCuenta.value);
        isSidebarOpen.value = false; // Cerrar sidebar
        cargarCuentas(); // Recargar lista
        // Reset form (opcional)
        nuevaCuenta.value = { nombre: '', tipo: 'EFECTIVO', moneda: 'MXN' };
    } catch (e) {
        alert("Error al crear cuenta: " + (e.response?.data?.mensaje || e.message));
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
    
    <main class="container main-content">
        <div class="main-headline">
            <div>
                <h1>Gestión de Cuentas</h1>
                <p class="subtitle">Tus cuentas organizadas.</p>
            </div>
            <div class="totals">
                <span class="kpi-label">Saldo Total</span>
                <strong>${{ saldoTotal.toFixed(2) }}</strong>
            </div>
        </div>

        <div class="accounts-grid">
            <div v-for="c in cuentas" :key="c.idCuenta" class="card account-card">
                <div class="account-card-header">
                    <h3>{{ c.nombre }}</h3>
                    <span class="badge-faint">{{ c.tipo }}</span>
                </div>
                <p class="account-bank">{{ c.moneda }}</p>
                <p class="account-balance" :class="{ 'text-expense': c.saldo < 0 }">
                    ${{ c.saldo.toFixed(2) }}
                </p>
                
                <div class="account-card-footer">
                    <button class="button-link">Ver movimientos</button>
                </div>
            </div>

            <div class="card account-card-add" @click="isSidebarOpen = true">
                <span class="material-symbols-outlined">add_circle</span>
                <strong>Agregar Nueva Cuenta</strong>
            </div>
        </div>
    </main>

    <div class="sidebar-overlay" :class="{ active: isSidebarOpen }" @click="isSidebarOpen = false"></div>

    <aside class="sidebar-form" :class="{ active: isSidebarOpen }">
        <div class="sidebar-header">
            <h3>Añadir Cuenta</h3>
            <button class="sidebar-close" @click="isSidebarOpen = false">&times;</button>
        </div>
        
        <div class="sidebar-content">
            <form @submit.prevent="crearCuenta" class="form-grid">
                <div class="form-field">
                    <label>Nombre</label>
                    <input v-model="nuevaCuenta.nombre" type="text" required placeholder="Ej. Billetera">
                </div>
                <div class="form-field">
                    <label>Tipo</label>
                    <select v-model="nuevaCuenta.tipo">
                        <option value="EFECTIVO">Efectivo</option>
                        <option value="DEBITO">Débito</option>
                        <option value="CREDITO">Crédito</option>
                    </select>
                </div>
                <div v-if="nuevaCuenta.tipo === 'CREDITO'" class="form-field">
                    <label>Límite de Crédito</label>
                    <input v-model="nuevaCuenta.limiteCredito" type="number" step="0.01">
                </div>

                <button type="submit" class="button-primary" style="margin-top: 1rem; width: 100%;">Guardar</button>
            </form>
        </div>
    </aside>
  </div>
</template>