package br.edu.simuladorsenhas;

import java.util.Map;

/**
 * Guarda os dados da analise que serao enviados para a interface.
 */
public class ResultadoAnalise {
    private final String nivel;
    private final int comprimento;
    private final Map<String, Boolean> tipos;
    private final int tamanhoAlfabeto;
    private final String combinacoes;
    private final String tempoEstimado;

    /**
     * Monta o resultado usando os dados da senha e os valores calculados.
     *
     * @param nivel classificacao da senha
     * @param senha senha analisada
     * @param combinacoes total de combinacoes ja formatado
     * @param tempoEstimado tempo de forca bruta ja formatado
     */
    public ResultadoAnalise(String nivel, Senha senha, String combinacoes, String tempoEstimado) {
        this.nivel = nivel;
        this.comprimento = senha.getComprimento();
        this.tipos = senha.getTipos();
        this.tamanhoAlfabeto = senha.getTamanhoAlfabeto();
        this.combinacoes = combinacoes;
        this.tempoEstimado = tempoEstimado;
    }

    /**
     * @return classificacao da senha
     */
    public String getNivel() { return nivel; }
    /**
     * @return quantidade de caracteres da senha
     */
    public int getComprimento() { return comprimento; }
    /**
     * @return tipos de caracteres encontrados
     */
    public Map<String, Boolean> getTipos() { return tipos; }
    /**
     * @return tamanho do alfabeto considerado
     */
    public int getTamanhoAlfabeto() { return tamanhoAlfabeto; }
    /**
     * @return quantidade de combinacoes formatada
     */
    public String getCombinacoes() { return combinacoes; }
    /**
     * @return tempo estimado formatado
     */
    public String getTempoEstimado() { return tempoEstimado; }
}
