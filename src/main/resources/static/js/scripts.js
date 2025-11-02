// Espera a que todo el HTML esté cargado
document.addEventListener('DOMContentLoaded', () => {
    /**
     * Función reutilizable para controlar cualquier barra lateral (sidebar)
     * @param {string} triggerId - El ID del elemento que ABRE la sidebar
     * @param {string} sidebarId - El ID de la sidebar que se va a mostrar
     * @param {string} closeId - El ID del botón que CIERRA la sidebar
     * @param {string} overlayId - El ID del fondo oscuro (overlay)
     */
    const setupSidebar = (triggerId, sidebarId, closeId, overlayId) => {
        const openEl = document.getElementById(triggerId);
        const sidebar = document.getElementById(sidebarId);
        const closeEl = document.getElementById(closeId);
        const overlay = document.getElementById(overlayId);

        if (!openEl || !sidebar || !closeEl || !overlay) {
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

        openEl.addEventListener('click', openSidebar);
        closeEl.addEventListener('click', closeSidebar);
        overlay.addEventListener('click', closeSidebar);
    };

    // 1. Sidebar "Registrar/Editar Movimiento"
    setupSidebar('btn-open-sidebar', 'register-sidebar', 'btn-close-sidebar', 'sidebar-overlay');
    // 2. Sidebar "Añadir/Editar Categoría"
    setupSidebar('btn-edit-category', 'category-sidebar', 'btn-close-category', 'sidebar-overlay');
    // 3. Sidebar "Añadir Cuenta" (pantalla Cuentas)
    setupSidebar('btn-open-add-account', 'add-account-sidebar', 'btn-close-add-account', 'add-account-overlay');

    // Toggle de campos exclusivos de crédito en "Añadir Cuenta"
    const cuentaTipo = document.getElementById('cuenta-tipo');
    const creditOnlyEls = document.querySelectorAll('#add-account-sidebar .credit-only');

    const refreshCreditFields = () => {
        if (!cuentaTipo || !creditOnlyEls.length) return;
        const isCredito = cuentaTipo.value === 'CREDITO';
        creditOnlyEls.forEach(el => {
            // Si tus estilos usan display grid/inline-grid para esos bloques, ajusta aquí:
            el.style.display = isCredito ? '' : 'none';
        });
    };
    if (cuentaTipo) {
        cuentaTipo.addEventListener('change', refreshCreditFields);
        refreshCreditFields(); // estado inicial
    }

    // Lógica de botones Ingreso/Gasto para formulario de transacciones
    const btnIngreso = document.getElementById('btn-ingreso');
    const btnGasto = document.getElementById('btn-gasto');
    const tipoMovimientoInput = document.getElementById('tipoMovimiento');
    const categoriaSelect = document.getElementById('sidebar-cat');

    const filtrarCategorias = (tipo) => {
        if (!categoriaSelect) return;
        // Reiniciar selección
        categoriaSelect.value = "";
        // Recorrer todas las opciones (incluyendo "Nueva categoría")
        Array.from(categoriaSelect.options).forEach(option => {
            if (!option.value || option.value === "" || option.value === "new") {
                // Dejar visible la opción placeholder y la opción "new"
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

    if (btnIngreso) {
        btnIngreso.addEventListener('click', (e) => {
            e.preventDefault();
            btnIngreso.classList.add('active');
            btnGasto.classList.remove('active');
            if (tipoMovimientoInput) tipoMovimientoInput.value = 'INGRESO';
            filtrarCategorias('INGRESO');
        });
    }
    if (btnGasto) {
        btnGasto.addEventListener('click', (e) => {
            e.preventDefault();
            btnGasto.classList.add('active');
            btnIngreso.classList.remove('active');
            if (tipoMovimientoInput) tipoMovimientoInput.value = 'EGRESO';
            filtrarCategorias('EGRESO');
        });
    }
    // Al cargar la página, asegurar que las categorías estén filtradas según tipo por defecto (INGRESO)
    if (document.getElementById('register-sidebar')) {
        filtrarCategorias('INGRESO');
    }

    // Lógica de selección de categoría: detectar "Nueva categoría..."
    if (categoriaSelect) {
        categoriaSelect.addEventListener('change', () => {
            if (categoriaSelect.value === "new") {
                // Reiniciar selección a estado inicial
                categoriaSelect.value = "";
                // Abrir sidebar de categorías en modo nueva
                const catSidebar = document.getElementById('category-sidebar');
                const overlay = document.getElementById('sidebar-overlay');
                if (catSidebar && overlay) {
                    // Limpiar formulario de categoría
                    catSidebar.querySelector('input[name="nombre"]').value = "";
                    catSidebar.querySelector('#tipoCategoria').value = btnIngreso.classList.contains('active') ? 'INGRESO' : 'EGRESO';
                    // Ajustar botones de tipo en sidebar categoría
                    const btnCatIng = document.getElementById('btn-cat-ingreso');
                    const btnCatGas = document.getElementById('btn-cat-gasto');
                    if (btnCatIng && btnCatGas) {
                        if (btnIngreso.classList.contains('active')) {
                            btnCatIng.classList.add('active');
                            btnCatGas.classList.remove('active');
                        } else {
                            btnCatIng.classList.remove('active');
                            btnCatGas.classList.add('active');
                        }
                    }
                    catSidebar.querySelector('#cat-color').value = "#000000"; // color por defecto negro (el usuario deberá cambiarlo)
                    catSidebar.classList.add('active');
                    overlay.classList.add('active');
                }
            }
        });
    }

    // Lógica de botones Ingreso/Gasto para formulario de categoría
    const btnCatIngreso = document.getElementById('btn-cat-ingreso');
    const btnCatGasto = document.getElementById('btn-cat-gasto');
    const tipoCategoriaInput = document.getElementById('tipoCategoria');
    if (btnCatIngreso && btnCatGasto && tipoCategoriaInput) {
        btnCatIngreso.addEventListener('click', (e) => {
            e.preventDefault();
            btnCatIngreso.classList.add('active');
            btnCatGasto.classList.remove('active');
            tipoCategoriaInput.value = 'INGRESO';
        });
        btnCatGasto.addEventListener('click', (e) => {
            e.preventDefault();
            btnCatGasto.classList.add('active');
            btnCatIngreso.classList.remove('active');
            tipoCategoriaInput.value = 'EGRESO';
        });
    }

    // Rellenar formulario de transacción al hacer clic en Editar (icono lápiz en la tabla)
    document.querySelectorAll('.edit-tx-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const txId = btn.dataset.id;
            const cuentaId = btn.dataset.cuenta;
            const categoriaId = btn.dataset.categoria;
            const monto = btn.dataset.monto;
            const descripcion = btn.dataset.descripcion;
            const tipo = btn.dataset.tipo;  // "INGRESO" o "EGRESO"
            // Abrir sidebar de transacción
            const txSidebar = document.getElementById('register-sidebar');
            const overlay = document.getElementById('sidebar-overlay');
            if (txSidebar && overlay) {
                txSidebar.classList.add('active');
                overlay.classList.add('active');
            }
            // Asignar valores a los campos del formulario
            const idInput = document.getElementById('txId');
            const cuentaSelect = document.getElementById('sidebar-cuenta');
            const catSelect = document.getElementById('sidebar-cat');
            const montoInput = document.querySelector('input[name="monto"]');
            const descInput = document.querySelector('input[name="descripcion"]');
            if (idInput) idInput.value = txId;
            if (cuentaSelect) cuentaSelect.value = cuentaId;
            if (montoInput) montoInput.value = monto;
            if (descInput) descInput.value = descripcion;
            if (tipo === 'INGRESO') {
                btnIngreso.classList.add('active');
                btnGasto.classList.remove('active');
                if (tipoMovimientoInput) tipoMovimientoInput.value = 'INGRESO';
            } else if (tipo === 'EGRESO') {
                btnGasto.classList.add('active');
                btnIngreso.classList.remove('active');
                if (tipoMovimientoInput) tipoMovimientoInput.value = 'EGRESO';
            }
            // Filtrar categorías según tipo y seleccionar la correspondiente
            filtrarCategorias(tipo);
            if (catSelect) {
                catSelect.value = categoriaId;
            }
        });
    });

    // Confirmación al eliminar transacción
    document.querySelectorAll('.delete-tx-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            if (!confirm("¿Seguro que deseas eliminar esta transacción?")) {
                e.preventDefault();
            }
        });
    });
    // Confirmación al eliminar categoría
    const deleteCatBtn = document.getElementById('btn-delete-category');
    if (deleteCatBtn) {
        deleteCatBtn.addEventListener('click', (e) => {
            if (!confirm("¿Seguro que deseas eliminar esta categoría?")) {
                e.preventDefault();
            }
        });
    }
});

// ===== Menú de usuario (perfil/configuración/cerrar sesión) =====
const userMenuBtn = document.getElementById('userMenuBtn');
const userMenuPanel = document.getElementById('userMenuPanel');
const userMenu = document.querySelector('.user-menu');

if (userMenuBtn && userMenu && userMenuPanel) {
    const openMenu = () => {
        userMenu.dataset.open = 'true';
        userMenuBtn.setAttribute('aria-expanded', 'true');
    };
    const closeMenu = () => {
        userMenu.dataset.open = 'false';
        userMenuBtn.setAttribute('aria-expanded', 'false');
    };
    const toggleMenu = (e) => {
        e.preventDefault();
        const isOpen = userMenu.dataset.open === 'true';
        if (isOpen) closeMenu(); else {
            // Si alguna sidebar/overlay quedó abierta, ciérrala para que no bloquee clics en el header
            ['register-sidebar', 'category-sidebar', 'add-account-sidebar'].forEach(id => {
                const el = document.getElementById(id);
                if (el) el.classList.remove('active');
            });
            ['sidebar-overlay', 'add-account-overlay'].forEach(id => {
                const el = document.getElementById(id);
                if (el) el.classList.remove('active');
            });
            openMenu();
        }
    };

    userMenuBtn.addEventListener('click', toggleMenu);

    // Cerrar al hacer clic fuera
    document.addEventListener('click', (e) => {
        if (!userMenu.contains(e.target)) closeMenu();
    });

    // Cerrar con ESC
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') closeMenu();
    });

    // Si se hace clic en alguna opción del menú (enlaces o botón logout), cerrar
    userMenuPanel.addEventListener('click', (e) => {
        const tgt = e.target.closest('a, button');
        if (tgt) closeMenu();
    });
}
