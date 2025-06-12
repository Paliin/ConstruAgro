package com.example.construagro;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class Tela_CriarRelatorio extends AppCompatActivity {

    private Spinner spinnerTipoRelatorio, spinnerNomeFiltro;
    private EditText editTextDataInicio, editTextDataFim, editTextCadastradoPorFiltro;
    private Button buttonGerarRelatorio, buttonExportarPDF;
    private RecyclerView recyclerViewResultadoRelatorio;
    private RelatorioResultadoAdapter adapter;

    private ArrayList<Produto> resultadosProdutos = new ArrayList<>();
    private ArrayList<Saida> resultadosSaidas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_criar_relatorio);

        spinnerTipoRelatorio = findViewById(R.id.spinnerTipoRelatorio);
        spinnerNomeFiltro = findViewById(R.id.spinnerNomeFiltro);
        editTextDataInicio = findViewById(R.id.editTextDataInicio);
        editTextDataFim = findViewById(R.id.editTextDataFim);
        editTextCadastradoPorFiltro = findViewById(R.id.editTextCadastradoPorFiltro);
        buttonGerarRelatorio = findViewById(R.id.buttonGerarRelatorio);
        buttonExportarPDF = findViewById(R.id.buttonExportarPDF);
        recyclerViewResultadoRelatorio = findViewById(R.id.recyclerViewResultadoRelatorio);

        adapter = new RelatorioResultadoAdapter(this);
        recyclerViewResultadoRelatorio.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewResultadoRelatorio.setAdapter(adapter);

        // Configura os spinners
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Itens Cadastrados", "Saídas de Produtos"});
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoRelatorio.setAdapter(tipoAdapter);

        ArrayAdapter<String> nomeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"", "Areia", "Cimento", "Tijolo"});
        nomeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNomeFiltro.setAdapter(nomeAdapter);

        setupDatePicker(editTextDataInicio);
        setupDatePicker(editTextDataFim);

        buttonGerarRelatorio.setOnClickListener(v -> gerarRelatorio());
        buttonExportarPDF.setOnClickListener(v -> exportarPDF());
    }

    private void setupDatePicker(EditText campo) {
        campo.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int ano = calendar.get(Calendar.YEAR);
            int mes = calendar.get(Calendar.MONTH);
            int dia = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (DatePicker view, int year, int month, int dayOfMonth) ->
                            campo.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)),
                    ano, mes, dia);
            datePickerDialog.show();
        });
    }

    private void gerarRelatorio() {
        String tipo = spinnerTipoRelatorio.getSelectedItem().toString();
        String nomeSelecionado = spinnerNomeFiltro.getSelectedItem().toString();
        String dataInicio = editTextDataInicio.getText().toString();
        String dataFim = editTextDataFim.getText().toString();
        String cadastradoPor = editTextCadastradoPorFiltro.getText().toString().trim();

        if (tipo.equals("Itens Cadastrados")) {
            resultadosProdutos.clear();
            FirebaseDatabase.getInstance().getReference("produtos")
                    .orderByChild("nome")
                    .startAt(nomeSelecionado)
                    .endAt(nomeSelecionado + "\uf8ff")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Produto produto = ds.getValue(Produto.class);
                                if (produto != null) {
                                    resultadosProdutos.add(produto);
                                }
                            }
                            Toast.makeText(Tela_CriarRelatorio.this,
                                    "Relatório gerado com " + resultadosProdutos.size() + " produtos.",
                                    Toast.LENGTH_SHORT).show();
                            adapter.atualizarListaProdutos(resultadosProdutos);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Tela_CriarRelatorio.this,
                                    "Erro ao gerar relatório", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else if (tipo.equals("Saídas de Produtos")) {
            resultadosSaidas.clear();
            FirebaseDatabase.getInstance().getReference("saidas")
                    .orderByChild("nome")
                    .startAt(nomeSelecionado)
                    .endAt(nomeSelecionado + "\uf8ff")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Saida saida = ds.getValue(Saida.class);
                                if (saida != null) {
                                    resultadosSaidas.add(saida);
                                }
                            }
                            Toast.makeText(Tela_CriarRelatorio.this,
                                    "Relatório gerado com " + resultadosSaidas.size() + " saídas.",
                                    Toast.LENGTH_SHORT).show();
                            adapter.atualizarListaSaidas(resultadosSaidas);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Tela_CriarRelatorio.this,
                                    "Erro ao gerar relatório", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void exportarPDF() {
        Toast.makeText(this, "Exportação para PDF em desenvolvimento", Toast.LENGTH_SHORT).show();
    }
}
