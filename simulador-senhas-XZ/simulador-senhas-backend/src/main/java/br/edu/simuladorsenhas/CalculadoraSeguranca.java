package br.edu.simuladorsenhas;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.stereotype.Service;

/**
 * Faz os calculos de combinacoes e tempo estimado para testar senhas.
 */
@Service
public class CalculadoraSeguranca {
    private final BigDecimal tentativasPorSegundo = new BigDecimal("1000000");

    /**
     * Calcula quantas combinacoes sao possiveis com o alfabeto da senha.
     *
     * @param senha senha que sera analisada
     * @return total de combinacoes (N elevado a L)
     */
    public BigInteger calcularCombinacoes(Senha senha) {
        return BigInteger.valueOf(senha.getTamanhoAlfabeto()).pow(senha.getComprimento());
    }

    /**
     * Calcula o tempo para testar todas as combinacoes, em segundos.
     *
     * @param combinacoes quantidade de combinacoes possiveis
     * @return tempo maximo em segundos
     * @throws IllegalArgumentException se o valor for nulo ou nao for positivo
     */
    public BigDecimal calcularTempoMaximo(BigInteger combinacoes) {
        if (combinacoes == null || combinacoes.signum() <= 0) {
            throw new IllegalArgumentException("As combinações devem ser positivas.");
        }
        return new BigDecimal(combinacoes).divide(tentativasPorSegundo);
    }

    /**
     * Calcula o tempo medio, considerando metade das tentativas.
     *
     * @param combinacoes quantidade de combinacoes possiveis
     * @return tempo medio em segundos
     */
    public BigDecimal calcularTempoMedio(BigInteger combinacoes) {
        return calcularTempoMaximo(combinacoes).divide(BigDecimal.valueOf(2));
    }

    /**
     * Deixa o total de combinacoes em um formato mais facil de ler.
     *
     * @param combinacoes quantidade de combinacoes
     * @return numero formatado ou em notacao cientifica
     */
    public String formatarCombinacoes(BigInteger combinacoes) {
        String texto = combinacoes.toString();
        if (texto.length() <= 12) {
            return NumberFormat.getIntegerInstance(Locale.forLanguageTag("pt-BR")).format(combinacoes);
        }
        // Usa o mesmo truncamento do frontend para manter a exibição das combinações.
        return texto.charAt(0) + "," + texto.substring(1, 3) + " × 10^" + (texto.length() - 1);
    }

    /**
     * Converte o tempo em segundos para uma unidade mais adequada.
     *
     * @param segundos tempo calculado em segundos
     * @return tempo formatado em segundos, minutos, horas, dias ou anos
     * @throws IllegalArgumentException se o tempo for nulo ou negativo
     */
    public String formatarTempo(BigDecimal segundos) {
        if (segundos == null || segundos.signum() < 0) {
            throw new IllegalArgumentException("O tempo não pode ser negativo.");
        }
        if (segundos.compareTo(BigDecimal.ONE) < 0) {
            return "menos de 1 segundo";
        }
        long[] unidades = {31536000, 86400, 3600, 60, 1};
        String[] nomes = {"ano", "dia", "hora", "minuto", "segundo"};
        for (int i = 0; i < unidades.length; i++) {
            if (segundos.compareTo(BigDecimal.valueOf(unidades[i])) >= 0) {
                BigDecimal quantidade = segundos.divide(BigDecimal.valueOf(unidades[i]), 10, RoundingMode.HALF_UP);
                if (i == 0 && quantidade.compareTo(new BigDecimal("1000000000000000000")) >= 0) {
                    return "mais de 1 quintilhão de anos";
                }
                BigDecimal arredondada = quantidade.setScale(1, RoundingMode.HALF_UP);
                NumberFormat formato = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"));
                formato.setMaximumFractionDigits(1);
                String nome = nomes[i] + (arredondada.compareTo(BigDecimal.ONE) == 0 ? "" : "s");
                return (i == 4 ? "" : "≈ ") + formato.format(arredondada) + " " + nome;
            }
        }
        throw new IllegalStateException("Não foi possível formatar o tempo.");
    }
}
