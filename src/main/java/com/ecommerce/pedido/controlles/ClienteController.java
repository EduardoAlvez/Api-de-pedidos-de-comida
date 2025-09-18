package com.ecommerce.pedido.controlles;

import com.ecommerce.pedido.models.Cliente;
import com.ecommerce.pedido.repositories.ClienteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;


    //CONTRUTOR
    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }


    //ENDPOINTS

    @GetMapping
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id){
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/criar")
    public Cliente criar(@RequestBody Cliente cliente){
        return clienteRepository.save(cliente);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @RequestBody Cliente cliente){
        return clienteRepository.findById(id)
                .map(clienteExistente -> {
                    BeanUtils.copyProperties(cliente, clienteExistente, "id");
                    Cliente atualizado = clienteRepository.save(clienteExistente);
                    return ResponseEntity.ok(atualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id){
        clienteRepository.findById(id)
                .map(cliente ->{
                        clienteRepository.delete(cliente);
                        return ResponseEntity.noContent().build();

                        });
        return ResponseEntity.notFound().build();
    }

}
