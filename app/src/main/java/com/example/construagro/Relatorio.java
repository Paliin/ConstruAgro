package com.example.construagro;

public class Relatorio {
    private String id;
    private String produto;
    private String dataInicio;
    private String dataFim;
    private String criadoPor;
    private String dataCriacao;

    public Relatorio() { }

    public Relatorio(String id, String produto, String dataInicio, String dataFim, String criadoPor, String dataCriacao) {
        this.id = id;
        this.produto = produto;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.criadoPor = criadoPor;
        this.dataCriacao = dataCriacao;
    }

    public String getId() { return id; }
    public String getProduto() { return produto; }
    public String getDataInicio() { return dataInicio; }
    public String getDataFim() { return dataFim; }
    public String getCriadoPor() { return criadoPor; }
    public String getDataCriacao() { return dataCriacao; }

    public void setId(String id) { this.id = id; }
    public void setProduto(String produto) { this.produto = produto; }
    public void setDataInicio(String dataInicio) { this.dataInicio = dataInicio; }
    public void setDataFim(String dataFim) { this.dataFim = dataFim; }
    public void setCriadoPor(String criadoPor) { this.criadoPor = criadoPor; }
    public void setDataCriacao(String dataCriacao) { this.dataCriacao = dataCriacao; }
}
