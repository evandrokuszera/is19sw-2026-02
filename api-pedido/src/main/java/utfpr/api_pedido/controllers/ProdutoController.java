package utfpr.api_pedido.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utfpr.api_pedido.dto.ProdutoDTO;
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
    public ResponseEntity<List<ProdutoDTO>> getAll(){
        List<ProdutoDTO> produtoDTOs = new ArrayList<>();
        for (Produto p : this.produtos){
            produtoDTOs.add( toProdutoDTO(p) );
        }
        return ResponseEntity.ok(produtoDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> getOne(@PathVariable Long id){
        for (Produto p : this.produtos){
            if (p.getId() == id){
               return ResponseEntity.ok( toProdutoDTO(p) );
            }
        }
        return  ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<String> add(@Valid @RequestBody ProdutoDTO produtoDTO){
        if (produtoDTO.description() == "" || produtoDTO.price() < 0){
            return ResponseEntity.badRequest().body("Descrição ou preço inválidos!");
        }
        this.produtos.add( toProduto(produtoDTO) );

        String msg = """
                {"msg": "Produto cadastrado com sucesso."}
                """;

        return ResponseEntity.created(null).body(msg);
    }

    @PutMapping(path="/{id}")
    public ResponseEntity<String> update(@PathVariable(name="id") Long idProduto, @Valid @RequestBody ProdutoDTO produtoDTO) {
        for (Produto p : this.produtos){
            if (p.getId().equals(idProduto)){
                p.setCategory(produtoDTO.category());
                p.setDescription(produtoDTO.description());
                p.setQuantity(produtoDTO.quantity());
                p.setPrice(produtoDTO.price());
                return ResponseEntity.ok("Produto atualizado com sucesso.");
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
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
    }

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------

    // Cria um ProdutoDTO a partir de um Produto
    private ProdutoDTO toProdutoDTO(Produto produto){
        ProdutoDTO produtoDTO = new ProdutoDTO(
                produto.getId(),
                produto.getDescription(),
                produto.getQuantity(),
                produto.getPrice(),
                produto.getCategory()
        );

        return produtoDTO;
    }

    // Cria um Produto a partir de um ProdutoDTO
    private Produto toProduto(ProdutoDTO dto){
        Produto produto = new Produto(
                dto.id(),
                dto.description(),
                dto.quantity(),
                dto.price(),
                dto.category()
        );
        return produto;
    }

}
