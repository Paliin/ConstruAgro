package com.example.construagro;

import java.util.ArrayList;
import java.util.List;

public class Produto {
    private String id;
    private String nome;
    private int quantidade;
    private String categoria;
    private double valor;
    private String dataCadastro;
    private String usuarioCadastro;
    private List<Movimentacao> historico;

    public Produto() {
        this.historico = new ArrayList<>();
    }

    public Produto(String nome, int quantidade, String categoria,
                   double valor, String dataCadastro, String usuarioCadastro) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.categoria = categoria;
        this.valor = valor;
        this.dataCadastro = dataCadastro;
        this.usuarioCadastro = usuarioCadastro;
        this.historico = new ArrayList<>();

        this.historico.add(new Movimentacao(
                quantidade,
                "Cadastro inicial",
                usuarioCadastro,
                dataCadastro
        ));
    }

    public static class Movimentacao {
        private int quantidade;
        private String tipo;
        private String usuario;
        private String data;
        private String observacao;

        public Movimentacao() {}

        public Movimentacao(int quantidade, String tipo, String usuario, String data) {
            this.quantidade = quantidade;
            this.tipo = tipo;
            this.usuario = usuario;
            this.data = data;
            this.observacao = "";
        }

        public Movimentacao(int quantidade, String tipo, String usuario, String data, String observacao) {
            this.quantidade = quantidade;
            this.tipo = tipo;
            this.usuario = usuario;
            this.data = data;
            this.observacao = observacao;
        }

        public int getQuantidade() { return quantidade; }
        public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }

        public String getUsuario() { return usuario; }
        public void setUsuario(String usuario) { this.usuario = usuario; }

        public String getData() { return data; }
        public void setData(String data) { this.data = data; }

        public String getObservacao() { return observacao; }
        public void setObservacao(String observacao) { this.observacao = observacao; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(String dataCadastro) { this.dataCadastro = dataCadastro; }

    public String getUsuarioCadastro() { return usuarioCadastro; }
    public void setUsuarioCadastro(String usuarioCadastro) { this.usuarioCadastro = usuarioCadastro; }

    public List<Movimentacao> getHistorico() { return historico; }
    public void setHistorico(List<Movimentacao> historico) { this.historico = historico; }
}
