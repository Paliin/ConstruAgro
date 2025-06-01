package com.example.construagro;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class Tela_Menu extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private MeuAdapter adapter;
    private List<String> itens;

    // Menu flutuante
    private LinearLayout layoutMenuRapido;
    private TextView opcaoCadastrar, opcaoSaida, opcaoRelatorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_menu);

        // Corrige cor da status bar ANTES do ajuste de insets
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.DarkBlue));
        }

        // Ajuste de padding para EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom); // REMOVE padding top
            return insets;
        });

        // Referências dos componentes
        searchView = findViewById(R.id.Text_Pesquisar_Itens);
        recyclerView = findViewById(R.id.recycler_view_itens);
        ImageButton fab = findViewById(R.id.Button_Menu_Rapido);

        // Referências do menu flutuante
        layoutMenuRapido = findViewById(R.id.layout_menu_popup);
        opcaoCadastrar = findViewById(R.id.opcao_cadastrar);
        opcaoSaida = findViewById(R.id.opcao_saida);
        opcaoRelatorio = findViewById(R.id.opcao_relatorio);

        // Toggle do menu ao clicar no botão
        fab = findViewById(R.id.Button_Menu_Rapido);
        LinearLayout layoutMenuPopup = findViewById(R.id.layout_menu_popup);

// Alterna visibilidade ao clicar no botão
        ImageButton finalFab = fab;
        fab.setOnClickListener(v -> {
            if (layoutMenuPopup.getVisibility() == View.VISIBLE) {
                layoutMenuPopup.setVisibility(View.GONE);
            } else {
                // Mostra o layout primeiro (para que ele possa ser medido)
                layoutMenuPopup.setVisibility(View.VISIBLE);

                // Aguarda layout ser desenhado antes de posicionar
                layoutMenuPopup.post(() -> {
                    int[] fabLocation = new int[2];
                    finalFab.getLocationOnScreen(fabLocation);
                    int fabX = fabLocation[0];
                    int fabY = fabLocation[1];

                    int popupWidth = layoutMenuPopup.getWidth();
                    int popupHeight = layoutMenuPopup.getHeight();

                    // Margem de segurança nas bordas
                    int margin = 20;

                    // Calcula posição X: alinha à direita do botão, mas com margem da tela
                    float posX = fabX + finalFab.getWidth() - popupWidth;
                    if (posX < margin) posX = margin;

                    // Calcula posição Y: logo acima do botão
                    float posY = fabY - popupHeight - margin;
                    if (posY < margin) posY = margin;

                    layoutMenuPopup.setX(posX);
                    layoutMenuPopup.setY(posY);
                });
            }
        });




// Ações dos botões do popup
        TextView opcaoCadastrar = findViewById(R.id.opcao_cadastrar);
        TextView opcaoSaida = findViewById(R.id.opcao_saida);
        TextView opcaoRelatorio = findViewById(R.id.opcao_relatorio);

        opcaoCadastrar.setOnClickListener(v -> {
            layoutMenuPopup.setVisibility(View.GONE);
            // TODO: abrir tela de cadastro
        });

        opcaoSaida.setOnClickListener(v -> {
            layoutMenuPopup.setVisibility(View.GONE);
            // TODO: abrir tela de saída
        });

        opcaoRelatorio.setOnClickListener(v -> {
            layoutMenuPopup.setVisibility(View.GONE);
            // TODO: abrir tela de relatório
        });


        searchView.setIconifiedByDefault(false); // Expandido
        searchView.setQueryHint("Pesquisar itens...");

        // Lista de teste
        itens = Arrays.asList(
                "Prego", "Balde", "Cano PVC", "Fertilizante",
                "Mangueira", "Parafuso", "Trator", "Tubo"
        );

        // RecyclerView config
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MeuAdapter(itens);
        recyclerView.setAdapter(adapter);

        // Filtragem ao digitar
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }

            @Override public boolean onQueryTextChange(String newText) {
                filtrarLista(newText);
                return true;
            }
        });
    }

    private void filtrarLista(String texto) {
        if (texto == null || texto.isEmpty()) {
            adapter = new MeuAdapter(itens);
        } else {
            String txtLower = texto.toLowerCase();
            List<String> filtrados = new java.util.ArrayList<>();
            for (String item : itens) {
                if (item.toLowerCase().contains(txtLower)) {
                    filtrados.add(item);
                }
            }
            adapter = new MeuAdapter(filtrados);
        }
        recyclerView.setAdapter(adapter);
    }
}
