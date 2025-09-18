package com.ecommerce.pedido.controlles;

import com.ecommerce.pedido.models.Restaurante;
import com.ecommerce.pedido.repositories.RestauranteRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {

    private final RestauranteRepository restauranteRepository;


    //CONTRUTOR
    public RestauranteController(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }


    //ENDPOINTS

    @GetMapping
    public List<Restaurante> listar() {
        return restauranteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurante> buscarPorId(@PathVariable Long id){
        return restauranteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/criar")
    public Restaurante criar(@RequestBody Restaurante restaurante){
        return restauranteRepository.save(restaurante);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Restaurante> atualizar(@PathVariable Long id, @RequestBody Restaurante restaurante){
        return restauranteRepository.findById(id)
                .map(restauranteExistente -> {
                    BeanUtils.copyProperties(restaurante, restauranteExistente, "id");
                    Restaurante atualizado = restauranteRepository.save(restauranteExistente);
                    return ResponseEntity.ok(atualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id){
        restauranteRepository.findById(id)
                .map(restaurante ->{
                    restauranteRepository.delete(restaurante);
                        return ResponseEntity.noContent().build();

                        });
        return ResponseEntity.notFound().build();
    }

}
