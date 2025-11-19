package com.restaurant.controller;

import com.restaurant.model.Usuario;
import com.restaurant.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setup")
@RequiredArgsConstructor
public class SetupController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/create-admin")
    public String createAdmin() {
        // Eliminar admin existente
        usuarioRepository.findByNombreUsuario("admin")
                .ifPresent(usuarioRepository::delete);

        // Crear nuevo admin
        Usuario admin = new Usuario();
        admin.setNombreUsuario("admin");
        admin.setContrasena(passwordEncoder.encode("admin123"));
        admin.setRol("ADMIN");
        admin.setEstado(true);

        usuarioRepository.save(admin);

        return "✅ Usuario admin creado con contraseña: admin123";
    }
}