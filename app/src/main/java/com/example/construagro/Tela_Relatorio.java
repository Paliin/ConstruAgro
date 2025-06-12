package com.example.construagro;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Tela_Relatorio extends AppCompatActivity {

    private EditText edtNomeFiltro, edtCadastroPorFiltro, edtDataInicioFiltro, edtDataFimFiltro;
    private Button btnFiltrar, btnNovoRelatorio, btnExportarPdf;
    private RecyclerView recyclerView;
    private RelatorioResultadoAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_relatorio);

        // Toolbar com botão de voltar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Referências dos componentes
        edtNomeFiltro = findViewById(R.id.edtNomeFiltro);
        edtCadastroPorFiltro = findViewById(R.id.edtCadastroPorFiltro);
        edtDataInicioFiltro = findViewById(R.id.edtDataInicioFiltro);
        edtDataFimFiltro = findViewById(R.id.edtDataFimFiltro);

        btnFiltrar = findViewById(R.id.btnFiltrar);
        btnNovoRelatorio = findViewById(R.id.btnNovoRelatorio);
        btnExportarPdf = findViewById(R.id.btnExportarPdf);

        recyclerView = findViewById(R.id.recyclerViewRelatorios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RelatorioResultadoAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        carregarRelatorios("");  // Carrega todos inicialmente

        btnFiltrar.setOnClickListener(v -> aplicarFiltros());

        btnNovoRelatorio.setOnClickListener(v -> {
            // Abrir tela para criar novo relatório
            Intent intent = new Intent(this, Tela_CriarRelatorio.class);
            startActivity(intent);
        });

        btnExportarPdf.setOnClickListener(v -> {
            // TODO: implementar exportar PDF
            Toast.makeText(this, "Função exportar PDF em desenvolvimento", Toast.LENGTH_SHORT).show();
        });

        ImageButton botaoVoltar = findViewById(R.id.botao_voltar);
        botaoVoltar.setOnClickListener(v -> finish());

        // Configura DatePickers para os campos de data
        edtDataInicioFiltro.setOnClickListener(v -> abrirDatePicker(edtDataInicioFiltro));
        edtDataFimFiltro.setOnClickListener(v -> abrirDatePicker(edtDataFimFiltro));
    }

    private void aplicarFiltros() {
        String nomeFiltro = edtNomeFiltro.getText().toString().trim();
        String cadastroPorFiltro = edtCadastroPorFiltro.getText().toString().trim();
        String dataInicioFiltro = edtDataInicioFiltro.getText().toString().trim();
        String dataFimFiltro = edtDataFimFiltro.getText().toString().trim();

        carregarRelatoriosComFiltros(nomeFiltro, cadastroPorFiltro, dataInicioFiltro, dataFimFiltro);
    }

    private void carregarRelatorios(String nomeFiltro) {
        db.collection("relatorios")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Relatorio> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Relatorio r = doc.toObject(Relatorio.class);
                        if (TextUtils.isEmpty(nomeFiltro) || r.getNomeRelatorio().toLowerCase().contains(nomeFiltro.toLowerCase())) {
                            lista.add(r);
                        }
                    }
                    adapter.atualizarLista(lista);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao carregar relatórios", Toast.LENGTH_SHORT).show());
    }

    private void carregarRelatoriosComFiltros(String nomeFiltro, String cadastroPorFiltro, String dataInicioFiltro, String dataFimFiltro) {
        db.collection("relatorios")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Relatorio> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Relatorio r = doc.toObject(Relatorio.class);

                        boolean ok = true;

                        if (!TextUtils.isEmpty(nomeFiltro) && !r.getNomeRelatorio().toLowerCase().contains(nomeFiltro.toLowerCase())) {
                            ok = false;
                        }

                        if (!TextUtils.isEmpty(cadastroPorFiltro) && !r.getCadastroPor().toLowerCase().contains(cadastroPorFiltro.toLowerCase())) {
                            ok = false;
                        }

                        if (!TextUtils.isEmpty(dataInicioFiltro) && r.getDataInicio() != null && r.getDataInicio().compareTo(dataInicioFiltro) < 0) {
                            ok = false;
                        }

                        if (!TextUtils.isEmpty(dataFimFiltro) && r.getDataFim() != null && r.getDataFim().compareTo(dataFimFiltro) > 0) {
                            ok = false;
                        }

                        if (ok) {
                            lista.add(r);
                        }
                    }
                    adapter.atualizarLista(lista);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao carregar relatórios com filtros", Toast.LENGTH_SHORT).show());
    }

    private void abrirDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String dataFormatada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    editText.setText(dataFormatada);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }
}
