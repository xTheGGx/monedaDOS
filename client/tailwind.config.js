/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'cookie-dough': '#D4A373',
        'dark-chocolate': '#4A3B32',
        'oatmeal-paper': '#F3E5D8',
        'berry-jam': '#E07A5F',
        'matcha-tea': '#81B29A',
      }
    },
  },
  plugins: [],
}