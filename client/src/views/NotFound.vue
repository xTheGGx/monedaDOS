
<template>
  <div class="page-wrapper">
    <Navbar />
    
    <main class="container main-content" style="display: flex; justify-content: center; align-items: center; text-align: center;">
        <div class="card" style="max-width: 500px; padding: 3rem;">
            <img src="/images/dos_fear.png" alt="Dos asustado" style="width: 120px; margin-bottom: 1.5rem;">
            
            <h1 style="margin-bottom: 1rem; font-size: 2rem;">404</h1>
            <h2 style="margin-bottom: 0.5rem;">¡Ups! Página no encontrada</h2>
            
            <p class="text-muted" style="margin-bottom: 2rem;">
                Parece que esta moneda se nos perdió en el camino o la función aún está en construcción.
            </p>

            <router-link to="/cuentas" class="button-primary" style="justify-content: center; text-decoration: none;">
                Volver a mis Cuentas
            </router-link>
        </div>
    </main>
  </div>
</template>