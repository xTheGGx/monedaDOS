<template>
  <transition name="modal-fade">
    <div v-if="isOpen" class="modal-backdrop" @click.self="closeModal">
      <div class="modal-card">
        <!-- Botón de cerrar -->
        <button @click="closeModal" class="close-btn">
          ✕
        </button>

        <!-- Header con emoji de categoría -->
        <div class="modal-header">
          <div class="category-emoji">{{ getCategoryEmoji(selectedCategory) }}</div>
          <h2>Nuevo Pedido</h2>
          <p class="category-label">{{ selectedCategory || 'Selecciona categoría' }}</p>
        </div>

        <!-- Formulario -->
        <form @submit.prevent="handleSubmit" class="transaction-form">
          <!-- Input de Monto -->
          <div class="form-group">
            <label>¿Cuánto gastaste?</label>
            <div class="amount-input-wrapper">
              <span class="currency-symbol">$</span>
              <input
                type="number"
                step="0.01"
                v-model="formData.monto"
                placeholder="0.00"
                required
                class="amount-input"
              />
            </div>
          </div>

          <!-- Input de Descripción -->
          <div class="form-group">
            <label>¿En qué lo gastaste?</label>
            <input
              type="text"
              v-model="formData.descripcion"
              placeholder="Ej: Desayuno en la cafetería"
              required
              class="text-input"
            />
          </div>

          <!-- Selector de Cuenta -->
          <div class="form-group">
            <label>¿De qué frasco sale?</label>
            <select v-model="formData.cuentaId" required class="select-input">
              <option value="">Selecciona cuenta</option>
              <option v-for="cuenta in cuentas" :key="cuenta.id" :value="cuenta.id">
                {{ cuenta.nombre }} - ${{ cuenta.saldo.toFixed(2) }}
              </option>
            </select>
          </div>

          <!-- Botones -->
          <div class="button-row">
            <button type="button" @click="closeModal" class="btn btn-secondary">
              Cancelar
            </button>
            <button type="submit" class="btn btn-primary btn-bounce">
              Agregar Gasto
            </button>
          </div>
        </form>

        <!-- Decoración -->
        <div class="decoration-cookie top-left">🍪</div>
        <div class="decoration-cookie bottom-right">☕</div>
      </div>
    </div>
  </transition>
</template>

<script>
export default {
  name: 'TransactionModal',
  props: {
    isOpen: Boolean,
    selectedCategory: String,
    cuentas: Array
  },
  data() {
    return {
      formData: {
        monto: '',
        descripcion: '',
        cuentaId: '',
        categoriaId: null
      }
    };
  },
  methods: {
    handleSubmit() {
      // Emitir evento al componente padre con los datos
      this.$emit('submit', {
        ...this.formData,
        fecha: new Date().toISOString()
      });
      this.resetForm();
    },
    closeModal() {
      this.$emit('close');
      this.resetForm();
    },
    resetForm() {
      this.formData = {
        monto: '',
        descripcion: '',
        cuentaId: '',
        categoriaId: null
      };
    },
    getCategoryEmoji(category) {
      const emojiMap = {
        'Comida': '🍕',
        'Transporte': '🚗',
        'Café': '☕',
        'Apps': '📱',
        'Ropa': '👕',
        'Salud': '💊',
        'Mascotas': '🐾',
        'Casa': '🏠',
        'Regalos': '🎁',
        'Ahorro': '🐷'
      };
      return emojiMap[category] || '📝';
    }
  }
};
</script>

<style scoped>
/* Backdrop del modal */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(74, 59, 50, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

/* Card del modal */
.modal-card {
  position: relative;
  background: var(--cream-milk);
  border-radius: var(--radius-lg);
  padding: 40px 32px;
  max-width: 480px;
  width: 100%;
  box-shadow: var(--shadow-large);
}

/* Botón cerrar */
.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--oatmeal-paper);
  border: none;
  font-size: 20px;
  cursor: pointer;
  transition: background 0.2s;
}

.close-btn:hover {
  background: var(--cookie-dough);
  color: white;
}

/* Header */
.modal-header {
  text-align: center;
  margin-bottom: 32px;
}

.category-emoji {
  font-size: 64px;
  margin-bottom: 16px;
}

.modal-header h2 {
  color: var(--dark-chocolate);
  margin-bottom: 8px;
}

.category-label {
  color: var(--brown-muted);
  font-size: 14px;
}

/* Formulario */
.transaction-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group label {
  display: block;
  font-size: 14px;
  color: var(--brown-muted);
  margin-bottom: 8px;
}

/* Input de monto */
.amount-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.currency-symbol {
  position: absolute;
  left: 24px;
  font-family: var(--font-display);
  font-size: 32px;
  color: var(--cookie-dough);
  pointer-events: none;
}

.amount-input {
  width: 100%;
  background: var(--oatmeal-paper);
  border: 3px solid transparent;
  border-radius: var(--radius-full);
  padding: 20px 24px 20px 64px;
  font-family: var(--font-display);
  font-size: 32px;
  color: var(--dark-chocolate);
  text-align: center;
  transition: border-color 0.2s;
}

.amount-input:focus {
  outline: none;
  border-color: var(--cookie-dough);
}

/* Input de texto */
.text-input,
.select-input {
  width: 100%;
  background: var(--oatmeal-paper);
  border: 3px solid transparent;
  border-radius: var(--radius-full);
  padding: 16px 20px;
  font-family: var(--font-body);
  font-size: 16px;
  color: var(--dark-chocolate);
  transition: border-color 0.2s;
}

.text-input:focus,
.select-input:focus {
  outline: none;
  border-color: var(--cookie-dough);
}

/* Botones */
.button-row {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.btn {
  flex: 1;
  padding: 16px;
  border: none;
  border-radius: var(--radius-full);
  font-family: var(--font-body);
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary {
  background: var(--cookie-dough);
  color: white;
  box-shadow: var(--shadow-medium);
}

.btn-primary:hover {
  background: var(--cookie-dough-hover);
  transform: translateY(-2px);
  box-shadow: var(--shadow-large);
}

.btn-secondary {
  background: var(--oatmeal-paper);
  color: var(--dark-chocolate);
}

.btn-secondary:hover {
  background: #E9D5C8;
}

/* Decoraciones */
.decoration-cookie {
  position: absolute;
  font-size: 48px;
}

.top-left {
  top: -12px;
  left: -12px;
  transform: rotate(12deg);
}

.bottom-right {
  bottom: -12px;
  right: -12px;
  transform: rotate(-12deg);
}

/* Animación del modal */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .modal-card {
  animation: modal-slide-up 0.3s;
}

@keyframes modal-slide-up {
  from {
    transform: translateY(40px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>