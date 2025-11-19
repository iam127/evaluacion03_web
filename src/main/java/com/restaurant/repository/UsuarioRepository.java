package com.restaurant.repository;

import com.restaurant.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    boolean existsByNombreUsuario(String nombreUsuario);

    List<Usuario> findByEstadoTrue();

    List<Usuario> findByRol(String rol);

    @Query("SELECT u FROM Usuario u WHERE u.estado = true AND u.rol = :rol")
    List<Usuario> findActiveUsersByRole(@Param("rol") String rol);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol = :rol")
    long countByRole(@Param("rol") String rol);
}