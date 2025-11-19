package com.restaurant.controller;

import com.restaurant.model.Mesa;
import com.restaurant.service.MesaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mesas")
@RequiredArgsConstructor
public class MesaController {

    private final MesaService mesaService;

    @GetMapping
    public String listar(Model model, @RequestParam(required = false) String estado) {
        if (estado != null && !estado.trim().isEmpty()) {
            try {
                String estadoMesa = estado.toUpperCase();
                model.addAttribute("mesas", mesaService.listarPorEstado(estadoMesa));
                model.addAttribute("estadoFiltro", estado);
            } catch (IllegalArgumentException e) {
                model.addAttribute("mesas", mesaService.listarTodas());
            }
        } else {
            model.addAttribute("mesas", mesaService.listarTodas());
        }

        // Estadísticas
        model.addAttribute("totalDisponibles", mesaService.contarPorEstado("DISPONIBLE"));
        model.addAttribute("totalOcupadas", mesaService.contarPorEstado("OCUPADA"));
        model.addAttribute("totalReservadas", mesaService.contarPorEstado("RESERVADA"));
        model.addAttribute("totalMantenimiento", mesaService.contarPorEstado("MANTENIMIENTO"));

        return "mesas/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("mesa", new Mesa());
        return "mesas/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Mesa mesa = mesaService.buscarPorId(id)
                .orElse(null);

        if (mesa == null) {
            redirectAttributes.addFlashAttribute("error", "Mesa no encontrada");
            return "redirect:/mesas";
        }

        model.addAttribute("mesa", mesa);
        return "mesas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Mesa mesa, RedirectAttributes redirectAttributes) {
        try {
            mesaService.guardar(mesa);
            redirectAttributes.addFlashAttribute("success",
                    mesa.getIdMesa() == null ? "Mesa creada exitosamente" : "Mesa actualizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la mesa: " + e.getMessage());
        }
        return "redirect:/mesas";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            mesaService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Mesa eliminada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la mesa: " + e.getMessage());
        }
        return "redirect:/mesas";
    }
}