package com.example.construagro;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class CadastroProdutoActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextViewNome;
    private EditText editTextQuantidade, editTextCategoria, editTextValor;
    private TextView textViewQuantidadeExistente;
    private Button buttonSalvar;
    private ImageButton btnVoltar;
    private DatabaseReference databaseRef;

    private List<Produto> listaProdutos = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private String usuarioAtual; // Usuário logado atual
    private Produto produtoSelecionado; // Produto selecionado para edição

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_produto);

        // Obtem usuário atual (FirebaseAuth)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        usuarioAtual = user != null ? user.getDisplayName() : "Administrador";

        databaseRef = FirebaseDatabase.getInstance().getReference("produtos");

        autoCompleteTextViewNome = findViewById(R.id.autoCompleteTextViewNome);
        editTextQuantidade = findViewById(R.id.editTextQuantidade);
        editTextCategoria = findViewById(R.id.editTextCategoria);
        editTextValor = findViewById(R.id.editTextValor);
        buttonSalvar = findViewById(R.id.buttonSalvar);
        textViewQuantidadeExistente = findViewById(R.id.textViewQuantidadeExistente);
        btnVoltar = findViewById(R.id.btn_voltar);

        // Configura adapter para autocomplete de nomes de produtos
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextViewNome.setAdapter(adapter);
        autoCompleteTextViewNome.setThreshold(1);

        carregarProdutos();

        // Quando o usuário selecionar um produto no autocomplete, preenche os campos para edição
        autoCompleteTextViewNome.setOnItemClickListener((parent, view, position, id) -> {
            String nomeSelecionado = (String) parent.getItemAtPosition(position);
            for (Produto produto : listaProdutos) {
                if (produto.getNome().equals(nomeSelecionado)) {
                    produtoSelecionado = produto;
                    textViewQuantidadeExistente.setVisibility(View.VISIBLE);
                    textViewQuantidadeExistente.setText(
                            String.format("Quantidade: %d | Cadastrado por: %s em %s",
                                    produto.getQuantidade(),
                                    produto.getUsuarioCadastro(),
                                    produto.getDataCadastro())
                    );
                    editTextCategoria.setText(produto.getCategoria());
                    editTextValor.setText(String.valueOf(produto.getValor()));
                    break;
                }
            }
        });

        // Botão salvar chama o métod que cadastra ou atualiza
        buttonSalvar.setOnClickListener(v -> salvarProduto());

        // Botão voltar fecha a activity atual
        btnVoltar.setOnClickListener(v -> finish());
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
                        produto.setId(dataSnapshot.getKey()); // salva o id do Firebase
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
                Toast.makeText(CadastroProdutoActivity.this,
                        "Erro ao carregar produtos: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void salvarProduto() {
        String nome = autoCompleteTextViewNome.getText().toString().trim();
        String quantidadeStr = editTextQuantidade.getText().toString().trim();
        String categoria = editTextCategoria.getText().toString().trim();
        String valorStr = editTextValor.getText().toString().trim();

        if (nome.isEmpty() || quantidadeStr.isEmpty() || categoria.isEmpty() || valorStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantidadeAdicional = Integer.parseInt(quantidadeStr);
        double valor = Double.parseDouble(valorStr);
        String dataAtual = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

        if (produtoSelecionado != null) {
            // Soma a nova quantidade à existente
            int novaQuantidadeTotal = produtoSelecionado.getQuantidade() + quantidadeAdicional;

            Produto.Movimentacao mov = new Produto.Movimentacao(
                    quantidadeAdicional,
                    "Entrada",
                    usuarioAtual,
                    dataAtual,
                    "Adição de estoque"
            );

            produtoSelecionado.setQuantidade(novaQuantidadeTotal);
            produtoSelecionado.setCategoria(categoria);
            produtoSelecionado.setValor(valor);
            produtoSelecionado.getHistorico().add(mov);

            databaseRef.child(produtoSelecionado.getId()).setValue(produtoSelecionado)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this,
                                String.format("Quantidade somada com sucesso! Novo total: %d", novaQuantidadeTotal),
                                Toast.LENGTH_LONG).show();
                        limparCampos();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erro ao atualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // Novo cadastro
            String id = databaseRef.push().getKey();
            Produto novoProduto = new Produto(
                    nome,
                    quantidadeAdicional,
                    categoria,
                    valor,
                    dataAtual,
                    usuarioAtual
            );
            novoProduto.setId(id);

            databaseRef.child(id).setValue(novoProduto)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this,
                                "Produto cadastrado por " + usuarioAtual + "!",
                                Toast.LENGTH_SHORT).show();
                        limparCampos();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erro ao cadastrar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }


    private void limparCampos() {
        autoCompleteTextViewNome.setText("");
        editTextQuantidade.setText("");
        editTextCategoria.setText("");
        editTextValor.setText("");
        textViewQuantidadeExistente.setVisibility(View.GONE);
        produtoSelecionado = null;
    }
}
