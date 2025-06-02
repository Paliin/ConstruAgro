package com.example.construagro;

public class Produto {
    private String nome;
    private int quantidade;
    private String categoria;

    // Construtor vazio exigido pelo Firebase
    public Produto() {}

    public Produto(String nome, int quantidade, String categoria) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.categoria = categoria;
    }

    // Getters e setters (necessários para Firebase)

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}
