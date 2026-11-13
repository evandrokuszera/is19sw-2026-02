package utfpr.api_frete.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import utfpr.api_frete.dtos.FreteRequestDTO;
import utfpr.api_frete.dtos.FreteResponseDTO;
import utfpr.api_frete.dtos.ViaCepResponseDTO;
import utfpr.api_frete.service.FreteService;

@RestController
@RequestMapping("/fretes")
public class FreteController {

    private FreteService freteService;

    // CRIANDO OBJETO RESTCLIENT
    private RestClient restClient = RestClient.create("https://viacep.com.br/ws/");

    public FreteController(FreteService freteService) {
        this.freteService = freteService;
    }

    @PostMapping
    public ResponseEntity<FreteResponseDTO> calcularFrete(@RequestBody FreteRequestDTO freteRequestDTO){

        // CHAMANDO A BUSCA POR CEP DA VIACEP
        ViaCepResponseDTO viaCepResponseDTO = getViaCEP(freteRequestDTO.cep());

        if (viaCepResponseDTO.erro() != null && viaCepResponseDTO.erro()){
            return ResponseEntity.notFound().build();
        }

        FreteResponseDTO freteResponseDTO = this.freteService.
                calcularFrete(
                        viaCepResponseDTO.uf(),
                        viaCepResponseDTO.localidade(),
                        freteRequestDTO.orderAmount()
                );

        return ResponseEntity.ok(freteResponseDTO);
    }



    // NOVO MÉTODO PARA CHAMAR API VIACEP
    private ViaCepResponseDTO getViaCEP(String cep) {
        return restClient.get()
                .uri(cep + "/json/")
                .retrieve()
                .body(ViaCepResponseDTO.class);
    }

}
