package com.example.construagro;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CadastroProdutoActivity extends AppCompatActivity {

    private EditText editTextNome, editTextQuantidade, editTextCategoria;
    private Button buttonSalvar;

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_produto);

        // Inicializa o Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("produtos");

        // Liga os componentes da tela
        editTextNome = findViewById(R.id.editTextNome);
        editTextQuantidade = findViewById(R.id.editTextQuantidade);
        editTextCategoria = findViewById(R.id.editTextCategoria);
        buttonSalvar = findViewById(R.id.buttonSalvar);

        // Ação do botão "Salvar"
        buttonSalvar.setOnClickListener(v -> salvarProduto());
    }

    private void salvarProduto() {
        String nome = editTextNome.getText().toString().trim();
        String quantidadeStr = editTextQuantidade.getText().toString().trim();
        String categoria = editTextCategoria.getText().toString().trim();

        if (nome.isEmpty() || quantidadeStr.isEmpty() || categoria.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantidade = Integer.parseInt(quantidadeStr);

        // Verifica se o produto já existe
        databaseRef.orderByChild("nome").equalTo(nome)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(CadastroProdutoActivity.this, "Produto já cadastrado!", Toast.LENGTH_SHORT).show();
                        } else {
                            String id = databaseRef.push().getKey();
                            Produto produto = new Produto(nome, quantidade, categoria);
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
