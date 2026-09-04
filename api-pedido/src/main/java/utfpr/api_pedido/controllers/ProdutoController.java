package utfpr.api_pedido.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utfpr.api_pedido.models.Produto;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/produto")
public class ProdutoController {

    private List<Produto> produtos;

    public ProdutoController(){
        this.produtos = new ArrayList<>();
        this.produtos.add(new Produto(1L, "IPhone 15", 10, 5000.0, "Smartphone"));
        this.produtos.add(new Produto(2L, "Airpods Pro", 50, 200.0, "Acessórios"));
        this.produtos.add(new Produto(3L, "Notebook i7", 10, 7000.0, "Computadores"));
        this.produtos.add(new Produto(4L, "Cadeira Gamer", 5, 1000.0, "Móveis"));
    }


    @GetMapping
    public ResponseEntity<List<Produto>> getAll(){
        return ResponseEntity.ok(this.produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> getOne(@PathVariable Long id){
        for (Produto p : this.produtos){
            if (p.getId() == id){
               return ResponseEntity.ok(p);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping
    public ResponseEntity<String> add(@RequestBody Produto produto){
        if (produto.getDescription() == "" || produto.getPrice() < 0){
            return ResponseEntity.badRequest().body("Descrição ou preço inválidos!");
        }
        this.produtos.add(produto);
        return ResponseEntity.created(null).body("Produto cadastrado com sucesso.");
    }

    @PutMapping(path="/{id}")
    public ResponseEntity<String> update(@PathVariable(name="id") Long idProduto, @RequestBody Produto produto) {
        for (Produto p : this.produtos){
            if (p.getId().equals(idProduto)){
                p.setCategory(produto.getCategory());
                p.setDescription(produto.getDescription());
                p.setQuantity(produto.getQuantity());
                p.setPrice(produto.getPrice());
                String msg = """                        
                        {"msg": "Produto cadastrado com sucesso"}                        
                        """;
                return ResponseEntity.ok(msg);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> delete(@PathVariable(name="id") Long idProduto){
        Produto produtoRemover = this.produtos.stream()
                .filter(p->p.getId().equals(idProduto))
                .findFirst()
                .orElse(null);
        if (produtoRemover != null){
            this.produtos.remove(produtoRemover);
            return ResponseEntity.ok("Produto removido com sucesso.");
        }
        return ResponseEntity.notFound().build();
    }

}
