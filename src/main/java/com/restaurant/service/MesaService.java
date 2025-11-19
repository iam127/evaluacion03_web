package com.restaurant.service;

import com.restaurant.model.Mesa;
import com.restaurant.repository.MesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MesaService {

    private final MesaRepository mesaRepository;

    @Transactional(readOnly = true)
    public List<Mesa> listarTodas() {
        return mesaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Mesa> listarPorEstado(String estado) {
        return mesaRepository.findByEstadoOrderByNumeroMesaAsc(estado);
    }

    @Transactional(readOnly = true)
    public List<Mesa> listarDisponibles() {
        return mesaRepository.findByEstadoOrderByNumeroMesaAsc("DISPONIBLE");
    }

    @Transactional(readOnly = true)
    public Optional<Mesa> buscarPorId(Long id) {
        return mesaRepository.findById(id);
    }

    @Transactional
    public Mesa guardar(Mesa mesa) {
        return mesaRepository.save(mesa);
    }

    @Transactional
    public void eliminar(Long id) {
        mesaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long contarPorEstado(String estado) {
        return mesaRepository.countByEstado(estado);
    }

    @Transactional(readOnly = true)
    public long contarDisponibles() {
        return mesaRepository.countByEstado("DISPONIBLE");
    }

    @Transactional(readOnly = true)
    public long contarOcupadas() {
        return mesaRepository.countByEstado("OCUPADA");
    }

    @Transactional(readOnly = true)
    public long contarReservadas() {
        return mesaRepository.countByEstado("RESERVADA");
    }
}