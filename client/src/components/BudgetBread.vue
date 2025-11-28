<template>
  <div class="budget-card">
    <div class="budget-header">
      <div>
        <h3>Presupuesto del Mes</h3>
        <p class="subtitle">Tu hogaza de dinero</p>
      </div>
      <div class="budget-amount">
        <div class="amount-remaining">${{ remaining.toFixed(2) }}</div>
        <p class="amount-label">quedan</p>
      </div>
    </div>

    <!-- Barra de Pan -->
    <div class="bread-bar-container">
      <div 
        class="bread-bar-fill"
        :style="{ 
          width: `${Math.min(percentage, 100)}%`,
          background: getBarColor(percentage)
        }"
      >
        <div class="bread-texture"></div>
      </div>
      
      <!-- Emoji de pan que se mueve -->
      <div 
        class="bread-emoji"
        :style="{ left: `${Math.min(percentage, 95)}%` }"
      >
        🍞
      </div>
    </div>

    <!-- Stats inferiores -->
    <div class="budget-stats">
      <span class="stat-remaining">${{ remaining.toFixed(0) }} restante</span>
      <span class="stat-total">${{ spent.toFixed(0) }} / ${{ budget.toFixed(0) }}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BudgetBread',
  props: {
    budget: {
      type: Number,
      required: true
    },
    spent: {
      type: Number,
      required: true
    }
  },
  computed: {
    remaining() {
      return this.budget - this.spent;
    },
    percentage() {
      return this.budget > 0 ? (this.spent / this.budget) * 100 : 0;
    }
  },
  methods: {
    getBarColor(percentage) {
      if (percentage > 90) {
        return 'linear-gradient(to right, #E07A5F, #D4685C)'; // Rojo (te pasaste)
      } else if (percentage > 70) {
        return 'linear-gradient(to right, #E9B05D, #D4A373)'; // Amarillo (cuidado)
      } else {
        return 'linear-gradient(to right, #D4A373, #C89563)'; // Cookie Dough (bien)
      }
    }
  }
};
</script>

<style scoped>
.budget-card {
  background: var(--oatmeal-paper);
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-medium);
  margin-bottom: 24px;
}

.budget-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.budget-header h3 {
  color: var(--dark-chocolate);
  margin-bottom: 4px;
}

.subtitle {
  font-size: 14px;
  color: var(--brown-muted);
}

.budget-amount {
  text-align: right;
}

.amount-remaining {
  font-family: var(--font-display);
  font-size: 36px;
  color: var(--cookie-dough);
  line-height: 1;
}

.amount-label {
  font-size: 14px;
  color: var(--brown-muted);
}

/* Barra de Pan */
.bread-bar-container {
  position: relative;
  height: 48px;
  background: var(--cream-milk);
  border-radius: var(--radius-full);
  border: 4px solid var(--cookie-dough);
  overflow: hidden;
  box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.1);
}

.bread-bar-fill {
  height: 100%;
  border-radius: var(--radius-full);
  transition: width 0.5s ease, background 0.3s ease;
  position: relative;
}

.bread-texture {
  width: 100%;
  height: 100%;
  background-image: 
    repeating-linear-gradient(
      90deg, 
      transparent, 
      transparent 15px, 
      rgba(255, 255, 255, 0.1) 15px, 
      rgba(255, 255, 255, 0.1) 30px
    );
}

.bread-emoji {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  font-size: 28px;
  transition: left 0.5s ease;
  pointer-events: none;
}

/* Stats */
.budget-stats {
  display: flex;
  justify-content: space-between;
  margin-top: 12px;
  font-size: 14px;
}

.stat-remaining {
  color: var(--matcha-tea);
  font-weight: 600;
}

.stat-total {
  color: var(--brown-muted);
}
</style>