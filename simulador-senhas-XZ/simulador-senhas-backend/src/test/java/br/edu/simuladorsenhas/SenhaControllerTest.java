package br.edu.simuladorsenhas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SenhaControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void respondeContratoDoFrontend() throws Exception {
        mvc.perform(post("/api/analisar").contentType(MediaType.APPLICATION_JSON)
                .content("{\"senha\":\"senha123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nivel").value("MEDIO"))
                .andExpect(jsonPath("$.comprimento").value(8))
                .andExpect(jsonPath("$.tamanhoAlfabeto").value(36))
                .andExpect(jsonPath("$.combinacoes").value("2,82 × 10^12"))
                .andExpect(jsonPath("$.tempoEstimado").value("≈ 32,7 dias"))
                .andExpect(jsonPath("$.tipos.digitos").value(true))
                .andExpect(jsonPath("$.tipos.minusculas").value(true))
                .andExpect(jsonPath("$.tipos.maiusculas").value(false))
                .andExpect(jsonPath("$.tipos.simbolos").value(false))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "null", "{", "{\"senha\":null}", "{\"senha\":\"\"}",
            "{\"senha\":123}", "{\"senha\":true}", "{\"senha\":[]}", "{\"senha\":\"abc\"} {}"})
    void rejeitaEntradaInvalidaSemExporSenha(String corpo) throws Exception {
        mvc.perform(post("/api/analisar").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"erro\":\"Envie um JSON com o campo senha contendo de 1 a 128 caracteres.\"}"));
    }

    @Test
    void limitaComprimento() throws Exception {
        for (int tamanho : new int[] {128, 129}) {
            mvc.perform(post("/api/analisar").contentType(MediaType.APPLICATION_JSON)
                    .content("{\"senha\":\"" + "a".repeat(tamanho) + "\"}"))
                    .andExpect(status().is(tamanho == 128 ? 200 : 400));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"http://localhost:5500", "http://127.0.0.1:5500"})
    void permiteLiveServer(String origem) throws Exception {
        mvc.perform(options("/api/analisar").header("Origin", origem)
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", origem));
    }

    @Test
    void rejeitaOutraOrigem() throws Exception {
        mvc.perform(options("/api/analisar").header("Origin", "https://outra-origem.example")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }
}
