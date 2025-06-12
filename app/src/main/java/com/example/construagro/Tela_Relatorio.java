package com.example.construagro;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Tela_Relatorio extends AppCompatActivity {

    private AutoCompleteTextView filtroProduto;
    private Button btnCriarRelatorio;
    ImageButton botaoVoltar;
    private RecyclerView recyclerRelatorios;

    private DatabaseReference databaseProdutos;
    private DatabaseReference databaseRelatorios;

    private ArrayAdapter<String> adapterProdutos;
    private List<String> listaNomesProdutos = new ArrayList<>();
    private List<Relatorio> listaRelatorios = new ArrayList<>();
    private RelatorioAdapter adapterRelatorios;

    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_relatorio);

        filtroProduto = findViewById(R.id.filtroProduto);
        btnCriarRelatorio = findViewById(R.id.btnCriarRelatorio);
        botaoVoltar = findViewById(R.id.btnVoltar);
        recyclerRelatorios = findViewById(R.id.recyclerRelatorios);

        databaseProdutos = FirebaseDatabase.getInstance().getReference("produtos");
        databaseRelatorios = FirebaseDatabase.getInstance().getReference("relatorios");

        adapterProdutos = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, listaNomesProdutos);
        filtroProduto.setAdapter(adapterProdutos);
        filtroProduto.setThreshold(1);

        recyclerRelatorios.setLayoutManager(new LinearLayoutManager(this));
        adapterRelatorios = new RelatorioAdapter(listaRelatorios);
        recyclerRelatorios.setAdapter(adapterRelatorios);

        carregarProdutos();
        carregarRelatorios();

        botaoVoltar.setOnClickListener(v -> finish());

        btnCriarRelatorio.setOnClickListener(v -> abrirDialogCriarRelatorio());
    }

    private void carregarProdutos() {
        databaseProdutos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaNomesProdutos.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Produto produto = data.getValue(Produto.class);
                    if (produto != null && produto.getNome() != null) {
                        listaNomesProdutos.add(produto.getNome());
                    }
                }
                adapterProdutos.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Tela_Relatorio.this, "Erro ao carregar produtos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirDialogCriarRelatorio() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_criar_relatorio, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AutoCompleteTextView autoProduto = dialogView.findViewById(R.id.autoProduto);
        EditText editDataInicio = dialogView.findViewById(R.id.editDataInicio);
        EditText editDataFim = dialogView.findViewById(R.id.editDataFim);
        EditText editCriadoPor = dialogView.findViewById(R.id.editCriadoPor);

        ArrayAdapter<String> adapterProdutoDialog = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, listaNomesProdutos);
        autoProduto.setAdapter(adapterProdutoDialog);

        editDataInicio.setOnClickListener(v -> mostrarDatePicker(editDataInicio));
        editDataFim.setOnClickListener(v -> mostrarDatePicker(editDataFim));

        builder.setTitle("Criar Relatório");
        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String produto = autoProduto.getText().toString().trim();
            String dataInicio = editDataInicio.getText().toString().trim();
            String dataFim = editDataFim.getText().toString().trim();
            String criadoPor = editCriadoPor.getText().toString().trim();

            if (produto.isEmpty() || dataInicio.isEmpty() || dataFim.isEmpty()) {
                Toast.makeText(this, "Produto e Período são obrigatórios", Toast.LENGTH_SHORT).show();
                return;
            }

            salvarRelatorio(produto, dataInicio, dataFim, criadoPor);
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void mostrarDatePicker(EditText campo) {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    campo.setText(formatoData.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    private void salvarRelatorio(String produto, String dataInicio, String dataFim, String criadoPor) {
        String id = databaseRelatorios.push().getKey();
        String dataCriacao = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date());

        Relatorio rel = new Relatorio(id, produto, dataInicio, dataFim, criadoPor, dataCriacao);

        if (id != null) {
            databaseRelatorios.child(id).setValue(rel)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Relatório criado!", Toast.LENGTH_SHORT).show();
                        carregarRelatorios();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Erro ao criar relatório", Toast.LENGTH_SHORT).show());
        }
    }

    private void carregarRelatorios() {
        databaseRelatorios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaRelatorios.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Relatorio rel = data.getValue(Relatorio.class);
                    if (rel != null) {
                        listaRelatorios.add(rel);
                    }
                }
                adapterRelatorios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Tela_Relatorio.this, "Erro ao carregar relatórios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Adapter
    private static class RelatorioAdapter extends RecyclerView.Adapter<RelatorioAdapter.RelatorioViewHolder> {

        private final List<Relatorio> relatorios;

        public RelatorioAdapter(List<Relatorio> relatorios) {
            this.relatorios = relatorios;
        }

        @NonNull
        @Override
        public RelatorioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new RelatorioViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RelatorioViewHolder holder, int position) {
            Relatorio rel = relatorios.get(position);
            holder.text1.setText("Produto: " + rel.getProduto() + " (" + rel.getDataInicio() + " até " + rel.getDataFim() + ")");
            String criadoPor = (rel.getCriadoPor() == null || rel.getCriadoPor().isEmpty()) ? "Desconhecido" : rel.getCriadoPor();
            holder.text2.setText("Criado por: " + criadoPor + " em " + rel.getDataCriacao());

            // Exibir Loading ou texto provisório enquanto busca movimentações
            holder.text2.append("\nCarregando dados do relatório...");

            DatabaseReference databaseMovimentacoes = FirebaseDatabase.getInstance().getReference("movimentacoes");
            databaseMovimentacoes.orderByChild("produto").equalTo(rel.getProduto())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int totalEntradas = 0;
                            int totalSaidas = 0;

                            // Converter datas do relatório para comparar
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            long dataInicioMs = 0, dataFimMs = 0;
                            try {
                                dataInicioMs = sdf.parse(rel.getDataInicio()).getTime();
                                dataFimMs = sdf.parse(rel.getDataFim()).getTime();
                            } catch (Exception e) {
                                // erro no parse - você pode tratar
                            }

                            for (DataSnapshot mov : snapshot.getChildren()) {
                                String tipo = mov.child("tipo").getValue(String.class);
                                String dataStr = mov.child("data").getValue(String.class);
                                Integer quantidade = mov.child("quantidade").getValue(Integer.class);

                                if (tipo == null || dataStr == null || quantidade == null) continue;

                                try {
                                    long dataMovMs = sdf.parse(dataStr).getTime();
                                    // Verifica se a movimentação está dentro do período do relatório
                                    if (dataMovMs >= dataInicioMs && dataMovMs <= dataFimMs) {
                                        if (tipo.equalsIgnoreCase("entrada")) {
                                            totalEntradas += quantidade;
                                        } else if (tipo.equalsIgnoreCase("saida")) {
                                            totalSaidas += quantidade;
                                        }
                                    }
                                } catch (Exception e) {
                                    // erro parse data movimentação
                                }
                            }

                            // Atualiza a UI com os dados somados
                            holder.text2.setText("Criado por: " + criadoPor + " em " + rel.getDataCriacao() +
                                    "\nTotal entradas: " + totalEntradas +
                                    "\nTotal saídas: " + totalSaidas);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            holder.text2.setText("Erro ao carregar dados do relatório.");
                        }
                    });
        }


        @Override
        public int getItemCount() {
            return relatorios.size();
        }

        static class RelatorioViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;

            public RelatorioViewHolder(@NonNull View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}
