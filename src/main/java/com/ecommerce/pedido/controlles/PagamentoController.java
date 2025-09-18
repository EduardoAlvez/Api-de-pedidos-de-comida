package com.ecommerce.pedido.controlles;

import com.ecommerce.pedido.models.Pagamento;
import com.ecommerce.pedido.repositories.PagamentoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoRepository pagamentoRepository;


    //CONTRUTOR
    public PagamentoController(PagamentoRepository pagamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
    }


    //ENDPOINTS

    @GetMapping
    public List<Pagamento> listar() {
        return pagamentoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pagamento> buscarPorId(@PathVariable Long id){
        return pagamentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/criar")
    public Pagamento criar(@RequestBody Pagamento pagamento){
        return pagamentoRepository.save(pagamento);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Pagamento> atualizar(@PathVariable Long id, @RequestBody Pagamento pagamento){
        return pagamentoRepository.findById(id)
                .map(pagamentoExistente -> {
                    BeanUtils.copyProperties(pagamento, pagamentoExistente, "id");
                    Pagamento atualizado = pagamentoRepository.save(pagamentoExistente);
                    return ResponseEntity.ok(atualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id){
        pagamentoRepository.findById(id)
                .map(pagamento ->{
                    pagamentoRepository.delete(pagamento);
                        return ResponseEntity.noContent().build();

                        });
        return ResponseEntity.notFound().build();
    }

}
