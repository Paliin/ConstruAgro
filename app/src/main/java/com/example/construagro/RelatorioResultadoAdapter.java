package com.example.construagro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RelatorioResultadoAdapter extends RecyclerView.Adapter<RelatorioResultadoAdapter.ViewHolder> {

    private List<Relatorio> relatorios;
    private Context context;

    public RelatorioResultadoAdapter(List<Relatorio> relatorios, Context context) {
        this.relatorios = relatorios;
        this.context = context;
    }

    public void atualizarLista(List<Relatorio> novaLista) {
        relatorios = novaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RelatorioResultadoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_relatorio, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatorioResultadoAdapter.ViewHolder holder, int position) {
        Relatorio relatorio = relatorios.get(position);
        holder.textNomeRelatorio.setText(relatorio.getNomeRelatorio());
        holder.textCadastroPor.setText("Cadastrado por: " + relatorio.getCadastroPor());
        holder.textPeriodo.setText(relatorio.getDataInicio() + " - " + relatorio.getDataFim());

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Visualizar/Exportar relatório: " + relatorio.getNomeRelatorio(), Toast.LENGTH_SHORT).show();
            // Aqui você pode abrir o relatório ou oferecer exportação para PDF
        });
    }

    @Override
    public int getItemCount() {
        return relatorios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textNomeRelatorio, textCadastroPor, textPeriodo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textNomeRelatorio = itemView.findViewById(R.id.textNomeRelatorio);
            textCadastroPor = itemView.findViewById(R.id.textCadastroPor);
            textPeriodo = itemView.findViewById(R.id.textPeriodo);
        }
    }
}
