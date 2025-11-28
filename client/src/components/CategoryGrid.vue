<template>
  <div class="category-section">
    <h2>📋 Hacer un Pedido</h2>
    <p class="section-subtitle">Selecciona la categoría de tu gasto</p>

    <div class="category-grid">
      <button
        v-for="category in categories"
        :key="category.id"
        @click="selectCategory(category)"
        class="category-button"
        :style="{ borderBottomColor: category.color }"
      >
        <div 
          class="category-icon"
          :style="{ backgroundColor: category.color }"
        >
          <component :is="category.iconComponent" />
        </div>
        <span class="category-name">{{ category.nombre }}</span>
      </button>

      <!-- Botón "Otro" -->
      <button
        @click="selectCategory({ nombre: 'Otro' })"
        class="category-button category-other"
      >
        <div class="category-icon">
          <span class="plus-icon">+</span>
        </div>
        <span class="category-name">Otro</span>
      </button>
    </div>
  </div>
</template>

<script>
// Importa iconos de lucide-vue-next o usa emojis
export default {
  name: 'CategoryGrid',
  props: {
    categories: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    selectCategory(category) {
      this.$emit('select', category);
    }
  }
};
</script>

<style scoped>
.category-section {
  margin-bottom: 32px;
}

.category-section h2 {
  color: var(--dark-chocolate);
  margin-bottom: 8px;
}

.section-subtitle {
  font-size: 14px;
  color: var(--brown-muted);
  margin-bottom: 16px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 16px;
}

.category-button {
  background: var(--oatmeal-paper);
  border: none;
  border-bottom: 4px solid var(--cookie-dough);
  border-radius: var(--radius-lg);
  padding: 20px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: var(--shadow-soft);
}

.category-button:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: var(--shadow-medium);
}

.category-button:active {
  transform: translateY(0) scale(0.98);
}

.category-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
}

.category-name {
  font-size: 14px;
  color: var(--dark-chocolate);
  font-weight: 500;
}

/* Botón "Otro" */
.category-other {
  background: var(--cookie-dough);
  border-bottom-color: var(--cookie-dough-hover);
}

.category-other .category-icon {
  background: var(--cookie-dough-hover);
}

.category-other .category-name {
  color: white;
}

.plus-icon {
  font-size: 32px;
  font-weight: 300;
}
</style>