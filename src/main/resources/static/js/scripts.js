// Espera a que todo el HTML esté cargado
document.addEventListener('DOMContentLoaded', () => {

    /**
     * Función reutilizable para controlar cualquier barra lateral (sidebar)
     * @param {string} triggerId - El ID del botón que ABRE la sidebar
     * @param {string} sidebarId - El ID de la sidebar que se va a mostrar
     * @param {string} closeId - El ID del botón que CIERRA la sidebar
     * @param {string} overlayId - El ID del fondo oscuro (overlay)
     */
    const setupSidebar = (triggerId, sidebarId, closeId, overlayId) => {
        const openBtn = document.getElementById(triggerId);
        const sidebar = document.getElementById(sidebarId);
        const closeBtn = document.getElementById(closeId);
        const overlay = document.getElementById(overlayId);

        if (!openBtn || !sidebar || !closeBtn || !overlay) {
            return;
        }
        
        const openSidebar = () => {
            sidebar.classList.add('active');
            overlay.classList.add('active');
        };

        const closeSidebar = () => {
            sidebar.classList.remove('active');
            overlay.classList.remove('active');
        };

        openBtn.addEventListener('click', openSidebar);
        closeBtn.addEventListener('click', closeSidebar);
        overlay.addEventListener('click', closeSidebar);
    };

    // --- CONFIGURACIÓN DE SIDEBARS ---

    // 1. Sidebar de "Registrar Movimiento" (transacciones.html)
    setupSidebar(
        'btn-open-sidebar',
        'register-sidebar',
        'btn-close-sidebar',
        'sidebar-overlay'
    );

    // 2. Sidebar de "Añadir Cuenta" (cuentas.html)
    setupSidebar(
        'btn-open-add-account',
        'add-account-sidebar',
        'btn-close-add-account',
        'add-account-overlay'
    );


    // --- Lógica de botones Ingreso/Gasto (transacciones.html) ---
    
    const btnIngreso = document.getElementById('btn-ingreso');
    const btnGasto = document.getElementById('btn-gasto');
    // El input oculto que guarda el tipo (INGRESO/EGRESO)
    const tipoMovimientoInput = document.getElementById('tipoMovimiento');
    // El <select> de categorías
    const categoriaSelect = document.getElementById('sidebar-cat');

    // Función para filtrar las categorías en el <select>
    const filtrarCategorias = (tipo) => {
        if (!categoriaSelect) return;

        // Reiniciar la selección
        categoriaSelect.value = ""; 
        
        // Recorrer todas las <option>
        Array.from(categoriaSelect.options).forEach(option => {
            if (option.value === "") { // Dejar "Selecciona una categoría"
                option.style.display = "block";
                return;
            }
            // Mostrar solo las opciones que coinciden con el 'data-tipo'
            if (option.dataset.tipo === tipo) {
                option.style.display = "block";
            } else {
                option.style.display = "none";
            }
        });
    };

    // Evento al hacer clic en INGRESO
    if (btnIngreso) {
        btnIngreso.addEventListener('click', (e) => {
            e.preventDefault();
            btnIngreso.classList.add('active');
            btnGasto.classList.remove('active');
            
            // Actualizar el input oculto
            if (tipoMovimientoInput) tipoMovimientoInput.value = 'INGRESO';
            
            // Filtrar categorías
            filtrarCategorias('INGRESO');
        });
    }

    // Evento al hacer clic en GASTO
    if (btnGasto) {
        btnGasto.addEventListener('click', (e) => {
            e.preventDefault();
            btnGasto.classList.add('active');
            btnIngreso.classList.remove('active');
            
            // Actualizar el input oculto
            if (tipoMovimientoInput) tipoMovimientoInput.value = 'EGRESO';
            
            // Filtrar categorías
            filtrarCategorias('EGRESO');
        });
    }
    
    // Al cargar la página, asegurarse de que las categorías estén filtradas
    // según el botón activo por defecto (INGRESO)
    if (document.getElementById('register-sidebar')) {
         filtrarCategorias('INGRESO');
    }

});

// --- Lógica del Menú de Usuario (Dropdown) ---
(() => {
    const menu = document.querySelector('.user-menu');
    if (!menu) return;

    const btn = menu.querySelector('#userMenuBtn');
    const toggle = (open) => {
        menu.dataset.open = open ? 'true' : 'false';
        btn.setAttribute('aria-expanded', open);
    };

    btn.addEventListener('click', (e) => {
        e.stopPropagation();
        toggle(menu.dataset.open !== 'true');
    });

    document.addEventListener('click', (e) => {
        if (!menu.contains(e.target)) toggle(false);
    });

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') toggle(false);
    });
})();