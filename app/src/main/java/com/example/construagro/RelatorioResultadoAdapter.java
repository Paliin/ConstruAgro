package com.example.construagro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RelatorioResultadoAdapter extends RecyclerView.Adapter<RelatorioResultadoAdapter.ViewHolder> {

    private ArrayList<Produto> listaProdutos = new ArrayList<>();
    private ArrayList<Saida> listaSaidas = new ArrayList<>();
    private boolean mostrandoProdutos = true;  // controla o tipo atual mostrado
    private Context context;

    public RelatorioResultadoAdapter(Context context) {
        this.context = context;
    }

    // Atualiza a lista de produtos e notifica
    public void atualizarListaProdutos(ArrayList<Produto> produtos) {
        this.listaProdutos = produtos;
        mostrandoProdutos = true;
        notifyDataSetChanged();
    }

    // Atualiza a lista de saídas e notifica
    public void atualizarListaSaidas(ArrayList<Saida> saidas) {
        this.listaSaidas = saidas;
        mostrandoProdutos = false;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (mostrandoProdutos) {
            view = LayoutInflater.from(context).inflate(R.layout.item_produto_relatorio, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_saida_relatorio, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mostrandoProdutos) {
            Produto produto = listaProdutos.get(position);
            // Exemplo de campos do produto, ajuste conforme sua classe Produto
            holder.textTitulo.setText(produto.getNome());
            holder.textDetalhes.setText("Marca: " + produto.getMarca() + " | Valor: R$ " + produto.getValor());
            holder.textExtras.setText("Cadastrado por: " + produto.getCadastroPor());
            holder.itemView.setOnClickListener(v -> Toast.makeText(context,
                    "Produto: " + produto.getNome(), Toast.LENGTH_SHORT).show());
        } else {
            Saida saida = listaSaidas.get(position);
            // Exemplo de campos da saída, ajuste conforme sua classe Saida
            holder.textTitulo.setText(saida.getNome());
            holder.textDetalhes.setText("Quantidade: " + saida.getQuantidade() + " | Data: " + saida.getData());
            holder.textExtras.setText("Solicitado por: " + saida.getSolicitadoPor());
            holder.itemView.setOnClickListener(v -> Toast.makeText(context,
                    "Saída: " + saida.getNome(), Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public int getItemCount() {
        return mostrandoProdutos ? listaProdutos.size() : listaSaidas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitulo, textDetalhes, textExtras;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitulo = itemView.findViewById(R.id.textTitulo);
            textDetalhes = itemView.findViewById(R.id.textDetalhes);
            textExtras = itemView.findViewById(R.id.textExtras);
        }
    }
}
