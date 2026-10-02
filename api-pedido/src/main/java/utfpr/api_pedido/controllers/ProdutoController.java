package utfpr.api_pedido.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utfpr.api_pedido.dto.ProdutoDTO;
import utfpr.api_pedido.models.Produto;
import utfpr.api_pedido.repositories.ProdutoRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/produto")
public class ProdutoController {

    private ProdutoRepository produtoRepository;

    private List<Produto> produtos;

    public ProdutoController(ProdutoRepository produtoRepository){

        this.produtoRepository = produtoRepository;

        this.produtos = new ArrayList<>();
        this.produtos.add(new Produto(1L, "IPhone 15", 10, 5000.0, "Smartphone"));
        this.produtos.add(new Produto(2L, "Airpods Pro", 50, 200.0, "Acessórios"));
        this.produtos.add(new Produto(3L, "Notebook i7", 10, 7000.0, "Computadores"));
        this.produtos.add(new Produto(4L, "Cadeira Gamer", 5, 1000.0, "Móveis"));
    }


    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> getAll(){
        List<ProdutoDTO> produtoDTOs = new ArrayList<>();
        for (Produto p : this.produtoRepository.findAll()){
            produtoDTOs.add( toProdutoDTO(p) );
        }
        return ResponseEntity.ok(produtoDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> getOne(@PathVariable Long id){
        Produto produtoFromDB = this.produtoRepository.findById(id).orElse(null);
        if (produtoFromDB == null){
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok( toProdutoDTO( produtoFromDB ) );
    }

    @PostMapping
    public ResponseEntity<ProdutoDTO> add(@Valid @RequestBody ProdutoDTO produtoDTO){
        Produto produtoFromDB = this.produtoRepository.save( toProduto(produtoDTO) );
        return ResponseEntity.created(null).body( toProdutoDTO(produtoFromDB) );
    }

    @PutMapping(path="/{id}")
    public ResponseEntity<ProdutoDTO> update(@PathVariable(name="id") Long idProduto, @Valid @RequestBody ProdutoDTO produtoDTO) {
        Produto produtoFromDB = this.produtoRepository.findById(idProduto).orElse(null);
        if (produtoFromDB == null) return ResponseEntity.notFound().build();

        produtoFromDB.setCategory(produtoDTO.category());
        produtoFromDB.setDescription(produtoDTO.description());
        produtoFromDB.setQuantity(produtoDTO.quantity());
        produtoFromDB.setPrice(produtoDTO.price());

        this.produtoRepository.save(produtoFromDB);

        return ResponseEntity.ok( toProdutoDTO( produtoFromDB ) );
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name="id") Long idProduto){

        Produto produtoFromDB = this.produtoRepository.findById(idProduto).orElse(null);
        if (produtoFromDB == null) return ResponseEntity.notFound().build();

        this.produtoRepository.delete(produtoFromDB);

        return ResponseEntity.noContent().build();
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
