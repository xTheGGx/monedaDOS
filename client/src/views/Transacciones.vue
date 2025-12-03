<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { transaccionService, cuentaService, categoriaService } from '../services/api';
// Iconos
import { Trash2, Edit2, Plus, Filter, Search, Calendar, ChevronLeft, ChevronRight, X } from 'lucide-vue-next';

// --- Estado de Datos ---
const transactions = ref([]);
const cuentas = ref([]);
const categorias = ref([]);
const loading = ref(false);
const totalElements = ref(0);
const totalPages = ref(0);

// --- Filtros y Paginación ---
const filters = ref({
  page: 0,
  size: 10,
  tipo: '', // '' (Todos), 'INGRESO', 'EGRESO'
  mes: '',  // Formato YYYY-MM (Actualmente no usado en UI) 
  search: '' // Búsqueda local (el backend no tiene endpoint de texto libre aún)
});

// --- Estado del Formulario (Sidebar) ---
const isSidebarOpen = ref(false);
const isSubmitting = ref(false);
const sidebarError = ref('');
const form = ref({
  idTransaccion: null,
  descripcion: '',
  monto: '',
  tipoMovimiento: 'INGRESO', // Para controlar qué categorías mostrar
  categoriaId: '',
  cuentaId: ''
});

// --- Carga Inicial ---
onMounted(async () => {
  await Promise.all([
    fetchAuxData(),
    fetchTransactions()
  ]);
});

// Watchers para recargar al cambiar filtros
watch(() => filters.value.tipo, () => {
  filters.value.page = 0; // Reset a página 1
  fetchTransactions();
});

// --- Métodos de API ---

const fetchAuxData = async () => {
  try {
    const [resCuentas, resCats] = await Promise.all([
      cuentaService.getAll(),
      categoriaService.getAll()
    ]);
    cuentas.value = resCuentas.data;
    categorias.value = resCats.data;
  } catch (e) {
    console.error("Error cargando datos auxiliares", e);
  }
};

const fetchTransactions = async () => {
  loading.value = true;
  try {
    // Preparar params para Spring Boot
    const params = {
      page: filters.value.page,
      size: filters.value.size,
      tipo: filters.value.tipo || undefined, // Enviar undefined si está vacío
      mes: filters.value.mes || undefined
    };

    const { data } = await transaccionService.getAll(params);
    
    transactions.value = data.content;
    totalElements.value = data.totalElements;
    totalPages.value = data.totalPages;
    
  } catch (error) {
    console.error("Error cargando transacciones", error);
  } finally {
    loading.value = false;
  }
};

const handleSave = async () => {
  if(!form.value.monto || !form.value.cuentaId || !form.value.categoriaId) {
    sidebarError.value = "Por favor completa los campos obligatorios.";
    return;
  }

  isSubmitting.value = true;
  sidebarError.value = '';

  try {
    const payload = {
      cuentaId: form.value.cuentaId,
      categoriaId: form.value.categoriaId,
      monto: parseFloat(form.value.monto),
      descripcion: form.value.descripcion
    };

    if (form.value.idTransaccion) {
      // Editar
      await transaccionService.update(form.value.idTransaccion, payload);
    } else {
      // Crear
      await transaccionService.create(payload);
    }

    closeSidebar();
    fetchTransactions(); // Recargar lista
  } catch (e) {
    sidebarError.value = e.response?.data?.mensaje || "Error al guardar el movimiento.";
  } finally {
    isSubmitting.value = false;
  }
};

const handleDelete = async (id) => {
  if(!confirm("¿Estás seguro de eliminar este movimiento?")) return;
  try {
    await transaccionService.delete(id);
    fetchTransactions();
  } catch (e) {
    alert("Error al eliminar");
  }
};

// --- Lógica de UI ---

const openSidebar = (tx = null) => {
  sidebarError.value = '';
  if (tx) {
    // Modo Edición
    form.value = {
      idTransaccion: tx.idTransaccion,
      descripcion: tx.descripcion,
      monto: Math.abs(tx.monto), // Mostrar siempre positivo en el input
      // Detectar tipo basado en la categoría de la transacción original
      tipoMovimiento: tx.categoria.tipo, 
      categoriaId: tx.categoria.idCategoria,
      cuentaId: tx.cuenta.idCuenta
    };
  } else {
    // Modo Creación
    form.value = {
      idTransaccion: null,
      descripcion: '',
      monto: '',
      tipoMovimiento: 'INGRESO',
      categoriaId: '',
      cuentaId: ''
    };
  }
  isSidebarOpen.value = true;
};

const closeSidebar = () => {
  isSidebarOpen.value = false;
};

// Filtrado local para búsqueda de texto (ya que el backend filtra por IDs/Tipos)
const filteredTransactions = computed(() => {
  if (!filters.value.search) return transactions.value;
  const lowerSearch = filters.value.search.toLowerCase();
  return transactions.value.filter(t => 
    t.descripcion.toLowerCase().includes(lowerSearch) ||
    t.categoria.nombre.toLowerCase().includes(lowerSearch)
  );
});

// Totales (TODO: El backend debería dar un endpoint de resumen)
const totalIngresos = computed(() => transactions.value
  .filter(t => t.categoria.tipo === 'INGRESO')
  .reduce((sum, t) => sum + t.monto, 0)
);

const totalEgresos = computed(() => transactions.value
  .filter(t => t.categoria.tipo === 'EGRESO')
  .reduce((sum, t) => sum + Math.abs(t.monto), 0)
);

// Helpers de Formato
const formatDate = (dateStr) => {
  if(!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleDateString('es-MX', { day: '2-digit', month: 'short', year: 'numeric' });
};

const currentDate = new Date().toLocaleDateString('es-MX', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });

// Filtrar categorías en el select según el tipo seleccionado (Ingreso/Gasto)
const categoriasDisponibles = computed(() => {
  return categorias.value.filter(c => c.tipo === form.value.tipoMovimiento);
});

const getCategoryEmoji = (nombreCat) => {
  const map = { 'Comida': '🍔', 'Transporte': '🚌', 'Salario': '💰', 'Salud': '🏥', 'Casa': '🏠', 'Entretenimiento': '🎬' };
  return map[nombreCat] || '📄';
};
</script>

<template>
  <div class="min-h-screen pb-24 relative">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-dark-chocolate mb-2">💸 Movimientos</h1>
        <p class="text-brown-muted">Todos tus ingresos y gastos en un solo lugar</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div class="bg-oatmeal-paper rounded-3xl p-6 shadow-lg border border-cookie-dough/20">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-full bg-cookie-dough flex items-center justify-center text-white text-xl">📅</div>
            <div>
              <p class="text-sm text-brown-muted">Total de movimientos</p>
              <p class="text-2xl font-bold text-dark-chocolate">{{ filteredTransactions.length }}</p>
            </div>
          </div>
        </div>

        <div class="bg-[#81B29A] rounded-3xl p-6 shadow-lg text-white">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-full bg-white/20 flex items-center justify-center text-xl">↓</div>
            <div>
              <p class="text-sm opacity-90">Ingresos</p>
              <p class="text-2xl font-bold">+${{ totalIngresos.toFixed(2) }}</p>
            </div>
          </div>
        </div>

        <div class="bg-berry-jam rounded-3xl p-6 shadow-lg text-white">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-full bg-white/20 flex items-center justify-center text-xl">↑</div>
            <div>
              <p class="text-sm opacity-90">Gastos</p>
              <p class="text-2xl font-bold">-${{ totalEgresos.toFixed(2) }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-oatmeal-paper rounded-3xl p-6 shadow-md mb-8">
        <div class="flex flex-col md:flex-row gap-4 items-center">
          <div class="flex-1 relative w-full">
            <Search class="absolute left-4 top-1/2 -translate-y-1/2 text-brown-muted" :size="20" />
            <input 
              type="text" 
              placeholder="Buscar descripción..." 
              v-model="filters.search"
              class="w-full bg-white rounded-full py-3 pl-12 pr-4 text-dark-chocolate border-2 border-transparent focus:outline-none focus:border-cookie-dough transition-colors"
            />
          </div>

          <div class="flex bg-white rounded-full p-1 shadow-sm">
            <button 
              @click="filters.tipo = ''"
              class="px-4 py-2 rounded-full text-sm font-bold transition-all"
              :class="filters.tipo === '' ? 'bg-cookie-dough text-white shadow' : 'text-brown-muted hover:bg-gray-100'"
            >Todos</button>
            <button 
              @click="filters.tipo = 'INGRESO'"
              class="px-4 py-2 rounded-full text-sm font-bold transition-all"
              :class="filters.tipo === 'INGRESO' ? 'bg-[#81B29A] text-white shadow' : 'text-brown-muted hover:bg-gray-100'"
            >Ingresos</button>
            <button 
              @click="filters.tipo = 'EGRESO'"
              class="px-4 py-2 rounded-full text-sm font-bold transition-all"
              :class="filters.tipo === 'EGRESO' ? 'bg-berry-jam text-white shadow' : 'text-brown-muted hover:bg-gray-100'"
            >Gastos</button>
          </div>
        </div>
      </div>

      <div class="bg-oatmeal-paper rounded-3xl shadow-lg overflow-hidden relative">
        <div class="text-center py-6 px-6 border-b-2 border-dashed border-cookie-dough/50 bg-[#fffdfa]">
          <h2 class="text-xl font-bold text-dark-chocolate flex items-center justify-center gap-2">
            🧾 Ticket de Movimientos
          </h2>
          <p class="text-sm text-brown-muted mt-1 capitalize">{{ currentDate }}</p>
        </div>

        <div class="p-6 min-h-[300px]">
          <div v-if="loading" class="text-center py-12 text-brown-muted">
            <span class="animate-spin text-4xl block mb-2">🍪</span>
            Cargando movimientos...
          </div>

          <div v-else-if="filteredTransactions.length === 0" class="text-center py-12">
            <div class="text-6xl mb-4 grayscale opacity-50">🥐</div>
            <p class="text-dark-chocolate font-medium">No hay movimientos aquí</p>
            <p class="text-sm text-brown-muted">¡Empieza registrando uno nuevo!</p>
          </div>

          <div v-else class="space-y-3">
            <div 
              v-for="tx in filteredTransactions" 
              :key="tx.idTransaccion"
              class="flex items-center justify-between gap-4 p-4 bg-white rounded-2xl border border-transparent hover:border-cookie-dough/30 transition-all group shadow-sm"
            >
              <div class="flex items-center gap-4 flex-1">
                <div 
                  class="w-12 h-12 rounded-full flex items-center justify-center text-2xl shrink-0"
                  :class="tx.categoria.tipo === 'INGRESO' ? 'bg-[#81B29A]/20' : 'bg-berry-jam/20'"
                >
                  {{ getCategoryEmoji(tx.categoria.nombre) }}
                </div>
                
                <div class="min-w-0">
                  <p class="text-dark-chocolate font-bold truncate">{{ tx.descripcion }}</p>
                  <div class="flex items-center gap-2 text-xs mt-1">
                    <span 
                      class="px-2 py-0.5 rounded-full font-medium"
                      :style="{ backgroundColor: tx.categoria.color + '20', color: tx.categoria.color }"
                    >
                      {{ tx.categoria.nombre }}
                    </span>
                    <span class="text-brown-muted">• {{ tx.cuenta.nombre }}</span>
                    <span class="text-brown-muted hidden sm:inline">• {{ formatDate(tx.fecha) }}</span>
                  </div>
                </div>
              </div>

              <div class="flex items-center gap-4">
                <div class="text-right">
                  <p 
                    class="text-lg font-bold whitespace-nowrap"
                    :class="tx.categoria.tipo === 'INGRESO' ? 'text-[#81B29A]' : 'text-berry-jam'"
                  >
                    {{ tx.categoria.tipo === 'INGRESO' ? '+' : '' }}{{ tx.monto.toLocaleString('es-MX', { style: 'currency', currency: 'MXN' }) }}
                  </p>
                </div>

                <div class="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button @click="openSidebar(tx)" class="p-2 hover:bg-cookie-dough/20 rounded-full text-cookie-dough" title="Editar">
                    <Edit2 :size="18" />
                  </button>
                  <button @click="handleDelete(tx.idTransaccion)" class="p-2 hover:bg-red-100 rounded-full text-berry-jam" title="Eliminar">
                    <Trash2 :size="18" />
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="totalPages > 1" class="p-4 border-t-2 border-dashed border-cookie-dough/50 flex justify-between items-center bg-[#fffdfa]">
          <button 
            @click="filters.page--" 
            :disabled="filters.page === 0"
            class="flex items-center gap-1 text-sm font-bold text-brown-muted disabled:opacity-30 hover:text-cookie-dough"
          >
            <ChevronLeft :size="16" /> Anterior
          </button>
          
          <span class="text-xs font-medium text-brown-muted">
            Página {{ filters.page + 1 }} de {{ totalPages }}
          </span>

          <button 
            @click="filters.page++" 
            :disabled="filters.page >= totalPages - 1"
            class="flex items-center gap-1 text-sm font-bold text-brown-muted disabled:opacity-30 hover:text-cookie-dough"
          >
            Siguiente <ChevronRight :size="16" /> 
          </button>
        </div>

        <div class="h-4 bg-oatmeal-paper ticket-torn-bottom"></div>
      </div>
    </div>

    <button
      @click="openSidebar()"
      class="fixed bottom-8 right-8 w-16 h-16 bg-cookie-dough hover:bg-cookie-hover text-white rounded-full shadow-2xl flex items-center justify-center transition-all hover:scale-110 z-40"
    >
      <Plus :size="32" stroke-width="3" />
    </button>

    <div 
      class="fixed inset-0 bg-black/40 z-50 transition-opacity" 
      v-if="isSidebarOpen"
      @click="closeSidebar"
    ></div>

    <aside 
      class="fixed top-0 right-0 h-full w-full max-w-md bg-milk-cream z-50 shadow-2xl transition-transform duration-300 transform flex flex-col"
      :class="isSidebarOpen ? 'translate-x-0' : 'translate-x-full'"
    >
      <div class="p-6 border-b border-cookie-dough/20 flex justify-between items-center bg-white">
        <h3 class="text-xl font-bold text-dark-chocolate">
          {{ form.idTransaccion ? '✏️ Editar Movimiento' : '📝 Nuevo Movimiento' }}
        </h3>
        <button @click="closeSidebar" class="text-brown-muted hover:text-dark-chocolate">
          <X :size="24" />
        </button>
      </div>

      <div class="p-6 flex-1 overflow-y-auto">
        <form @submit.prevent="handleSave" class="space-y-6">
          
          <div v-if="sidebarError" class="p-3 bg-red-50 text-berry-jam text-sm rounded-xl border border-red-100">
            {{ sidebarError }}
          </div>

          <div class="grid grid-cols-2 gap-3 p-1 bg-oatmeal-paper rounded-xl">
            <button 
              type="button"
              @click="form.tipoMovimiento = 'INGRESO'; form.categoriaId = ''"
              class="py-3 rounded-lg text-sm font-bold transition-all flex items-center justify-center gap-2"
              :class="form.tipoMovimiento === 'INGRESO' ? 'bg-white shadow text-[#81B29A]' : 'text-brown-muted'"
            >
              Ingreso
            </button>
            <button 
              type="button"
              @click="form.tipoMovimiento = 'EGRESO'; form.categoriaId = ''"
              class="py-3 rounded-lg text-sm font-bold transition-all flex items-center justify-center gap-2"
              :class="form.tipoMovimiento === 'EGRESO' ? 'bg-white shadow text-berry-jam' : 'text-brown-muted'"
            >
              Gasto
            </button>
          </div>

          <div>
            <label class="block text-sm font-bold text-brown-muted mb-2">Monto</label>
            <div class="relative">
              <span class="absolute left-4 top-1/2 -translate-y-1/2 text-dark-chocolate font-bold">$</span>
              <input 
                v-model="form.monto" 
                type="number" 
                step="0.01" 
                placeholder="0.00"
                class="w-full bg-white rounded-xl py-4 pl-10 pr-4 text-xl font-bold text-dark-chocolate border-2 border-transparent focus:border-cookie-dough focus:outline-none shadow-inner"
                required
              />
            </div>
          </div>

          <div>
            <label class="block text-sm font-bold text-brown-muted mb-2">Descripción</label>
            <input 
              v-model="form.descripcion" 
              type="text" 
              placeholder="Ej: Tacos al pastor"
              class="w-full bg-white rounded-xl py-3 px-4 border border-cookie-dough/20 focus:border-cookie-dough focus:outline-none"
              maxlength="255"
            />
          </div>

          <div>
            <label class="block text-sm font-bold text-brown-muted mb-2">Categoría</label>
            <div class="grid grid-cols-3 gap-2">
              <div 
                v-for="cat in categoriasDisponibles" 
                :key="cat.idCategoria"
                @click="form.categoriaId = cat.idCategoria"
                class="cursor-pointer p-2 rounded-xl border-2 text-center transition-all flex flex-col items-center gap-1"
                :class="form.categoriaId === cat.idCategoria ? 'border-cookie-dough bg-cookie-dough/10' : 'border-transparent bg-white hover:bg-gray-50'"
              >
                <div class="text-xl">{{ getCategoryEmoji(cat.nombre) }}</div>
                <span class="text-xs font-medium truncate w-full">{{ cat.nombre }}</span>
              </div>
            </div>
            <p v-if="categoriasDisponibles.length === 0" class="text-xs text-center text-brown-muted mt-2">
              No hay categorías de este tipo.
            </p>
          </div>

          <div>
            <label class="block text-sm font-bold text-brown-muted mb-2">Cuenta</label>
            <select 
              v-model="form.cuentaId"
              class="w-full bg-white rounded-xl py-3 px-4 border border-cookie-dough/20 focus:border-cookie-dough focus:outline-none appearance-none"
              required
            >
              <option value="" disabled>Selecciona una cuenta</option>
              <option v-for="cta in cuentas" :key="cta.idCuenta" :value="cta.idCuenta">
                {{ cta.nombre }} ({{ cta.moneda }})
              </option>
            </select>
          </div>

        </form>
      </div>

      <div class="p-6 bg-white border-t border-cookie-dough/10">
        <button 
          @click="handleSave" 
          :disabled="isSubmitting"
          class="w-full bg-cookie-dough hover:bg-cookie-hover text-white font-bold py-4 rounded-xl shadow-lg transform active:scale-95 transition-all disabled:opacity-50"
        >
          {{ isSubmitting ? 'Guardando...' : 'Guardar Movimiento' }}
        </button>
      </div>
    </aside>

  </div>
</template>

<style scoped>
/* Estilo decorativo del borde roto (ticket) */
.ticket-torn-bottom {
  background-image: radial-gradient(circle at 10px -5px, transparent 12px, #F3E5D8 13px);
  background-size: 20px 20px;
  background-repeat: repeat-x;
  transform: rotate(180deg);
}

/* Ocultar scrollbar del sidebar pero permitir scroll */
aside .overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}
aside .overflow-y-auto::-webkit-scrollbar-thumb {
  background-color: rgba(0,0,0,0.1);
  border-radius: 3px;
}
</style>