package com.restaurant.repository;

import com.restaurant.model.Bitacora;
import com.restaurant.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BitacoraRepository extends JpaRepository<Bitacora, Long> {

    // Buscar por usuario
    List<Bitacora> findByUsuarioOrderByFechaHoraDesc(Usuario usuario);

    // Buscar por entidad
    List<Bitacora> findByEntidadOrderByFechaHoraDesc(String entidad);

    // Buscar por entidad y ID de entidad
    List<Bitacora> findByEntidadAndIdEntidadOrderByFechaHoraDesc(String entidad, Long idEntidad);

    // Buscar por rango de fechas
    List<Bitacora> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime inicio, LocalDateTime fin);

    // Obtener últimas acciones
    @Query("SELECT b FROM Bitacora b ORDER BY b.fechaHora DESC")
    List<Bitacora> findUltimasAcciones();
}