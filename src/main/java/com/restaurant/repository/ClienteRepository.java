package com.restaurant.repository;

import com.restaurant.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Buscar clientes activos
    List<Cliente> findByEstadoTrue();

    // Buscar por DNI
    Optional<Cliente> findByDni(String dni);

    // Verificar si existe un DNI
    boolean existsByDni(String dni);

    // Buscar por email
    Optional<Cliente> findByEmail(String email);

    // Verificar si existe un email
    boolean existsByEmail(String email);

    // Buscar por término (nombre, apellido o DNI)
    @Query("SELECT c FROM Cliente c WHERE " +
            "(LOWER(c.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "c.dni LIKE CONCAT('%', :termino, '%')) AND " +
            "c.estado = true")
    List<Cliente> buscarPorTermino(@Param("termino") String termino);

    // Contar clientes activos
    long countByEstadoTrue();
}