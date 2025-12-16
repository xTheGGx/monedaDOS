<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

// --- Estado ---
const cuentas = ref([]);
const saldoTotal = ref(0);
const isSidebarOpen = ref(false);
const isLoading = ref(false);
const errorMsg = ref(''); // Error global de la página
const sidebarError = ref(''); // Error específico del formulario lateral

// Estado inicial constante para resetear fácil
const initialState = {
    nombre: '',
    tipo: 'EFECTIVO',
    divisaId: '',
    saldoInicial: 0,
    diaCorte: null,
    diaPago: null,
    limiteCredito: null
};

// Formulario reactivo 
const nuevaCuenta = ref({ ...initialState });

// --- API Calls ---
const cargarCuentas = async () => {
    isLoading.value = true;
    errorMsg.value = '';
    try {
        const res = await api.get('/cuentas');
        cuentas.value = res.data;
        saldoTotal.value = cuentas.value.reduce((sum, c) => sum + safeNumber(c.saldo), 0);
    } catch (e) {
        console.error("Error cargando cuentas", e);
        errorMsg.value = 'No se pudieron cargar las cuentas';
    } finally {
        isLoading.value = false;
    }
};

const divisas = ref([]);

const cargarDivisas = async () => {
    try {
        const res = await api.get('/divisas');
        divisas.value = res.data;
    } catch (e) {
        console.error('Error cargando divisas', e);
    }
};

onMounted(() => {
    cargarDivisas();
    cargarCuentas();
});


const crearCuenta = async () => {
    // Limpiar error previo del sidebar
    sidebarError.value = '';

    if (!nuevaCuenta.value.nombre.trim()) {
        sidebarError.value = 'El nombre de la cuenta es obligatorio';
        return;
    }

    //El nombre de la cuenta debe ser unico
    if (cuentas.value.some(c => c.nombre.toLowerCase() === nuevaCuenta.value.nombre.trim().toLowerCase())) {
        sidebarError.value = 'Ya existe una cuenta con ese nombre';
        return;
    }

    if (!nuevaCuenta.value.divisaId) {
        sidebarError.value = 'La divisa es obligatoria';
        return;
    }

    isLoading.value = true;
    try {
        await api.post('/cuentas', nuevaCuenta.value);

        // Éxito: Cerrar y recargar
        isSidebarOpen.value = false;
        await cargarCuentas();

        // Reset completo del formulario usando la copia del estado inicial
        nuevaCuenta.value = { ...initialState };

    } catch (e) {
        // Mostrar error EN EL SIDEBAR
        const msg = e?.response?.data?.message || e?.response?.data?.mensaje;

        if (e?.response?.status === 409) {
            sidebarError.value = msg || 'Ya existe una cuenta con ese nombre';
        } else {
            sidebarError.value = msg || e.message || 'Error al crear la cuenta';
        }
    } finally {
        isLoading.value = false;
    }
};

const safeNumber = (v) => {
  const n = Number(v);
  return Number.isFinite(n) ? n : 0;
};

const money = (v) => safeNumber(v).toFixed(2);

</script>

<template>
    <div class="page-wrapper">

        <main class="site-main">
            <div class="container main-content">
                <div class="main-headline">
                    <div>
                        <h1>Gestión de Cuentas</h1>
                        <p class="subtitle">Tus cuentas organizadas.</p>
                    </div>
                    <div style="text-align: right;">
                        <span style="display: block; font-size: 0.875rem; color: var(--text-muted);">Saldo Total</span>
                        <strong style="font-size: 1.5rem; font-weight: 700;">${{ money(saldoTotal) }}</strong>
                    </div>
                </div>

                <div v-if="isLoading && !isSidebarOpen" style="text-align: center; padding: 2rem;">
                    <p>Cargando...</p>
                </div>

                <div v-else-if="errorMsg" class="login-error" style="margin-bottom: 1rem;">
                    <span class="material-symbols-outlined">warning</span>
                    <span>{{ errorMsg }}</span>
                </div>

                <div v-else class="accounts-grid">
                    <div v-for="c in cuentas" :key="c.id" class="card account-card">
                        <div class="account-card-header">
                            <h3>{{ c.nombre }}</h3>
                            <span class="badge" :class="c.tipo === 'CREDITO' ? 'badge-yellow' : 'badge-blue'">
                                {{ c.tipo }}
                            </span>
                        </div>

                        <p class="account-bank">{{ c.divisaCodigo }}</p>
                        <p class="account-balance"
   :class="{ 'text-expense': safeNumber(c.saldo) < 0, 'text-income': safeNumber(c.saldo) >= 0 }">
  ${{ money(c.saldo) }}
</p>


                        <div v-if="c.tipo === 'CREDITO' && c.limiteCredito" class="account-credit-details">
                            <div>
                                <span>Límite:</span>
                                <strong>${{ money(c.limiteCredito) }}</strong>
                            </div>
                        </div>
                    </div>

                    <div class="card account-card-add" @click="isSidebarOpen = true">
                        <span class="material-symbols-outlined">add_circle</span>
                        <strong>Agregar Nueva Cuenta</strong>
                    </div>
                </div>
            </div>
        </main>

        <div class="sidebar-overlay" :class="{ active: isSidebarOpen }" @click="isSidebarOpen = false"></div>

        <aside id="add-account-sidebar" class="sidebar-form" :class="{ active: isSidebarOpen }">
            <div class="sidebar-header">
                <h3>Añadir Cuenta</h3>
                <button class="sidebar-close" @click="isSidebarOpen = false">&times;</button>
            </div>

            <form @submit.prevent="crearCuenta" class="sidebar-content">

                <div v-if="sidebarError" class="login-error" style="margin-bottom: 1rem; font-size: 0.9rem;">
                    <span class="material-symbols-outlined" style="font-size: 1.2rem;">error</span>
                    <span>{{ sidebarError }}</span>
                </div>

                <div class="form-group">
                    <label>Nombre de la Cuenta</label>
                    <input v-model="nuevaCuenta.nombre" type="text" class="form-input" required
                        placeholder="Ej. Billetera">
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
                    <select v-model="nuevaCuenta.divisaId" class="form-select" required>
                        <option disabled value="">Selecciona una divisa</option>
                        <option v-for="d in divisas" :key="d.idDivisa" :value="d.idDivisa">
                            {{ d.codigoDivisa }} - {{ d.nombreDivisa }}
                        </option>
                    </select>
                </div>

                <div v-if="nuevaCuenta.tipo === 'CREDITO'" class="credit-details-optional">
                    <div class="form-group">
                        <label>Límite de Crédito</label>
                        <input v-model.number="nuevaCuenta.limiteCredito" type="number" class="form-input" step="0.01">
                    </div>
                    <div class="form-group">
                        <label>Día de Corte</label>
                        <input v-model.number="nuevaCuenta.diaCorte" type="number" class="form-input" min="1" max="31">
                    </div>
                    <div class="form-group">
                        <label>Día de Pago</label>
                        <input v-model.number="nuevaCuenta.diaPago" type="number" class="form-input" min="1" max="31">
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