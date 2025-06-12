package com.example.construagro;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.view.ViewGroup;
import android.view.LayoutInflater;

public class SaidaProdutoActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextViewProduto;
    private TextView textViewQuantidadeAtual, textViewQuantidadeRestante;
    private EditText editTextQuantidadeSaida;
    private Button buttonAdicionarSaida, buttonConfirmarTodasSaidas;
    private RecyclerView recyclerViewSaidas;

    private DatabaseReference databaseRef;
    private List<Produto> listaProdutos = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private String usuarioAtual;
    private Produto produtoSelecionado;

    private List<Saida> listaSaidas = new ArrayList<>();
    private SaidaAdapter saidaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saida_produto);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        usuarioAtual = user != null ? user.getDisplayName() : "Administrador";

        databaseRef = FirebaseDatabase.getInstance().getReference("produtos");

        // Inicializa componentes
        autoCompleteTextViewProduto = findViewById(R.id.autoCompleteTextViewProduto);
        textViewQuantidadeAtual = findViewById(R.id.textViewQuantidadeAtual);
        textViewQuantidadeRestante = findViewById(R.id.textViewQuantidadeRestante);
        editTextQuantidadeSaida = findViewById(R.id.editTextQuantidadeSaida);
        buttonAdicionarSaida = findViewById(R.id.buttonAdicionarSaida);
        buttonConfirmarTodasSaidas = findViewById(R.id.buttonConfirmarTodasSaidas);
        recyclerViewSaidas = findViewById(R.id.recyclerViewSaidas);

        // Configura RecyclerView
        recyclerViewSaidas.setLayoutManager(new LinearLayoutManager(this));
        saidaAdapter = new SaidaAdapter(listaSaidas);
        recyclerViewSaidas.setAdapter(saidaAdapter);

        // Configura AutoCompleteTextView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextViewProduto.setAdapter(adapter);
        autoCompleteTextViewProduto.setThreshold(1);

        carregarProdutos();

        autoCompleteTextViewProduto.setOnItemClickListener((parent, view, position, id) -> {
            String nomeSelecionado = (String) parent.getItemAtPosition(position);
            for (Produto produto : listaProdutos) {
                if (produto.getNome().equals(nomeSelecionado)) {
                    produtoSelecionado = produto;
                    textViewQuantidadeAtual.setVisibility(View.VISIBLE);
                    textViewQuantidadeAtual.setText("Quantidade atual: " + produto.getQuantidade());
                    break;
                }
            }
        });

        editTextQuantidadeSaida.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (produtoSelecionado != null && !s.toString().isEmpty()) {
                    try {
                        int quantidadeSaida = Integer.parseInt(s.toString());
                        int quantidadeRestante = produtoSelecionado.getQuantidade() - quantidadeSaida;

                        textViewQuantidadeRestante.setVisibility(View.VISIBLE);
                        textViewQuantidadeRestante.setText("Quantidade restante: " + quantidadeRestante);

                        if (quantidadeRestante < 0) {
                            textViewQuantidadeRestante.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        } else {
                            textViewQuantidadeRestante.setTextColor(getResources().getColor(R.color.DarkBlue));
                        }
                    } catch (NumberFormatException e) {
                        textViewQuantidadeRestante.setVisibility(View.GONE);
                    }
                } else {
                    textViewQuantidadeRestante.setVisibility(View.GONE);
                }
            }
        });

        buttonAdicionarSaida.setOnClickListener(v -> adicionarSaidaLista());
        buttonConfirmarTodasSaidas.setOnClickListener(v -> confirmarTodasSaidas());
    }

    private void adicionarSaidaLista() {
        if (produtoSelecionado == null) {
            Toast.makeText(this, "Selecione um produto", Toast.LENGTH_SHORT).show();
            return;
        }

        String quantidadeStr = editTextQuantidadeSaida.getText().toString().trim();

        if (quantidadeStr.isEmpty()) {
            Toast.makeText(this, "Informe a quantidade", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantidadeSaida = Integer.parseInt(quantidadeStr);
        int quantidadeRestante = produtoSelecionado.getQuantidade() - quantidadeSaida;

        if (quantidadeSaida <= 0) {
            Toast.makeText(this, "Quantidade deve ser maior que zero", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantidadeRestante < 0) {
            Toast.makeText(this, "Quantidade indisponível", Toast.LENGTH_SHORT).show();
            return;
        }

        // Adiciona à lista de saídas
        Saida saida = new Saida(
                produtoSelecionado.getId(),
                produtoSelecionado.getNome(),
                quantidadeSaida,
                produtoSelecionado.getQuantidade(),
                quantidadeRestante
        );

        listaSaidas.add(saida);
        saidaAdapter.notifyDataSetChanged();

        // Atualiza UI
        recyclerViewSaidas.setVisibility(View.VISIBLE);
        buttonConfirmarTodasSaidas.setVisibility(View.VISIBLE);

        // Limpa campos para próxima saída
        autoCompleteTextViewProduto.setText("");
        editTextQuantidadeSaida.setText("");
        textViewQuantidadeAtual.setVisibility(View.GONE);
        textViewQuantidadeRestante.setVisibility(View.GONE);
        produtoSelecionado = null;
    }

    private void confirmarTodasSaidas() {
        String dataAtual = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

        for (Saida saida : listaSaidas) {
            DatabaseReference refProduto = databaseRef.child(saida.getIdProduto());

            refProduto.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Produto produto = snapshot.getValue(Produto.class);
                    if (produto != null) {
                        produto.setId(snapshot.getKey());
                        int novaQuantidade = produto.getQuantidade() - saida.getQuantidade();

                        Produto.Movimentacao movimentacao = new Produto.Movimentacao(
                                saida.getQuantidade(),
                                "Saída",
                                usuarioAtual,
                                dataAtual,
                                "Saída registrada via app"
                        );

                        produto.setQuantidade(novaQuantidade);
                        if (produto.getHistorico() == null) {
                            produto.setHistorico(new ArrayList<>());
                        }
                        produto.getHistorico().add(movimentacao);

                        refProduto.setValue(produto);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SaidaProdutoActivity.this,
                            "Erro ao salvar saída: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }


        Toast.makeText(this, "Saídas confirmadas!", Toast.LENGTH_SHORT).show();
        listaSaidas.clear();
        saidaAdapter.notifyDataSetChanged();
        recyclerViewSaidas.setVisibility(View.GONE);
        buttonConfirmarTodasSaidas.setVisibility(View.GONE);
    }


    private void carregarProdutos() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaProdutos.clear();
                List<String> nomesProdutos = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Produto produto = dataSnapshot.getValue(Produto.class);
                    if (produto != null) {
                        produto.setId(dataSnapshot.getKey());
                        listaProdutos.add(produto);
                        nomesProdutos.add(produto.getNome());
                    }
                }

                adapter.clear();
                adapter.addAll(nomesProdutos);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SaidaProdutoActivity.this,
                        "Erro ao carregar produtos: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

// Classe Saida (adicione como uma classe separada ou interna)
class Saida {
    private String idProduto;
    private String nomeProduto;
    private int quantidade;
    private int quantidadeAnterior;
    private int quantidadeRestante;

    public Saida() {}

    public Saida(String idProduto, String nomeProduto, int quantidade,
                 int quantidadeAnterior, int quantidadeRestante) {
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.quantidadeAnterior = quantidadeAnterior;
        this.quantidadeRestante = quantidadeRestante;
    }

    // Getters
    public String getIdProduto() { return idProduto; }
    public String getNomeProduto() { return nomeProduto; }
    public int getQuantidade() { return quantidade; }
    public int getQuantidadeAnterior() { return quantidadeAnterior; }
    public int getQuantidadeRestante() { return quantidadeRestante; }
}

// Classe SaidaAdapter (adicione como uma classe separada ou interna)
class SaidaAdapter extends RecyclerView.Adapter<SaidaAdapter.SaidaViewHolder> {
    private List<Saida> listaSaidas;

    public SaidaAdapter(List<Saida> listaSaidas) {
        this.listaSaidas = listaSaidas;
    }

    @NonNull
    @Override
    public SaidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saida, parent, false);
        return new SaidaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaidaViewHolder holder, int position) {
        Saida saida = listaSaidas.get(position);
        holder.textViewProduto.setText(saida.getNomeProduto());
        holder.textViewQuantidade.setText(String.format(
                "Retirada: %d (De %d para %d)",
                saida.getQuantidade(),
                saida.getQuantidadeAnterior(),
                saida.getQuantidadeRestante()
        ));
    }

    @Override
    public int getItemCount() {
        return listaSaidas.size();
    }

    static class SaidaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewProduto, textViewQuantidade;

        public SaidaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProduto = itemView.findViewById(R.id.textViewProduto);
            textViewQuantidade = itemView.findViewById(R.id.textViewQuantidade);
        }
    }
}