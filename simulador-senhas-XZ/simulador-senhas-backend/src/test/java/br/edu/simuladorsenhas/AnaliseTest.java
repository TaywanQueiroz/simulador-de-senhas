package br.edu.simuladorsenhas;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class AnaliseTest {
    private final CalculadoraSeguranca calculadora = new CalculadoraSeguranca();
    private final ClassificadorSenha classificador = new ClassificadorSenha();

    @ParameterizedTest
    @CsvSource({
        "123456, 6, 10, 1000000, MUITO_FRACO",
        "senha123, 8, 36, 2821109907456, MEDIO",
        "T4x@zQ9!, 8, 94, 6095689385410816, MUITO_FORTE",
        "Abc, 3, 52, 140608, MUITO_FRACO"
    })
    void exemplosDoFrontend(String texto, int comprimento, int alfabeto, String total, String nivel) {
        Senha senha = new Senha(texto);
        BigInteger combinacoes = calculadora.calcularCombinacoes(senha);
        assertEquals(comprimento, senha.getComprimento());
        assertEquals(alfabeto, senha.getTamanhoAlfabeto());
        assertEquals(new BigInteger(total), combinacoes);
        assertEquals(nivel, classificador.classificar(calculadora.calcularTempoMaximo(combinacoes)));
    }

    @ParameterizedTest
    @CsvSource({
        "0, MUITO_FRACO", "3599.999999, MUITO_FRACO", "3600, FRACO",
        "86399.999999, FRACO", "86400, MEDIO", "31535999.999999, MEDIO",
        "31536000, FORTE", "3153599999.999999, FORTE", "3153600000, MUITO_FORTE"
    })
    void limitesDeClassificacao(String segundos, String nivel) {
        assertEquals(nivel, classificador.classificar(new BigDecimal(segundos)));
    }

    @Test
    void preservaPrecisaoETiposSemGuardarSenha() {
        Senha senha = new Senha("Aa1!");
        assertTrue(senha.getTipos().values().stream().allMatch(Boolean::booleanValue));
        assertThrows(UnsupportedOperationException.class, () -> senha.getTipos().put("digitos", false));
        assertEquals(0, new BigDecimal("0.000001").compareTo(calculadora.calcularTempoMaximo(BigInteger.ONE)));
        assertEquals(0, new BigDecimal("0.0000005").compareTo(calculadora.calcularTempoMedio(BigInteger.ONE)));
        assertEquals(BigInteger.valueOf(94).pow(128), calculadora.calcularCombinacoes(new Senha("Aa1!".repeat(32))));
    }

    @Test
    void formatoEmPortugues() {
        assertEquals("1.000.000", calculadora.formatarCombinacoes(new BigInteger("1000000")));
        assertEquals("2,82 × 10^12", calculadora.formatarCombinacoes(new BigInteger("2821109907456")));
        assertEquals("menos de 1 segundo", calculadora.formatarTempo(new BigDecimal("0.5")));
        assertEquals("1 segundo", calculadora.formatarTempo(BigDecimal.ONE));
        assertEquals("≈ 1 minuto", calculadora.formatarTempo(BigDecimal.valueOf(60)));
        assertEquals("≈ 1 hora", calculadora.formatarTempo(BigDecimal.valueOf(3600)));
        assertEquals("≈ 1 dia", calculadora.formatarTempo(BigDecimal.valueOf(86400)));
        assertEquals("≈ 1 ano", calculadora.formatarTempo(BigDecimal.valueOf(31536000)));
        assertEquals("≈ 32,7 dias", calculadora.formatarTempo(new BigDecimal("2821109.907456")));
        assertEquals("mais de 1 quintilhão de anos", calculadora.formatarTempo(new BigDecimal("1e100")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "a"})
    void validaLimitesDaSenha(String texto) {
        String invalida = texto.isEmpty() ? texto : texto.repeat(129);
        assertThrows(IllegalArgumentException.class, () -> new Senha(invalida));
    }

    @Test
    void rejeitaValoresInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new Senha(null));
        assertThrows(IllegalArgumentException.class, () -> calculadora.calcularTempoMaximo(BigInteger.ZERO));
        assertThrows(IllegalArgumentException.class, () -> calculadora.formatarTempo(BigDecimal.valueOf(-1)));
        assertThrows(IllegalArgumentException.class, () -> classificador.classificar(BigDecimal.valueOf(-1)));
    }
}
