<template>
  <div class="dashboard-page">
    <h1>¡Bienvenido a tu Dashboard! 🍪</h1>

    <BudgetBread 
      :budget="presupuesto" 
      :spent="totalGastado" 
    />

    <CategoryGrid 
      :categories="categorias.filter(c => c.tipo === 'EGRESO')"
      @select="openModal" 
    />

    <TransactionModal
      :isOpen="isModalOpen"
      :selectedCategory="selectedCategory ? selectedCategory.nombre : ''"
      :cuentas="cuentas"
      @close="isModalOpen = false"
      @submit="handleTransactionSubmit"
    />
  </div>
</template>

<script>
import { transaccionService, cuentaService, categoriaService } from '../services/api';
import BudgetBread from '../components/BudgetBread.vue';
import CategoryGrid from '../components/CategoryGrid.vue';
import TransactionModal from '../components/TransactionModal.vue';

export default {
  name: 'Dashboard',
  components: {
    BudgetBread,
    CategoryGrid,
    TransactionModal
  },
  data() {
    return {
      cuentas: [],
      categorias: [],
      transacciones: [],
      presupuesto: 1000, // Esto debería venir del backend
      isModalOpen: false,
      selectedCategory: null
    };
  },
  computed: {
    totalGastado() {
      return this.transacciones
        .filter(t => t.tipo === 'EGRESO')
        .reduce((sum, t) => sum + t.monto, 0);
    }
  },
  async mounted() {
    await this.loadData();
  },
  methods: {
    async loadData() {
      try {
        const [cuentasRes, categoriasRes, transaccionesRes] = await Promise.all([
          cuentaService.getAll(),
          categoriaService.getAll(),
          transaccionService.getAll()
        ]);
        
        // Asumiendo que las categorías tienen un campo 'tipo' (EGRESO/INGRESO)
        this.cuentas = cuentasRes.data;
        this.categorias = categoriasRes.data;
        this.transacciones = transaccionesRes.data;
      } catch (error) {
        console.error('Error al cargar datos:', error);
      }
    },
    openModal(category) {
      this.selectedCategory = category;
      // Necesitas asignar el ID de la categoría seleccionada al `formData` del modal antes de abrirlo.
      // Ya que `TransactionModal` tiene `categoriaId` en su `formData`.
      // En este caso, el componente `TransactionModal` recibe solo el nombre,
      // pero la lógica de la categoría se maneja principalmente en `Dashboard`
      // y se pasa el objeto completo o el ID en el `handleTransactionSubmit`.

      // Corregiremos la lógica de `openModal` para que use el nombre para el modal
      // y el objeto completo para la lógica interna.
      this.isModalOpen = true;
    },
    async handleTransactionSubmit(transactionData) {
      try {
        // Aseguramos que el ID de la categoría seleccionada se incluya en los datos.
        const dataToSend = {
            ...transactionData,
            categoriaId: this.selectedCategory.id // Asume que `selectedCategory` tiene el `id`
        }

        await transaccionService.create(dataToSend);
        await this.loadData(); // Recargar datos
        this.isModalOpen = false;
        
        // Mostrar notificación de éxito 
        alert('¡Gasto registrado! 🎉');
      } catch (error) {
        console.error('Error al crear transacción:', error);
        alert('Error al guardar el gasto');
      }
    }
  }
};
</script>

<style scoped>
/* Puedes añadir estilos específicos de Dashboard aquí si es necesario */
.dashboard-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.dashboard-page h1 {
  color: var(--dark-chocolate); /* Asumiendo que tienes esta variable CSS */
  margin-bottom: 30px;
  text-align: center;
}
</style>