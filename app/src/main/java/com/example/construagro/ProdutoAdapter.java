package com.example.construagro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProdutoAdapter extends RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder> {

    private List<Produto> listaProdutos;

    public ProdutoAdapter(List<Produto> listaProdutos) {
        this.listaProdutos = listaProdutos;
    }

    @NonNull
    @Override
    public ProdutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Cria um layout simples em tempo de execução
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ProdutoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdutoViewHolder holder, int position) {
        Produto produto = listaProdutos.get(position);

        holder.titulo.setText(produto.getNome());

        String detalhes = "Qtd: " + produto.getQuantidade()
                + " | Ctg: " + produto.getCategoria()
                + " | R$: " + produto.getValor()
                + " | " + produto.getDataCadastro();

        holder.subtitulo.setText(detalhes);
    }

    @Override
    public int getItemCount() {
        return listaProdutos.size();
    }

    public static class ProdutoViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, subtitulo;

        public ProdutoViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(android.R.id.text1);
            subtitulo = itemView.findViewById(android.R.id.text2);
        }
    }

    public void atualizarLista(List<Produto> novaLista) {
        this.listaProdutos.clear();
        this.listaProdutos.addAll(novaLista);
        notifyDataSetChanged();
    }

}
