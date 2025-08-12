package com.ecommerce.pedido.controlles;

import com.ecommerce.pedido.models.Cliente;
import com.ecommerce.pedido.models.Produto;
import com.ecommerce.pedido.repositorys.ProdutoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;


    //CONTRUTOR
    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }


    //ENDPOINTS

    @GetMapping
    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id){
        return produtoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/criar")
    public Produto criar(@RequestBody Produto produto){
        return produtoRepository.save(produto);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody Produto produto){
        return produtoRepository.findById(id)
                .map(produtoExistente -> {
                    BeanUtils.copyProperties(produto, produtoExistente, "id");
                    Produto atualizado = produtoRepository.save(produtoExistente);
                    return ResponseEntity.ok(atualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id){
        produtoRepository.findById(id)
                .map(produto ->{
                    produtoRepository.delete(produto);
                        return ResponseEntity.noContent().build();

                        });
        return ResponseEntity.notFound().build();
    }

}
