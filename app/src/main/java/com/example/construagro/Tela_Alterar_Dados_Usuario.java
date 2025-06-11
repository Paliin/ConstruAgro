package com.example.construagro;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Tela_Alterar_Dados_Usuario extends AppCompatActivity {

    private TextView textUsuarioLogado;
    private EditText editNome, editSenha;
    private Button btnSalvar, btnCancelar;
    private FirebaseAuth auth;
    private FirebaseUser usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_alterar_dados_usuario);

        auth = FirebaseAuth.getInstance();
        usuario = auth.getCurrentUser();

        textUsuarioLogado = findViewById(R.id.text_usuario_logado);
        editNome = findViewById(R.id.edit_nome);
        editSenha = findViewById(R.id.edit_senha);
        btnSalvar = findViewById(R.id.btn_salvar);
        btnCancelar = findViewById(R.id.btn_cancelar);

        if (usuario != null) {
            String usuarioID = usuario.getUid();
            FirebaseFirestore.getInstance()
                    .collection("Usuarios")
                    .document(usuarioID)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nome = documentSnapshot.getString("nome");
                            if (nome != null && !nome.isEmpty()) {
                                textUsuarioLogado.setText(nome.toUpperCase());
                            } else {
                                textUsuarioLogado.setText("USUÁRIO SEM NOME");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        textUsuarioLogado.setText("ERRO AO BUSCAR NOME");
                    });
        }

        btnSalvar.setOnClickListener(v -> alterarDados());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void alterarDados() {
        String novoNome = editNome.getText().toString().trim();
        String novaSenha = editSenha.getText().toString().trim();

        if (usuario == null) {
            Toast.makeText(this, "Usuário não está logado.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!novaSenha.isEmpty()) {
            EditText inputSenhaAtual = new EditText(this);
            inputSenhaAtual.setHint("Senha atual");

            new AlertDialog.Builder(this)
                    .setTitle("Reautenticação necessária")
                    .setMessage("Digite sua senha atual para alterar a senha.")
                    .setView(inputSenhaAtual)
                    .setPositiveButton("Confirmar", (dialog, which) -> {
                        String senhaAtual = inputSenhaAtual.getText().toString().trim();
                        reautenticarUsuario(usuario.getEmail(), senhaAtual, () -> {
                            aplicarAlteracoes(novoNome, novaSenha);
                        });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        } else {
            aplicarAlteracoes(novoNome, null);
        }
    }

    private void reautenticarUsuario(String email, String senha, Runnable aoReautenticar) {
        if (usuario == null) return;

        AuthCredential credential = EmailAuthProvider.getCredential(email, senha);

        usuario.reauthenticate(credential)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Reautenticação bem-sucedida", Toast.LENGTH_SHORT).show();
                    aoReautenticar.run();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Falha na reautenticação: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void aplicarAlteracoes(String novoNome, String novaSenha) {
        boolean alterouAlgo = false;

        if (novoNome != null && !novoNome.isEmpty()) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(novoNome)
                    .build();

            usuario.updateProfile(profileUpdates)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Nome atualizado", Toast.LENGTH_SHORT).show();
                        atualizarNomeNoFirestore(novoNome);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Erro ao atualizar nome: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
            alterouAlgo = true;
        }

        if (novaSenha != null && !novaSenha.isEmpty()) {
            usuario.updatePassword(novaSenha)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Senha atualizada", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erro ao atualizar senha: " + e.getMessage(), Toast.LENGTH_LONG).show());
            alterouAlgo = true;
        }

        if (alterouAlgo) {
            Toast.makeText(this, "Alterações salvas com sucesso.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Nenhuma alteração feita.", Toast.LENGTH_SHORT).show();
        }
    }

    private void atualizarNomeNoFirestore(String novoNome) {
        String usuarioID = usuario.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Usuarios").document(usuarioID);

        docRef.update("nome", novoNome)
                .addOnSuccessListener(unused -> {
                    // Nome no Firestore atualizado com sucesso
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao atualizar nome no Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
