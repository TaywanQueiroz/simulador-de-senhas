package br.edu.simuladorsenhas;

import java.util.Map;

public class Senha {
    private final int comprimento;
    private final boolean digitos;
    private final boolean minusculas;
    private final boolean maiusculas;
    private final boolean simbolos;

    public Senha(String valor) {
        if (valor == null || valor.isEmpty() || valor.length() > 128) {
            throw new IllegalArgumentException("A senha deve ter entre 1 e 128 caracteres.");
        }
        comprimento = valor.length();
        digitos = valor.chars().anyMatch(c -> c >= '0' && c <= '9');
        minusculas = valor.chars().anyMatch(c -> c >= 'a' && c <= 'z');
        maiusculas = valor.chars().anyMatch(c -> c >= 'A' && c <= 'Z');
        simbolos = valor.chars().anyMatch(c -> !(c >= '0' && c <= '9')
                && !(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z'));
    }

    public int getComprimento() {
        return comprimento;
    }

    public int getTamanhoAlfabeto() {
        return (digitos ? 10 : 0) + (minusculas ? 26 : 0)
                + (maiusculas ? 26 : 0) + (simbolos ? 32 : 0);
    }

    public Map<String, Boolean> getTipos() {
        return Map.of("digitos", digitos, "minusculas", minusculas,
                "maiusculas", maiusculas, "simbolos", simbolos);
    }
}
