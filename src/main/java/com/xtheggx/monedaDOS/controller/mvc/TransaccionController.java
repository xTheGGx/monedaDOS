package com.xtheggx.monedaDOS.controller.mvc;

import com.xtheggx.monedaDOS.dto.TransaccionDTO;
import com.xtheggx.monedaDOS.exception.ResourceNotFoundException;
import com.xtheggx.monedaDOS.model.Categoria;
import com.xtheggx.monedaDOS.model.CategoriaTipo;
import com.xtheggx.monedaDOS.model.Cuenta;
import com.xtheggx.monedaDOS.model.Transaccion;
import com.xtheggx.monedaDOS.repository.UsuarioRepository;
import com.xtheggx.monedaDOS.service.CategoriaService;
import com.xtheggx.monedaDOS.service.CuentaService;
import com.xtheggx.monedaDOS.service.TransaccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/transacciones")
public class TransaccionController {

    private final TransaccionService transaccionService;
    private final CuentaService cuentaService;
    private final CategoriaService categoriaService;
    private final UsuarioRepository usuarioRepository;

    /**
     * Obtiene el ID del usuario autenticado usando el email del Authentication
     */
    private Long currentUserIdOrNull() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        // En JWT/UserDetails, getName() trae el email
        String email = auth.getName();
        if (email == null || email.isBlank() || "anonymousUser".equals(String.valueOf(auth.getPrincipal()))) {
            return null;
        }
        return usuarioRepository.findIdByEmailIgnoreCase(email).orElse(null);
    }

    // GET: mostrar página de transacciones con filtros, lista y formulario
    @GetMapping
    public String listarTransacciones(
            Model model,
            @RequestParam(required = false) String mes,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String cuenta,
            @RequestParam(defaultValue = "0") int page
    ) {
        Long userId = currentUserIdOrNull();
        if (userId == null) {
            return "redirect:/login";
        }

        // Determinar filtro de mes
        YearMonth filtroMes = null;
        String selectedMes;
        if (mes == null) {
            // Por defecto, si no se especifica, usar el mes actual
            filtroMes = YearMonth.now();
            selectedMes = filtroMes.toString();  // formato "YYYY-MM"
        } else if (mes.isEmpty()) {
            // "Todos los meses"
            selectedMes = "";
        } else {
            filtroMes = YearMonth.parse(mes);
            selectedMes = filtroMes.toString();
        }
        // Determinar filtro de tipo
        CategoriaTipo filtroTipo = null;
        String selectedTipo;
        if (tipo != null && !tipo.isEmpty()) {
            filtroTipo = CategoriaTipo.valueOf(tipo);
            selectedTipo = tipo;
        } else {
            selectedTipo = "";
        }
        // Determinar filtro de categoría
        Integer filtroCategoriaId = null;
        String selectedCategoria;
        Categoria filtroCategoria = null;
        if (categoria != null && !categoria.isEmpty()) {
            filtroCategoriaId = Integer.parseInt(categoria);
            selectedCategoria = categoria;
        } else {
            selectedCategoria = "";
        }
        // Determinar filtro de cuenta
        Integer filtroCuentaId = null;
        String selectedCuenta;
        if (cuenta != null && !cuenta.isEmpty()) {
            filtroCuentaId = Integer.parseInt(cuenta);
            selectedCuenta = cuenta;
        } else {
            selectedCuenta = "";
        }

        // Preparar consulta paginada de transacciones según filtros
        int pageSize = 10;
        Page<Transaccion> transaccionesPage;

        if (filtroCategoriaId != null || filtroCuentaId != null || filtroTipo != null) {

            final Integer filtroCategoriaIdFinal = filtroCategoriaId;
            final Integer filtroCuentaIdFinal = filtroCuentaId;
            final CategoriaTipo filtroTipoFinal = filtroTipo;

            List<Transaccion> todas;
            if (filtroMes != null) {
                LocalDateTime from = filtroMes.atDay(1).atStartOfDay();
                LocalDateTime to = filtroMes.atEndOfMonth().atTime(23, 59, 59);
                todas = transaccionService.listarTransaccionesPorUsuarioYFechas(userId, from, to);
            } else {
                todas = transaccionService.listarTransaccionesPorUsuario(userId);
            }

            // Usa las variables finales dentro de las lambdas:
            if (filtroTipoFinal != null) {
                todas = todas.stream()
                        .filter(tx -> tx.getCategoria().getTipo().equals(filtroTipoFinal))
                        .collect(Collectors.toList());
            }
            if (filtroCategoriaIdFinal != null) {
                todas = todas.stream()
                        .filter(tx -> tx.getCategoria().getIdCategoria().equals(filtroCategoriaIdFinal))
                        .collect(Collectors.toList());
            }
            if (filtroCuentaIdFinal != null) {
                todas = todas.stream()
                        .filter(tx -> tx.getCuenta().getIdCuenta().equals(filtroCuentaIdFinal))
                        .collect(Collectors.toList());
            }

            todas.sort((a, b) -> b.getFecha().compareTo(a.getFecha()));
            int total = todas.size();
            int fromIndex = page * pageSize;
            if (fromIndex > total) fromIndex = 0;
            int toIndex = Math.min(fromIndex + pageSize, total);
            List<com.xtheggx.monedaDOS.model.Transaccion> contenidoPagina = todas.subList(fromIndex, toIndex);
            transaccionesPage = new PageImpl<>(contenidoPagina, PageRequest.of(page, pageSize), total);

        } else {
            if (filtroMes != null) {
                LocalDateTime from = filtroMes.atDay(1).atStartOfDay();
                LocalDateTime to = filtroMes.atEndOfMonth().atTime(23, 59, 59);
                transaccionesPage = transaccionService.listarTransaccionesPaginadasPorFecha(userId, from, to, page, pageSize);
            } else {
                transaccionesPage = transaccionService.listarTransaccionesPaginadas(userId, page, pageSize);
            }
        }

        // Obtener listas de cuentas y categorías para el usuario
        List<Cuenta> cuentas = cuentaService.listar(userId);
        // Opcional: ordenar cuentas por nombre
        cuentas.sort((c1, c2) -> c1.getNombre().compareToIgnoreCase(c2.getNombre()));
        List<Categoria> categorias = categoriaService.listarCategoriasUsuario(userId);
        // Separar categorías por tipo para usarlas en el filtro (optgroup) si se desea
        List<Categoria> categoriasIngreso = categorias.stream()
                .filter(cat -> cat.getTipo() == CategoriaTipo.INGRESO)
                .collect(Collectors.toList());
        List<Categoria> categoriasGasto = categorias.stream()
                .filter(cat -> cat.getTipo() == CategoriaTipo.EGRESO)
                .collect(Collectors.toList());
        // Preparar lista de últimos 12 meses para el filtro de mes
        YearMonth mesActual = YearMonth.now();
        List<String[]> ultimosMeses = java.util.stream.IntStream.range(0, 12)
                .mapToObj(i -> mesActual.minusMonths(i))
                .sorted((m1, m2) -> m2.compareTo(m1))  // ordenar descendente del más reciente al más antiguo
                .map(ym -> {
                    // Nombre en español capitalizado Ej: "Noviembre 2025"
                    String nombreMes = ym.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
                    nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);
                    String anio = String.valueOf(ym.getYear());
                    return new String[]{ym.toString(), nombreMes + " " + anio};
                })
                .collect(Collectors.toList());

        // Agregar atributos al modelo para la vista
        if (!model.containsAttribute("transaccionDTO")) {
            model.addAttribute("transaccionDTO", new TransaccionDTO());
        }
        if (!model.containsAttribute("categoriaDTO")) {
            model.addAttribute("categoriaDTO", new com.xtheggx.monedaDOS.dto.CategoriaDTO());
        }
        model.addAttribute("transaccionesPage", transaccionesPage);
        model.addAttribute("cuentas", cuentas);
        model.addAttribute("categorias", categorias);
        model.addAttribute("categoriasIngreso", categoriasIngreso);
        model.addAttribute("categoriasGasto", categoriasGasto);
        model.addAttribute("ultimosMeses", ultimosMeses);
        // Valores seleccionados de filtros para mantener seleccionados en la vista
        model.addAttribute("selectedMes", selectedMes);
        model.addAttribute("selectedTipo", selectedTipo);
        model.addAttribute("selectedCategoria", selectedCategoria);
        model.addAttribute("selectedCuenta", selectedCuenta);

        return "home/transacciones";
    }

    private void cargarModeloComun(Model model, Long userId) {
        List<Cuenta> cuentas = cuentaService.listar(userId);
        cuentas.sort((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()));
        List<Categoria> categorias = categoriaService.listarCategoriasUsuario(userId);

        List<Categoria> categoriasIngreso = categorias.stream()
                .filter(c -> c.getTipo() == CategoriaTipo.INGRESO)
                .collect(java.util.stream.Collectors.toList());
        List<Categoria> categoriasGasto = categorias.stream()
                .filter(c -> c.getTipo() == CategoriaTipo.EGRESO)
                .collect(java.util.stream.Collectors.toList());

        // Meses (opcional, si la vista lo usa)
        java.time.YearMonth mesActual = java.time.YearMonth.now();
        List<String[]> ultimosMeses = java.util.stream.IntStream.range(0, 12)
                .mapToObj(i -> mesActual.minusMonths(i))
                .sorted((m1, m2) -> m2.compareTo(m1))
                .map(ym -> {
                    String nombreMes = ym.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("es", "ES"));
                    nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);
                    return new String[]{ym.toString(), nombreMes + " " + ym.getYear()};
                })
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("cuentas", cuentas);
        model.addAttribute("categorias", categorias);
        model.addAttribute("categoriasIngreso", categoriasIngreso);
        model.addAttribute("categoriasGasto", categoriasGasto);
        model.addAttribute("ultimosMeses", ultimosMeses);

        // Si tu vista espera estos “selected”, déjalos vacíos por defecto
        model.addAttribute("selectedMes", "");
        model.addAttribute("selectedTipo", "");
        model.addAttribute("selectedCategoria", "");
        model.addAttribute("selectedCuenta", "");
    }


    // POST: registrar o actualizar transacción
    @PostMapping
    public String guardarTransaccion(@Valid @ModelAttribute("transaccionDTO") TransaccionDTO transaccionDTO,
                                     BindingResult bindingResult,
                                     Model model,
                                     org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {

        Long userId = currentUserIdOrNull();
        if (userId == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            // ⚠️ REARMAR EL MODELO IGUAL QUE EN EL GET
            cargarModeloComun(model, userId);
            model.addAttribute("error", "Por favor corrige los errores del formulario");
            return "home/transacciones";
        }

        transaccionDTO.setUsuarioId(userId);

        try {
            if (transaccionDTO.getIdTransaccion() == null) {
                transaccionService.registrarTransaccion(transaccionDTO);
                ra.addFlashAttribute("created", "¡Transacción registrada exitosamente!");
            } else {
                transaccionService.actualizarTransaccion(transaccionDTO.getIdTransaccion(), transaccionDTO);
                ra.addFlashAttribute("created", "Transacción actualizada correctamente");
            }
        } catch (IllegalArgumentException | ResourceNotFoundException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/transacciones";
    }


    // POST: eliminar transacción existente
    @PostMapping("/eliminar/{id}")
    public String eliminarTransaccion(@PathVariable("id") Integer idTransaccion, Model model) {
        // Obtener usuario actual
        Long userId = currentUserIdOrNull();

        if (userId == null) {
            return "redirect:/login";
        }


        try {
            transaccionService.eliminar(idTransaccion);
        } catch (ResourceNotFoundException e) {
            // Si no existe, simplemente ignorar o agregar mensaje de error
            model.addAttribute("error", e.getMessage());
        }
        // Redirigir nuevamente a la lista (se podría agregar mensaje de éxito si se desea)
        return "redirect:/transacciones";
    }
}
