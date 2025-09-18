package com.ecommerce.pedido.controlles;

import com.ecommerce.pedido.models.Pedido;
import com.ecommerce.pedido.repositories.PedidoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;


    //CONTRUTOR
    public PedidoController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }


    //ENDPOINTS

    @GetMapping
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id){
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/criar")
    public Pedido criar(@RequestBody Pedido pedido){
        return pedidoRepository.save(pedido);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Pedido> atualizar(@PathVariable Long id, @RequestBody Pedido pedido){
        return pedidoRepository.findById(id)
                .map(pedidoExistente -> {
                    BeanUtils.copyProperties(pedido, pedidoExistente, "id");
                    Pedido atualizado = pedidoRepository.save(pedidoExistente);
                    return ResponseEntity.ok(atualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id){
        pedidoRepository.findById(id)
                .map(pedido ->{
                    pedidoRepository.delete(pedido);
                        return ResponseEntity.noContent().build();

                        });
        return ResponseEntity.notFound().build();
    }

}
