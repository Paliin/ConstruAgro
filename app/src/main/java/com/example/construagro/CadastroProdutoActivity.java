package com.example.construagro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CadastroProdutoActivity extends AppCompatActivity {

    private EditText editTextNome, editTextQuantidade, editTextCategoria, editTextValor;
    private Button buttonSalvar;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_produto);

        databaseRef = FirebaseDatabase.getInstance().getReference("produtos");

        editTextNome = findViewById(R.id.editTextNome);
        editTextQuantidade = findViewById(R.id.editTextQuantidade);
        editTextCategoria = findViewById(R.id.editTextCategoria);
        editTextValor = findViewById(R.id.editTextValor);
        buttonSalvar = findViewById(R.id.buttonSalvar);
        ImageButton btnVoltar = findViewById(R.id.btn_voltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Encerra a Activity atual e volta para a anterior
            }
        });


        buttonSalvar.setOnClickListener(v -> salvarProduto());

    }

    private void salvarProduto() {
        String nome = editTextNome.getText().toString().trim();
        String quantidadeStr = editTextQuantidade.getText().toString().trim();
        String categoria = editTextCategoria.getText().toString().trim();
        String valorStr = editTextValor.getText().toString().trim();

        if (nome.isEmpty() || quantidadeStr.isEmpty() || categoria.isEmpty() || valorStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantidade = Integer.parseInt(quantidadeStr);
        double valor = Double.parseDouble(valorStr);

        // Gera a data atual no formato dd/MM/yyyy
        String dataAtual = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        // Verifica se o produto já existe
        databaseRef.orderByChild("nome").equalTo(nome)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(CadastroProdutoActivity.this, "Produto já cadastrado!", Toast.LENGTH_SHORT).show();
                        } else {
                            String id = databaseRef.push().getKey();
                            Produto produto = new Produto(nome, quantidade, categoria, valor, dataAtual);
                            databaseRef.child(id).setValue(produto)
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(CadastroProdutoActivity.this, "Produto cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                                    )
                                    .addOnFailureListener(e ->
                                            Toast.makeText(CadastroProdutoActivity.this, "Erro ao salvar produto.", Toast.LENGTH_SHORT).show()
                                    );
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(CadastroProdutoActivity.this, "Erro ao acessar o banco de dados: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
