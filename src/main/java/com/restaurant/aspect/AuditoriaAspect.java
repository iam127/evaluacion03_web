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
     * IMPORTANTE: Solo intercepta métodos específicos del service, no todo
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
     * Método auxiliar para registrar en bitácora
     * CORREGIDO: Evita consultas durante la autenticación
     */
    private void registrarBitacora(String accion, String entidad, Long idEntidad) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            // Verificar que el usuario está autenticado Y que no es anónimo
            if (auth != null && auth.isAuthenticated()
                    && !"anonymousUser".equals(auth.getPrincipal())
                    && auth.getName() != null) {

                String nombreUsuario = auth.getName();

                // IMPORTANTE: Usar una transacción separada para evitar problemas
                try {
                    Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario).orElse(null);

                    if (usuario != null) {
                        Bitacora bitacora = new Bitacora();
                        bitacora.setUsuario(usuario);
                        bitacora.setAccion(accion);
                        bitacora.setEntidad(entidad);
                        bitacora.setIdEntidad(idEntidad);
                        bitacora.setFechaHora(LocalDateTime.now());

                        bitacoraRepository.save(bitacora);
                        log.debug("Auditoría registrada: {} - {}", nombreUsuario, accion);
                    }
                } catch (Exception e) {
                    // Solo log, no lanzar excepción para no afectar la operación principal
                    log.warn("No se pudo registrar auditoría: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error al registrar en bitácora", e);
        }
    }
}