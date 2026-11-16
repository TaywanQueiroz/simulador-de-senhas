package br.edu.simuladorsenhas;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

@Service
public class ClassificadorSenha {
    public String classificar(BigDecimal segundos) {
        if (segundos == null || segundos.signum() < 0) {
            throw new IllegalArgumentException("O tempo não pode ser negativo.");
        }
        if (segundos.compareTo(BigDecimal.valueOf(3600)) < 0) return "MUITO_FRACO";
        if (segundos.compareTo(BigDecimal.valueOf(86400)) < 0) return "FRACO";
        if (segundos.compareTo(BigDecimal.valueOf(31536000)) < 0) return "MEDIO";
        if (segundos.compareTo(BigDecimal.valueOf(3153600000L)) < 0) return "FORTE";
        return "MUITO_FORTE";
    }
}
