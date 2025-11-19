package com.restaurant.repository;

import com.restaurant.model.Mesa;
import com.restaurant.model.Mesa.EstadoMesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    // Buscar mesa por número
    Optional<Mesa> findByNumero(Integer numero);

    // Verificar si existe una mesa con un número específico
    boolean existsByNumero(Integer numero);

    // Buscar mesas por estado
    List<Mesa> findByEstado(EstadoMesa estado);

    // Buscar mesas disponibles
    List<Mesa> findByEstadoOrderByNumeroAsc(EstadoMesa estado);

    // Contar mesas por estado
    long countByEstado(EstadoMesa estado);

    // Buscar mesas disponibles con capacidad mínima
    @Query("SELECT m FROM Mesa m WHERE m.estado = 'DISPONIBLE' AND m.capacidad >= :capacidad ORDER BY m.capacidad ASC")
    List<Mesa> findMesasDisponiblesPorCapacidad(Integer capacidad);

    // Obtener estadísticas de mesas
    @Query("SELECT m.estado, COUNT(m) FROM Mesa m GROUP BY m.estado")
    List<Object[]> obtenerEstadisticasMesas();
}