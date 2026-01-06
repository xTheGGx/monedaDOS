<script setup>
import { ref, computed, onMounted, watch } from "vue";
import { cuentaService, divisaService } from "../services/api";

// ---------- Estado ----------
const cuentas = ref([]);
const divisas = ref([]);

const showBalances = ref(true);
const showAddModal = ref(false);

const isLoading = ref(false);
const isSaving = ref(false);

const errorMsg = ref("");
const formError = ref("");

// ---------- Edición de cuenta ----------
const showUpdateModal = ref(false);
const editingId = ref(null);
const editingCuenta = ref({
  nombre: "",
  tipo: "EFECTIVO",
  divisaId: "",
  limiteCredito: null,
  diaCorte: null,
  diaPago: null,
});
// cierra ambos modales
const cerrarModal = () => {
  showAddModal.value = false;
  showUpdateModal.value = false;
};

// abre modal de edición 
const abrirUpdateModal = (c) => {
  formError.value = "";
  editingId.value = c.idCuenta ?? c.id;

  editingCuenta.value = {
    nombre: c?.nombre ?? "",
    tipo: c?.tipo ?? "EFECTIVO",
    divisaId: String(
      c?.divisaId ??
      c?.divisa?.idDivisa ??
      c?.divisa?.id ??
      c?.idDivisa ??
      ""
    ),
    limiteCredito: c?.limiteCredito ?? null,
    diaCorte: c?.diaCorte ?? null,
    diaPago: c?.diaPago ?? null,
  };

  showUpdateModal.value = true;
};


// Form inicial
const initialForm = {
  nombre: "",
  tipo: "EFECTIVO",
  saldo: 0,
  limiteCredito: null,
  diaCorte: null,
  diaPago: null,
  divisaId: "", // obligatorio
};
const nuevaCuenta = ref({ ...initialForm });

// ---------- Helpers ----------
const safeNumber = (v) => {
  const n = Number(v);
  return Number.isFinite(n) ? n : 0;
};

const money = (v) => safeNumber(v).toFixed(2);

const moneySigned = (v) => {
  const n = safeNumber(v);
  const abs = Math.abs(n).toFixed(2);
  return n < 0 ? `-${abs}` : abs;
};

const divisaCode = (c) => {
  // soporte a varias formas de DTO:
  return (
    c?.divisa?.codigoDivisa ||
    c?.divisa?.codigo ||
    c?.codigoDivisa ||
    c?.divisaCodigo ||
    ""
  );
};

const emojiPorTipo = {
  EFECTIVO: "💵",
  DEBITO: "🏦",
  BANCARIA: "🏦",
  AHORRO: "🐷",
  INVERSION: "📈",
  CREDITO: "💳",
};

const colorPorTipo = {
  EFECTIVO: "#81B29A",
  DEBITO: "#D4A373",
  BANCARIA: "#D4A373",
  AHORRO: "#E9B05D",
  INVERSION: "#5DADE2",
  CREDITO: "#E07A5F",
};

const emojiCuenta = (c) => c?.emoji || c?.icono || emojiPorTipo[c?.tipo] || "💰";
const colorCuenta = (c) => colorPorTipo[c?.tipo] || "#D4A373";

// ---------- Computed ----------
const totalBalance = computed(() => cuentas.value.reduce((s, a) => s + safeNumber(a.saldo), 0));
const totalAssets = computed(() => cuentas.value.filter(a => safeNumber(a.saldo) > 0).reduce((s, a) => s + safeNumber(a.saldo), 0));
const totalLiabilities = computed(() => Math.abs(cuentas.value.filter(a => safeNumber(a.saldo) < 0).reduce((s, a) => s + safeNumber(a.saldo), 0)));

// ---------- API ----------
const cargarDivisas = async () => {
  const res = await divisaService.getAll();
  divisas.value = res.data || [];
};

const cargarCuentas = async () => {
  isLoading.value = true;
  errorMsg.value = "";
  try {
    const res = await cuentaService.getAll();
    cuentas.value = res.data || [];
  } catch (e) {
    console.error("Error cargando cuentas", e);
    errorMsg.value = "No se pudieron cargar las cuentas.";
  } finally {
    isLoading.value = false;
  }
};

const abrirModal = () => {
  formError.value = "";
  nuevaCuenta.value = { ...initialForm };
  showAddModal.value = true;
};


const crearCuenta = async () => {
  formError.value = "";

  // validación negocio: divisa obligatoria
  if (!nuevaCuenta.value.divisaId) {
    formError.value = "Selecciona una divisa.";
    return;
  }

  if (!nuevaCuenta.value.nombre.trim()) {
    formError.value = "El nombre es obligatorio.";
    return;
  }

  isSaving.value = true;

  try {

    const payload = {
      nombre: nuevaCuenta.value.nombre.trim(),
      tipo: nuevaCuenta.value.tipo,
      saldo: safeNumber(nuevaCuenta.value.saldo),
      limiteCredito: nuevaCuenta.value.tipo === "CREDITO" ? safeNumber(nuevaCuenta.value.limiteCredito) : null,
      diaCorte: nuevaCuenta.value.tipo === "CREDITO" ? nuevaCuenta.value.diaCorte : null,
      diaPago: nuevaCuenta.value.tipo === "CREDITO" ? nuevaCuenta.value.diaPago : null,
      divisaId: Number(nuevaCuenta.value.divisaId),
    };

    await cuentaService.create(payload);

    cerrarModal();
    await cargarCuentas();
  } catch (e) {
    console.error("Error creando cuenta", e);

    const status = e?.response?.status;
    const msg = e?.response?.data?.message || e?.response?.data?.mensaje;

    if (status === 409) {
      formError.value = msg || "Ya existe una cuenta con ese nombre.";
    } else {
      formError.value = msg || "No se pudo crear la cuenta.";
    }
  } finally {
    isSaving.value = false;
  }
};

const handleDelete = async (id) => {
  // Mensaje emergente de confirmación
  if (!confirm(`¿Estás seguro de que deseas eliminar la cuenta "${cuentas.value.find(c => (c.idCuenta ?? c.id) === id)?.nombre}"? Esta acción no se puede deshacer.`)) {
    return;
  }

  try {
    await cuentaService.delete(id);
    await cargarCuentas();
  } catch (e) {
    console.error("Error eliminando cuenta", e);
    //errorMsg.value = e?.response?.data?.message || "No se pudo eliminar la cuenta.";
  }
};


const handleUpdate = async () => {
  formError.value = "";

  if (!editingId.value) {
    formError.value = "No se pudo identificar la cuenta a actualizar.";
    return;
  }

  if (!editingCuenta.value.nombre.trim()) {
    formError.value = "El nombre es obligatorio.";
    return;
  }

  if (!editingCuenta.value.divisaId) {
    formError.value = "Selecciona una divisa.";
    return;
  }

  const esCredito = editingCuenta.value.tipo === "CREDITO";

  if (esCredito) {
    if (editingCuenta.value.limiteCredito === null || editingCuenta.value.limiteCredito === "") {
      formError.value = "Si es crédito, el límite de crédito es obligatorio.";
      return;
    }
    if (!editingCuenta.value.diaCorte) {
      formError.value = "Si es crédito, el día de corte es obligatorio.";
      return;
    }
    if (!editingCuenta.value.diaPago) {
      formError.value = "Si es crédito, el día de pago es obligatorio.";
      return;
    }
  }

  isSaving.value = true;

  try {
    const payload = {
      nombre: editingCuenta.value.nombre.trim(),
      tipo: editingCuenta.value.tipo,
      divisaId: Number(editingCuenta.value.divisaId),
      limiteCredito: esCredito ? safeNumber(editingCuenta.value.limiteCredito) : null,
      diaCorte: esCredito ? Number(editingCuenta.value.diaCorte) : null,
      diaPago: esCredito ? Number(editingCuenta.value.diaPago) : null,
    };

    await cuentaService.update(editingId.value, payload);

    showUpdateModal.value = false;
    await cargarCuentas();
  } catch (e) {
    console.error("Error actualizando cuenta", e);
    const msg = e?.response?.data?.message || e?.response?.data?.mensaje;
    formError.value = msg || "No se pudo actualizar la cuenta.";
  } finally {
    isSaving.value = false;
  }
};


onMounted(async () => {
  await Promise.all([cargarDivisas(), cargarCuentas()]);
});

watch(
  () => editingCuenta.value.tipo,
  (nuevoTipo) => {
    if (nuevoTipo !== "CREDITO") {
      editingCuenta.value.limiteCredito = null;
      editingCuenta.value.diaCorte = null;
      editingCuenta.value.diaPago = null;
    }
  }
);


</script>

<template>
  <div class="page">
    <div class="container">
      <!-- Header -->
      <div class="header">
        <div>
          <h1 class="title">💰 Mis Cuentas</h1>
          <p class="subtitle">Administra tus cuentas y saldos</p>
        </div>
      </div>

      <!-- Loading / Error -->
      <div v-if="isLoading" class="state">Cargando...</div>
      <div v-else-if="errorMsg" class="error">{{ errorMsg }}</div>

      <template v-else>
        <!-- Total card -->
        <div class="total-card">
          <div class="total-top">
            <div>
              <p class="total-label">Balance Total Neto</p>
              <h2 class="total-amount">
                <span v-if="showBalances">${{ money(totalBalance) }}</span>
                <span v-else>••••••</span>
              </h2>
            </div>

            <button class="icon-btn" @click="showBalances = !showBalances"
              :title="showBalances ? 'Ocultar' : 'Mostrar'">
              <span class="material-symbols-outlined">{{ showBalances ? "visibility" : "visibility_off" }}</span>
            </button>
          </div>

          <div class="totals-row">
            <div class="mini">
              <p class="mini-label">Activos</p>
              <p class="mini-value">
                <span v-if="showBalances">${{ money(totalAssets) }}</span>
                <span v-else>••••</span>
              </p>
            </div>

            <div class="mini">
              <p class="mini-label">Pasivos</p>
              <p class="mini-value">
                <span v-if="showBalances">${{ money(totalLiabilities) }}</span>
                <span v-else>••••</span>
              </p>
            </div>
          </div>
        </div>

        <!-- Accounts -->
        <div class="section-head">
          <h3 class="section-title">🏦 Todas las Cuentas</h3>
        </div>

        <div class="grid">
          <div v-for="c in cuentas" :key="c.idCuenta ?? c.id" class="card">
            <div class="card-top">
              <div class="left">
                <div class="emoji" :style="{ backgroundColor: `${colorCuenta(c)}22` }">
                  {{ emojiCuenta(c) }}
                </div>
                <div>
                  <h4 class="card-title">{{ c.nombre }}</h4>
                  <p class="card-sub">
                    {{ c.tipo }}
                    <span v-if="divisaCode(c)" class="dot">•</span>
                    <span v-if="divisaCode(c)">{{ divisaCode(c) }}</span>
                  </p>
                </div>
              </div>

              <div class="actions">
                <button class="act edit" title="Editar" @click="abrirUpdateModal(c)">
                  <span class="material-symbols-outlined">edit</span>
                </button>
                <button class="act del" title="Eliminar" @click="handleDelete(c.idCuenta ?? c.id)">
                  <span class="material-symbols-outlined">delete</span>
                </button>
              </div>
            </div>

            <div class="balance-box">
              <p class="balance-label">Saldo Actual</p>

              <p v-if="showBalances" class="balance" :class="safeNumber(c.saldo) >= 0 ? 'pos' : 'neg'">
                ${{ moneySigned(c.saldo) }}
              </p>
              <p v-else class="balance hidden">••••••</p>
            </div>

            <p v-if="c.tipo === 'CREDITO' && c.limiteCredito" class="desc">
              Límite: <strong>${{ money(c.limiteCredito) }}</strong>
            </p>
            <p v-else class="desc">Lista para usar ✨</p>

            <div class="quick">
              <button class="btn primary">Ver Movimientos</button>
              <button class="btn ghost">Transferir</button>
            </div>
          </div>

          <!-- Add card -->
          <button class="add-card" @click="abrirModal">
            <div class="add-icon">
              <span class="material-symbols-outlined">add</span>
            </div>
            <div>
              <p class="add-title">Agregar Nueva Cuenta</p>
              <p class="add-sub">Banco, efectivo, tarjeta o ahorro</p>
            </div>
          </button>
        </div>

        <!-- Tip -->
        <div class="tip">
          <div class="tip-emoji">💡</div>
          <div>
            <h3 class="tip-title">Consejo del Chef</h3>
            <p class="tip-text">
              Mantén tu efectivo separado del dinero del banco para tener mejor control.
              Es como tener frascos distintos en tu cocina, cada uno con su propósito.
            </p>
          </div>
        </div>
      </template>
    </div>

    <div v-if="showUpdateModal" class="modal-overlay" @click.self="cerrarModal">
      <div class="modal">
        <h2 class="modal-title">Editar Cuenta</h2>

        <div v-if="formError" class="error" style="margin-bottom: 12px;">{{ formError }}</div>

        <form @submit.prevent="handleUpdate" class="form">

          <div class="fg">
            <label>Nombre</label>
            <input v-model="editingCuenta.nombre" type="text" placeholder="Ej: Banco principal" />
          </div>

          <div class="fg">
            <label>Tipo</label>
            <select v-model="editingCuenta.tipo">
              <option value="EFECTIVO">Efectivo</option>
              <option value="DEBITO">Cuenta Bancaria</option>
              <option value="AHORRO">Ahorro</option>
              <option value="INVERSION">Inversión</option>
              <option value="CREDITO">Tarjeta de Crédito</option>
            </select>
          </div>

          <div class="fg">
            <label>Divisa</label>
            <select v-model="editingCuenta.divisaId" required>
              <option disabled value="">Selecciona una divisa</option>
              <option v-for="d in divisas" :key="d.idDivisa" :value="String(d.idDivisa)">
                {{ d.codigoDivisa }} - {{ d.nombreDivisa }}
              </option>
            </select>
          </div>
          <div v-if="editingCuenta.tipo === 'CREDITO'" class="row">
            <div class="fg">
              <label>Límite crédito</label>
              <input v-model.number="editingCuenta.limiteCredito" type="number" step="0.01" placeholder="0.00" />
            </div>

            <div class="fg">
              <label>Día corte</label>
              <input v-model.number="editingCuenta.diaCorte" type="number" min="1" max="31" />
            </div>

            <div class="fg">
              <label>Día pago</label>
              <input v-model.number="editingCuenta.diaPago" type="number" min="1" max="31" />
            </div>
          </div>


          <div class="modal-actions">
            <button type="button" class="btn ghost" @click="cerrarModal" :disabled="isSaving">Cancelar</button>
            <button type="submit" class="btn primary" :disabled="isSaving">
              <span v-if="!isSaving">Actualizar Cuenta</span>
              <span v-else>Guardando…</span>
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Modal Add -->
    <div v-if="showAddModal" class="modal-overlay" @click.self="cerrarModal">
      <div class="modal">
        <h2 class="modal-title">Agregar Nueva Cuenta</h2>

        <div v-if="formError" class="error" style="margin-bottom: 12px;">{{ formError }}</div>

        <form @submit.prevent="crearCuenta" class="form">
          <div class="fg">
            <label>Nombre</label>
            <input v-model="nuevaCuenta.nombre" type="text" placeholder="Ej: Banco principal" />
          </div>

          <div class="fg">
            <label>Tipo</label>
            <select v-model="nuevaCuenta.tipo">
              <option value="EFECTIVO">Efectivo</option>
              <option value="DEBITO">Cuenta Bancaria</option>
              <option value="AHORRO">Ahorro</option>
              <option value="INVERSION">Inversión</option>
              <option value="CREDITO">Tarjeta de Crédito</option>
            </select>
          </div>

          <div class="fg">
            <label>Divisa</label>
            <select v-model="nuevaCuenta.divisaId" required>
              <option disabled value="">Selecciona una divisa</option>
              <option v-for="d in divisas" :key="d.idDivisa" :value="String(d.idDivisa)">
                {{ d.codigoDivisa }} - {{ d.nombreDivisa }}
              </option>
            </select>
          </div>

          <div class="fg">
            <label>Saldo inicial</label>
            <input v-model.number="nuevaCuenta.saldo" type="number" step="0.01" placeholder="0.00" />
          </div>

          <div v-if="nuevaCuenta.tipo === 'CREDITO'" class="row">
            <div class="fg">
              <label>Límite crédito</label>
              <input v-model.number="nuevaCuenta.limiteCredito" type="number" step="0.01" placeholder="0.00" />
            </div>
            <div class="fg">
              <label>Día corte</label>
              <input v-model.number="nuevaCuenta.diaCorte" type="number" min="1" max="31" />
            </div>
            <div class="fg">
              <label>Día pago</label>
              <input v-model.number="nuevaCuenta.diaPago" type="number" min="1" max="31" />
            </div>
          </div>

          <div class="modal-actions">
            <button type="button" class="btn ghost" @click="cerrarModal" :disabled="isSaving">Cancelar</button>
            <button type="submit" class="btn primary" :disabled="isSaving">
              <span v-if="!isSaving">Crear Cuenta</span>
              <span v-else>Guardando…</span>
            </button>
          </div>
        </form>
      </div>
    </div>

  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  padding-bottom: 5rem;
  background: #fff8f0;
}

.container {
  max-width: 1120px;
  margin: 0 auto;
  padding: 32px 16px;
  color: #4a3b32;
}

/* Header */
.header {
  margin-bottom: 24px;
}

.title {
  font-size: 28px;
  margin: 0;
}

.subtitle {
  color: #8b6f47;
  margin-top: 6px;
}

/* States */
.state {
  text-align: center;
  padding: 24px;
  color: #8b6f47;
}

.error {
  background: #ffe6e1;
  border: 1px solid #e07a5f55;
  padding: 12px 14px;
  border-radius: 16px;
  color: #7c2c1b;
}

/* Total card */
.total-card {
  border-radius: 28px;
  padding: 28px;
  color: white;
  background: linear-gradient(135deg, #d4a373, #c89563);
  box-shadow: 0 18px 40px rgba(74, 59, 50, 0.18);
  margin-bottom: 26px;
}

.total-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.total-label {
  font-size: 12px;
  opacity: 0.9;
  margin: 0 0 8px 0;
}

.total-amount {
  font-size: 44px;
  margin: 0;
  font-weight: 700;
}

.icon-btn {
  width: 46px;
  height: 46px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.20);
  border: none;
  color: white;
  display: grid;
  place-items: center;
  cursor: pointer;
}

.icon-btn:hover {
  background: rgba(255, 255, 255, 0.28);
}

.totals-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-top: 18px;
}

.mini {
  background: rgba(255, 255, 255, 0.12);
  border-radius: 18px;
  padding: 14px;
}

.mini-label {
  font-size: 12px;
  opacity: 0.9;
  margin: 0 0 6px 0;
}

.mini-value {
  font-size: 22px;
  font-weight: 700;
  margin: 0;
}

/* Section */
.section-head {
  margin: 18px 0 10px;
}

.section-title {
  margin: 0;
  font-size: 18px;
}

/* Grid */
.grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
}

@media (min-width: 768px) {
  .grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.card {
  background: #f3e5d8;
  border-radius: 28px;
  padding: 18px;
  box-shadow: 0 10px 22px rgba(74, 59, 50, 0.10);
  transition: transform .15s ease, box-shadow .15s ease;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 26px rgba(74, 59, 50, 0.14);
}

.card-top {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}

.left {
  display: flex;
  gap: 12px;
  align-items: center;
}

.emoji {
  width: 56px;
  height: 56px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  font-size: 28px;
}

.card-title {
  margin: 0;
  font-size: 16px;
}

.card-sub {
  margin: 4px 0 0;
  font-size: 12px;
  color: #8b6f47;
}

.dot {
  margin: 0 6px;
  opacity: 0.8;
}

.actions {
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity .15s ease;
}

.card:hover .actions {
  opacity: 1;
}

.act {
  width: 38px;
  height: 38px;
  border-radius: 999px;
  border: none;
  cursor: pointer;
  display: grid;
  place-items: center;
  color: white;
}

.act.edit {
  background: #d4a373;
}

.act.edit:hover {
  background: #c89563;
}

.act.del {
  background: #e07a5f;
}

.act.del:hover {
  background: #d4685c;
}

.balance-box {
  background: white;
  border-radius: 18px;
  padding: 14px;
  margin-top: 12px;
}

.balance-label {
  margin: 0 0 6px;
  font-size: 12px;
  color: #8b6f47;
}

.balance {
  margin: 0;
  font-size: 26px;
  font-weight: 800;
}

.balance.hidden {
  color: #4a3b32;
}

.balance.pos {
  color: #81b29a;
}

.balance.neg {
  color: #e07a5f;
}

.desc {
  margin: 10px 0 0;
  font-size: 13px;
  color: #8b6f47;
}

.quick {
  display: flex;
  gap: 10px;
  margin-top: 14px;
}

.btn {
  flex: 1;
  padding: 10px 12px;
  border-radius: 999px;
  border: none;
  cursor: pointer;
  font-size: 13px;
  transition: background .15s ease;
}

.btn.primary {
  background: #d4a373;
  color: white;
}

.btn.primary:hover {
  background: #c89563;
}

.btn.ghost {
  background: white;
  color: #4a3b32;
}

.btn.ghost:hover {
  background: #e9d5c8;
}

/* Add card */
.add-card {
  border: none;
  cursor: pointer;
  background: #d4a373;
  color: white;
  border-radius: 28px;
  padding: 18px;
  display: flex;
  align-items: center;
  gap: 14px;
  box-shadow: 0 12px 26px rgba(74, 59, 50, 0.14);
}

.add-card:hover {
  background: #c89563;
}

.add-icon {
  width: 56px;
  height: 56px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.22);
  display: grid;
  place-items: center;
}

.add-title {
  margin: 0;
  font-weight: 800;
}

.add-sub {
  margin: 4px 0 0;
  opacity: 0.9;
  font-size: 13px;
}

/* Tip */
.tip {
  margin-top: 22px;
  background: #81b29a;
  color: white;
  border-radius: 28px;
  padding: 18px;
  display: flex;
  gap: 14px;
  box-shadow: 0 12px 26px rgba(74, 59, 50, 0.14);
}

.tip-emoji {
  font-size: 34px;
}

.tip-title {
  margin: 0 0 6px;
  font-size: 18px;
  font-weight: 800;
}

.tip-text {
  margin: 0;
  font-size: 13px;
  opacity: 0.95;
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(74, 59, 50, 0.45);
  backdrop-filter: blur(6px);
  display: grid;
  place-items: center;
  padding: 16px;
  z-index: 999;
}

.modal {
  width: 100%;
  max-width: 520px;
  background: #fff8f0;
  border-radius: 28px;
  padding: 22px;
  box-shadow: 0 22px 55px rgba(0, 0, 0, 0.25);
}

.modal-title {
  margin: 0 0 14px;
  text-align: center;
  font-size: 20px;
  font-weight: 800;
}

.form {
  display: grid;
  gap: 12px;
}

.fg label {
  display: block;
  font-size: 12px;
  color: #8b6f47;
  margin-bottom: 6px;
}

.fg input,
.fg select {
  width: 100%;
  border: 2px solid transparent;
  border-radius: 999px;
  padding: 10px 14px;
  background: #f3e5d8;
  color: #4a3b32;
  outline: none;
}

.fg input:focus,
.fg select:focus {
  border-color: #d4a373;
  background: #f6eadf;
}

.row {
  display: grid;
  gap: 12px;
}

@media (min-width: 720px) {
  .row {
    grid-template-columns: repeat(3, 1fr);
  }
}

.modal-actions {
  display: flex;
  gap: 10px;
  margin-top: 8px;
}
</style>
