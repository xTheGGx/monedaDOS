/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // Paleta "Cafetería"
        'cookie-dough': '#D4A373',       // Dorado/Beige fuerte (Botones, Iconos)
        'cookie-hover': '#C89563',       // Hover del botón
        'dark-chocolate': '#4A3B32',     // Texto principal
        'oatmeal-paper': '#F3E5D8',      // Fondo de tarjeta
        'milk-cream': '#FFF8F0',         // Fondo global
        'brown-muted': '#8B6F47',        // Texto secundario / Labels
        'berry-jam': '#E07A5F',          // Errores
      },
      animation: {
        'float': 'bounce 3s infinite',
      }
    },
  },
  plugins: [],
}