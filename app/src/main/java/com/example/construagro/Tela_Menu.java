package com.example.construagro;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

public class Tela_Menu extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProdutoAdapter adapter;
    private List<Produto> listaProdutos;          // Lista exibida no adapter
    private List<Produto> listaProdutosOriginal;  // Lista original com todos os produtos carregados
    private DatabaseReference produtosRef;

    private LinearLayout layoutMenuRapido;
    private TextView opcaoCadastrar, opcaoSaida, opcaoRelatorio;

    private EditText filtroCategoria, filtroValorMax, dataInicio, dataFim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_menu);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.DarkBlue));
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referências do layout
        searchView = findViewById(R.id.Text_Pesquisar_Itens);
        recyclerView = findViewById(R.id.recycler_view_itens);
        filtroCategoria = findViewById(R.id.filtro_categoria);
        filtroValorMax = findViewById(R.id.filtro_valor_max);
        dataInicio = findViewById(R.id.data_inicio);
        dataFim = findViewById(R.id.data_fim);

        ImageButton fab = findViewById(R.id.Button_Menu_Rapido);

        layoutMenuRapido = findViewById(R.id.layout_menu_popup);
        opcaoCadastrar = findViewById(R.id.opcao_cadastrar);
        opcaoSaida = findViewById(R.id.opcao_saida);
        opcaoRelatorio = findViewById(R.id.opcao_relatorio);

        // Inicializa as listas
        listaProdutosOriginal = new ArrayList<>();
        listaProdutos = new ArrayList<>();
        adapter = new ProdutoAdapter(listaProdutos);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Firebase
        produtosRef = FirebaseDatabase.getInstance().getReference("produtos");
        carregarProdutosFirebase();

        // DatePickers para filtro de datas
        dataInicio.setFocusable(false);
        dataInicio.setClickable(true);
        dataFim.setFocusable(false);
        dataFim.setClickable(true);

        dataInicio.setOnClickListener(v -> abrirDatePicker(dataInicio));
        dataFim.setOnClickListener(v -> abrirDatePicker(dataFim));

        // Configuração do SearchView
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Pesquisar itens...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.trim().isEmpty()) {
                    // Texto vazio: mostra lista completa original sem filtros
                    listaProdutos.clear();
                    listaProdutos.addAll(listaProdutosOriginal);
                    adapter.notifyDataSetChanged();
                } else {
                    aplicarFiltros();
                }
                return true;
            }
        });

        TextWatcher filtroTextWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                aplicarFiltros();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        filtroCategoria.addTextChangedListener(filtroTextWatcher);
        filtroValorMax.addTextChangedListener(filtroTextWatcher);

        // FAB - botão do menu rápido
        fab.setOnClickListener(v -> {
            if (layoutMenuRapido.getVisibility() == View.VISIBLE) {
                layoutMenuRapido.setVisibility(View.GONE);
            } else {
                layoutMenuRapido.setVisibility(View.VISIBLE);
                layoutMenuRapido.post(() -> {
                    int[] fabLocation = new int[2];
                    fab.getLocationOnScreen(fabLocation);
                    int fabX = fabLocation[0];
                    int fabY = fabLocation[1];

                    int popupWidth = layoutMenuRapido.getWidth();
                    int popupHeight = layoutMenuRapido.getHeight();
                    int margin = 20;

                    float posX = fabX + fab.getWidth() - popupWidth;
                    if (posX < margin) posX = margin;

                    float posY = fabY - popupHeight - margin;
                    if (posY < margin) posY = margin;

                    layoutMenuRapido.setX(posX);
                    layoutMenuRapido.setY(posY);
                });
            }
        });

        // Ações dos botões do menu rápido
        opcaoCadastrar.setOnClickListener(v -> {
            layoutMenuRapido.setVisibility(View.GONE);
            startActivity(new Intent(Tela_Menu.this, CadastroProdutoActivity.class));
        });

        opcaoSaida.setOnClickListener(v -> {
            layoutMenuRapido.setVisibility(View.GONE);
            Intent intent = new Intent(Tela_Menu.this, SaidaProdutoActivity.class);
            startActivity(intent);
        });

        opcaoRelatorio.setOnClickListener(v -> {
            layoutMenuRapido.setVisibility(View.GONE);
            Toast.makeText(this, "Relatórios - em desenvolvimento", Toast.LENGTH_SHORT).show();
        });
    }

    private void carregarProdutosFirebase() {
        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listaProdutosOriginal.clear();
                for (DataSnapshot dado : snapshot.getChildren()) {
                    Produto produto = dado.getValue(Produto.class);
                    listaProdutosOriginal.add(produto);
                }
                // Atualiza lista exibida e adapter
                listaProdutos.clear();
                listaProdutos.addAll(listaProdutosOriginal);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Tela_Menu.this, "Erro ao carregar produtos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aplicarFiltros() {
        String textoBusca = searchView.getQuery() != null ? searchView.getQuery().toString().toLowerCase() : "";
        String categoriaFiltro = filtroCategoria.getText().toString().toLowerCase();
        String valorMaxStr = filtroValorMax.getText().toString();
        String dataInicioStr = dataInicio.getText().toString();
        String dataFimStr = dataFim.getText().toString();

        Double valorMax = parseDouble(valorMaxStr);
        Long dataInicioMillis = dataToMillis(dataInicioStr);
        Long dataFimMillis = dataToMillis(dataFimStr);

        List<Produto> filtrada = new ArrayList<>();
        for (Produto p : listaProdutosOriginal) {
            if (filtrarPorNome(p, textoBusca) &&
                    filtrarPorCategoria(p, categoriaFiltro) &&
                    filtrarPorValor(p, valorMax) &&
                    filtrarPorData(p, dataInicioMillis, dataFimMillis)) {
                filtrada.add(p);
            }
        }
        listaProdutos.clear();
        listaProdutos.addAll(filtrada);
        adapter.notifyDataSetChanged();
    }

    private boolean filtrarPorNome(Produto p, String textoBusca) {
        return p.getNome().toLowerCase().contains(textoBusca);
    }

    private boolean filtrarPorCategoria(Produto p, String categoriaFiltro) {
        return categoriaFiltro.isEmpty() || p.getCategoria().toLowerCase().contains(categoriaFiltro);
    }

    private boolean filtrarPorValor(Produto p, Double valorMax) {
        return valorMax == null || p.getValor() <= valorMax;
    }

    private boolean filtrarPorData(Produto p, Long dataInicioMillis, Long dataFimMillis) {
        if (dataInicioMillis == null && dataFimMillis == null) return true;

        Long dataProdutoMillis = dataToMillis(p.getDataCadastro());
        if (dataProdutoMillis == null) return false;

        if (dataInicioMillis != null && dataProdutoMillis < dataInicioMillis) return false;
        if (dataFimMillis != null && dataProdutoMillis > dataFimMillis) return false;

        return true;
    }

    private Double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long dataToMillis(String dataStr) {
        if (dataStr == null || dataStr.isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.parse(dataStr).getTime();
        } catch (Exception e) {
            return null;
        }
    }

    private void abrirDatePicker(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String dataFormatada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    editText.setText(dataFormatada);
                    aplicarFiltros();
                }, ano, mes, dia);
        datePickerDialog.show();
    }
}
