package com.restaurant.aspect;

import com.restaurant.model.Bitacora;
import com.restaurant.model.Usuario;
import com.restaurant.repository.BitacoraRepository;
import com.restaurant.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditoriaAspect {

    private final BitacoraRepository bitacoraRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Auditar operaciones de guardado en ClienteService
     */
    @AfterReturning(
            pointcut = "execution(* com.restaurant.service.ClienteService.guardar(..))",
            returning = "result"
    )
    public void auditarGuardarCliente(JoinPoint joinPoint, Object result) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] != null) {
                Object cliente = args[0];
                Long idCliente = (Long) cliente.getClass().getMethod("getIdCliente").invoke(cliente);
                String nombre = (String) cliente.getClass().getMethod("getNombre").invoke(cliente);
                String apellido = (String) cliente.getClass().getMethod("getApellido").invoke(cliente);

                String accion = (idCliente == null)
                        ? "CREAR Cliente: " + nombre + " " + apellido
                        : "ACTUALIZAR Cliente: " + nombre + " " + apellido;

                registrarBitacora(accion, "Cliente",
                        (Long) result.getClass().getMethod("getIdCliente").invoke(result));
            }
        } catch (Exception e) {
            log.error("Error en auditoría de Cliente", e);
        }
    }

    /**
     * Auditar operaciones de eliminación en ClienteService
     */
    @AfterReturning("execution(* com.restaurant.service.ClienteService.eliminar(..))")
    public void auditarEliminarCliente(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                Long idCliente = (Long) args[0];
                registrarBitacora("ELIMINAR (desactivar) Cliente", "Cliente", idCliente);
            }
        } catch (Exception e) {
            log.error("Error en auditoría de eliminación de Cliente", e);
        }
    }

    /**
     * Auditar operaciones de activación en ClienteService
     */
    @AfterReturning("execution(* com.restaurant.service.ClienteService.activar(..))")
    public void auditarActivarCliente(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                Long idCliente = (Long) args[0];
                registrarBitacora("ACTIVAR Cliente", "Cliente", idCliente);
            }
        } catch (Exception e) {
            log.error("Error en auditoría de activación de Cliente", e);
        }
    }

    /**
     * Auditar operaciones de guardado en MesaService
     */
    @AfterReturning(
            pointcut = "execution(* com.restaurant.service.MesaService.guardar(..))",
            returning = "result"
    )
    public void auditarGuardarMesa(JoinPoint joinPoint, Object result) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] != null) {
                Object mesa = args[0];
                Long idMesa = (Long) mesa.getClass().getMethod("getIdMesa").invoke(mesa);
                Integer numero = (Integer) mesa.getClass().getMethod("getNumero").invoke(mesa);

                String accion = (idMesa == null)
                        ? "CREAR Mesa #" + numero
                        : "ACTUALIZAR Mesa #" + numero;

                registrarBitacora(accion, "Mesa",
                        (Long) result.getClass().getMethod("getIdMesa").invoke(result));
            }
        } catch (Exception e) {
            log.error("Error en auditoría de Mesa", e);
        }
    }

    /**
     * Auditar operaciones de eliminación en MesaService
     */
    @AfterReturning("execution(* com.restaurant.service.MesaService.eliminar(..))")
    public void auditarEliminarMesa(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                Long idMesa = (Long) args[0];
                registrarBitacora("ELIMINAR Mesa", "Mesa", idMesa);
            }
        } catch (Exception e) {
            log.error("Error en auditoría de eliminación de Mesa", e);
        }
    }

    /**
     * ✅ SOLUCIÓN: Método auxiliar con transacción independiente
     * que NO causa dependencias circulares durante la autenticación
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void registrarBitacora(String accion, String entidad, Long idEntidad) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            // ✅ CRÍTICO: Verificaciones para evitar ciclos infinitos
            if (auth == null || !auth.isAuthenticated()) {
                log.debug("No hay autenticación activa, saltando auditoría");
                return;
            }

            // ✅ Evitar auditar durante el proceso de autenticación
            if ("anonymousUser".equals(auth.getPrincipal())) {
                log.debug("Usuario anónimo, saltando auditoría");
                return;
            }

            // ✅ Evitar auditar si el principal no es un String (aún no está completamente autenticado)
            if (!(auth.getPrincipal() instanceof String)) {
                log.debug("Autenticación en progreso, saltando auditoría");
                return;
            }

            String nombreUsuario = auth.getName();
            if (nombreUsuario == null || nombreUsuario.isBlank()) {
                log.debug("Nombre de usuario vacío, saltando auditoría");
                return;
            }

            // ✅ Buscar usuario SIN disparar proxies adicionales
            Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario).orElse(null);

            if (usuario != null) {
                Bitacora bitacora = new Bitacora();
                bitacora.setUsuario(usuario);
                bitacora.setAccion(accion);
                bitacora.setEntidad(entidad);
                bitacora.setIdEntidad(idEntidad);
                bitacora.setFechaHora(LocalDateTime.now());

                bitacoraRepository.save(bitacora);
                log.debug("✅ Auditoría registrada: {} - {}", nombreUsuario, accion);
            } else {
                log.warn("Usuario no encontrado para auditoría: {}", nombreUsuario);
            }

        } catch (Exception e) {
            // ✅ IMPORTANTE: Solo log, NO lanzar excepción
            log.error("Error al registrar auditoría (no crítico): {}", e.getMessage());
        }
    }
}