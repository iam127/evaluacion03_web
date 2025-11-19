package com.restaurant.service;

import com.restaurant.model.Cliente;
import com.restaurant.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarActivos() {
        return clienteRepository.findByEstadoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorDni(String dni) {
        return clienteRepository.findByDni(dni);
    }

    @Transactional(readOnly = true)
    public List<Cliente> buscar(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return listarActivos();
        }
        return clienteRepository.buscarPorTermino(termino.trim());
    }

    @Transactional
    public Cliente guardar(Cliente cliente) {
        // Validar DNI único
        if (cliente.getIdCliente() == null) {
            if (clienteRepository.existsByDni(cliente.getDni())) {
                throw new RuntimeException("Ya existe un cliente con el DNI: " + cliente.getDni());
            }
        } else {
            Optional<Cliente> existente = clienteRepository.findByDni(cliente.getDni());
            if (existente.isPresent() && !existente.get().getIdCliente().equals(cliente.getIdCliente())) {
                throw new RuntimeException("Ya existe otro cliente con el DNI: " + cliente.getDni());
            }
        }

        return clienteRepository.save(cliente);
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));

        // Desactivar en lugar de eliminar (eliminación lógica)
        cliente.setEstado(false);
        clienteRepository.save(cliente);
    }

    @Transactional
    public void activar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));

        cliente.setEstado(true);
        clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public long contarActivos() {
        return clienteRepository.countByEstadoTrue();
    }

    @Transactional(readOnly = true)
    public boolean existePorDni(String dni) {
        return clienteRepository.existsByDni(dni);
    }
}