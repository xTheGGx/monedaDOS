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
        // Busca los elementos en la página
        const openBtn = document.getElementById(triggerId);
        const sidebar = document.getElementById(sidebarId);
        const closeBtn = document.getElementById(closeId);
        const overlay = document.getElementById(overlayId);

        // Si no encontramos todos los elementos, no hacemos nada
        // (Esto evita errores si estamos en una página que no tiene esa sidebar)
        if (!openBtn || !sidebar || !closeBtn || !overlay) {
            return;
        }

        // Función para abrir
        const openSidebar = () => {
            sidebar.classList.add('active');
            overlay.classList.add('active');
        };

        // Función para cerrar
        const closeSidebar = () => {
            sidebar.classList.remove('active');
            overlay.classList.remove('active');
        };

        // Asignar los eventos
        openBtn.addEventListener('click', openSidebar);
        closeBtn.addEventListener('click', closeSidebar);
        overlay.addEventListener('click', closeSidebar);
    };

    // --- CONFIGURACIÓN DE TODAS LAS SIDEBARS DE LA APP ---

    // 1. Sidebar de "Registrar Movimiento" (para transacciones.html)
    setupSidebar(
        'btn-open-sidebar',     // Botón que la abre
        'register-sidebar',     // La sidebar en sí
        'btn-close-sidebar',    // Botón de cierre
        'sidebar-overlay'       // El fondo oscuro
    );

    // 2. Sidebar de "Añadir Cuenta" (para cuentas.html)
    setupSidebar(
        'btn-open-add-account',  // Botón que la abre (la tarjeta punteada)
        'add-account-sidebar',   // La nueva sidebar
        'btn-close-add-account', // El nuevo botón de cierre
        'add-account-overlay'    // El nuevo fondo oscuro
    );


    // --- Lógica de botones Ingreso/Gasto (de transacciones.html) ---
    // (La mantenemos aquí, pero solo funcionará si los botones existen)
    const btnIngreso = document.getElementById('btn-ingreso');
    const btnGasto = document.getElementById('btn-gasto');

    if (btnIngreso && btnGasto) {
        btnIngreso.addEventListener('click', (e) => {
            e.preventDefault();
            btnIngreso.classList.add('active');
            btnGasto.classList.remove('active');
        });
        btnGasto.addEventListener('click', (e) => {
            e.preventDefault();
            btnGasto.classList.add('active');
            btnIngreso.classList.remove('active');
        });
    }

});

(() => {
    const menu = document.querySelector('.user-menu');
    if (!menu) return;

    const btn = menu.querySelector('#userMenuBtn');
    const toggle = (open) => {
        menu.dataset.open = open ? 'true' : 'false';
        btn.setAttribute('aria-expanded', open);
    };

    // Abrir/cerrar por click del botón
    btn.addEventListener('click', (e) => {
        e.stopPropagation();
        toggle(menu.dataset.open !== 'true');
    });

    // Cerrar al hacer click fuera
    document.addEventListener('click', (e) => {
        if (!menu.contains(e.target)) toggle(false);
    });

    // Cerrar con Escape
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') toggle(false);
    });
})();
