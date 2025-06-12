package com.example.construagro;

public class Relatorio {
    private String nomeRelatorio;
    private String cadastroPor;
    private String dataInicio;
    private String dataFim;

    public Relatorio() {} // Construtor vazio obrigatório para Firestore

    public Relatorio(String nomeRelatorio, String cadastroPor, String dataInicio, String dataFim) {
        this.nomeRelatorio = nomeRelatorio;
        this.cadastroPor = cadastroPor;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public String getNomeRelatorio() {
        return nomeRelatorio;
    }

    public String getCadastroPor() {
        return cadastroPor;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public String getDataFim() {
        return dataFim;
    }
}
