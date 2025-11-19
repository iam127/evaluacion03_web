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
                String nombres = (String) cliente.getClass().getMethod("getNombres").invoke(cliente);
                String apellidos = (String) cliente.getClass().getMethod("getApellidos").invoke(cliente);

                String accion = (idCliente == null)
                        ? "CREAR Cliente: " + nombres + " " + apellidos
                        : "ACTUALIZAR Cliente: " + nombres + " " + apellidos;

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
     * Auditar cambios de estado en MesaService
     */
    @AfterReturning("execution(* com.restaurant.service.MesaService.cambiarEstado(..))")
    public void auditarCambiarEstadoMesa(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length >= 2) {
                Long idMesa = (Long) args[0];
                Object estado = args[1];
                registrarBitacora("CAMBIAR ESTADO Mesa a: " + estado, "Mesa", idMesa);
            }
        } catch (Exception e) {
            log.error("Error en auditoría de cambio de estado de Mesa", e);
        }
    }

    /**
     * Auditar ocupación de mesa
     */
    @AfterReturning("execution(* com.restaurant.service.MesaService.ocuparMesa(..))")
    public void auditarOcuparMesa(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                Long idMesa = (Long) args[0];
                registrarBitacora("OCUPAR Mesa", "Mesa", idMesa);
            }
        } catch (Exception e) {
            log.error("Error en auditoría de ocupación de Mesa", e);
        }
    }

    /**
     * Auditar liberación de mesa
     */
    @AfterReturning("execution(* com.restaurant.service.MesaService.liberarMesa(..))")
    public void auditarLiberarMesa(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                Long idMesa = (Long) args[0];
                registrarBitacora("LIBERAR Mesa", "Mesa", idMesa);
            }
        } catch (Exception e) {
            log.error("Error en auditoría de liberación de Mesa", e);
        }
    }

    /**
     * Auditar reserva de mesa
     */
    @AfterReturning("execution(* com.restaurant.service.MesaService.reservarMesa(..))")
    public void auditarReservarMesa(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                Long idMesa = (Long) args[0];
                registrarBitacora("RESERVAR Mesa", "Mesa", idMesa);
            }
        } catch (Exception e) {
            log.error("Error en auditoría de reserva de Mesa", e);
        }
    }

    /**
     * Método auxiliar para registrar en bitácora
     */
    private void registrarBitacora(String accion, String entidad, Long idEntidad) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                String nombreUsuario = auth.getName();

                usuarioRepository.findByNombreUsuario(nombreUsuario).ifPresent(usuario -> {
                    Bitacora bitacora = new Bitacora();
                    bitacora.setUsuario(usuario);
                    bitacora.setAccion(accion);
                    bitacora.setEntidad(entidad);
                    bitacora.setIdEntidad(idEntidad);
                    bitacora.setFechaHora(LocalDateTime.now());

                    bitacoraRepository.save(bitacora);
                    log.info("Auditoría registrada: {} - {}", nombreUsuario, accion);
                });
            }
        } catch (Exception e) {
            log.error("Error al registrar en bitácora", e);
        }
    }
}