package utfpr.api_pedido.controllers;

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
    public List<Produto> getAll(){
        return this.produtos;
    }

    @GetMapping("/{id}")
    public Produto getOne(@PathVariable Long id){
        for (Produto p : this.produtos){
            if (p.getId() == id){
               return p;
            }
        }
        return null;
    }

    @PostMapping
    public String add(@RequestBody Produto produto){
        if (produto.getDescription() == "" || produto.getPrice() < 0){
            return "Descrição ou preço inválidos!";
        }
        this.produtos.add(produto);
        return "Produto cadastrado com sucesso.";
    }

}
