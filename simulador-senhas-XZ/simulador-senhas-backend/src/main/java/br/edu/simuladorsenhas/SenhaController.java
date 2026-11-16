package br.edu.simuladorsenhas;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500"})
public class SenhaController {
    private final CalculadoraSeguranca calculadora;
    private final ClassificadorSenha classificador;

    public SenhaController(CalculadoraSeguranca calculadora, ClassificadorSenha classificador) {
        this.calculadora = calculadora;
        this.classificador = classificador;
    }

    @PostMapping("/api/analisar")
    public ResultadoAnalise analisar(@RequestBody EntradaAnalise entrada) {
        if (entrada.senha() == null || !entrada.senha().isTextual()) {
            throw new IllegalArgumentException("A senha deve ser texto.");
        }
        Senha senha = new Senha(entrada.senha().textValue());
        BigInteger combinacoes = calculadora.calcularCombinacoes(senha);
        BigDecimal tempo = calculadora.calcularTempoMaximo(combinacoes);
        return new ResultadoAnalise(classificador.classificar(tempo), senha,
                calculadora.formatarCombinacoes(combinacoes), calculadora.formatarTempo(tempo));
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> entradaInvalida() {
        return Map.of("erro", "Envie um JSON com o campo senha contendo de 1 a 128 caracteres.");
    }

    public record EntradaAnalise(JsonNode senha) {
        @Override
        public String toString() {
            return "EntradaAnalise[conteudo omitido]";
        }
    }
}
