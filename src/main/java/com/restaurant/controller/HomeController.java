package com.restaurant.controller;

import com.restaurant.model.Mesa;
import com.restaurant.service.ClienteService;
import com.restaurant.service.MesaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ClienteService clienteService;
    private final MesaService mesaService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping({"/", "/home"})
    public String home(Model model, Authentication authentication) {
        // Estadísticas para el dashboard
        long totalClientes = clienteService.contarActivos();
        long mesasDisponibles = mesaService.contarPorEstado(Mesa.EstadoMesa.DISPONIBLE);
        long mesasOcupadas = mesaService.contarPorEstado(Mesa.EstadoMesa.OCUPADA);
        long mesasReservadas = mesaService.contarPorEstado(Mesa.EstadoMesa.RESERVADA);

        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("mesasDisponibles", mesasDisponibles);
        model.addAttribute("mesasOcupadas", mesasOcupadas);
        model.addAttribute("mesasReservadas", mesasReservadas);
        model.addAttribute("usuario", authentication.getName());

        return "index";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "error/acceso-denegado";
    }
}