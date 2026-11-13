package utfpr.api_frete.service;

import org.springframework.stereotype.Service;
import utfpr.api_frete.dtos.FreteResponseDTO;

@Service
public class FreteService {

    public FreteResponseDTO calcularFrete(String estado, String cidade, Double valorPedido){

        int numeroDiasEntrega = this.getNumeroDiasEntrega(estado);

        double valorFrete = this.getValorFrete(estado);

        valorFrete = this.aplicarDescontoFrete(valorFrete, valorPedido);

        return new FreteResponseDTO(
                valorFrete, numeroDiasEntrega, cidade, estado
        );

    }

    // --------------------------------------------------------------------------------------
    // Retorna número de dias para entrega, conforme Estado
    // --------------------------------------------------------------------------------------
    private int getNumeroDiasEntrega(String estado){
        switch (estado) {
            case "PR": case "SC":case "RS": return 5;
            case "SP": case "RJ":case "MG": return 10;
            default: return 15;
        }
    }

    // --------------------------------------------------------------------------------------
    // Retorna o valor do frete, conforme Estado
    // --------------------------------------------------------------------------------------
    public int getValorFrete(String estado){
        switch (estado) {
            case "PR": case "SC":case "RS": return 50;
            case "SP": case "RJ":case "MG": return 100;
            default: return 200;
        }
    }

    // --------------------------------------------------------------------------------------
    // Retorna o portentagem de desconto do frete, conforme valor do pedido
    // --------------------------------------------------------------------------------------
    public double aplicarDescontoFrete(Double valorFrete, Double valorPedido){
        double desconto = 0;
        if (valorPedido > 500){
            desconto = 100;
        } else if (valorPedido > 300){
            desconto = 50;
        }
        return valorFrete - (valorFrete * (desconto / 100));
    }

}
