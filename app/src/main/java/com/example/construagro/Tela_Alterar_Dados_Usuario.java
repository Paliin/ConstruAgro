package com.example.construagro;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Tela_Alterar_Dados_Usuario extends AppCompatActivity {

    private TextView textUsuarioLogado;
    private EditText editNome, editEmail, editSenha;
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
        editEmail = findViewById(R.id.edit_email);
        editSenha = findViewById(R.id.edit_senha);
        btnSalvar = findViewById(R.id.btn_salvar);
        btnCancelar = findViewById(R.id.btn_cancelar);

        if (usuario != null) {
            String nomeExibicao = usuario.getDisplayName();
            if (nomeExibicao == null || nomeExibicao.isEmpty()) {
                nomeExibicao = usuario.getEmail();
            }
            textUsuarioLogado.setText(nomeExibicao.toUpperCase());
        }

        btnSalvar.setOnClickListener(v -> alterarDados());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void alterarDados() {
        String novoNome = editNome.getText().toString().trim();
        String novoEmail = editEmail.getText().toString().trim();
        String novaSenha = editSenha.getText().toString().trim();

        if (usuario == null) {
            Toast.makeText(this, "Usuário não está logado.", Toast.LENGTH_LONG).show();
            return;
        }

        boolean alterouAlgo = false;

        if (!novoNome.isEmpty()) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(novoNome)
                    .build();

            usuario.updateProfile(profileUpdates)
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erro ao atualizar nome: " + e.getMessage(), Toast.LENGTH_LONG).show());
            alterouAlgo = true;
        }

        if (!novoEmail.isEmpty()) {
            usuario.updateEmail(novoEmail)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "E-mail atualizado", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erro ao atualizar e-mail: " + e.getMessage(), Toast.LENGTH_LONG).show());
            alterouAlgo = true;
        }

        if (!novaSenha.isEmpty()) {
            usuario.updatePassword(novaSenha)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, "Senha atualizada", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erro ao atualizar senha: " + e.getMessage(), Toast.LENGTH_LONG).show());
            alterouAlgo = true;
        }

        if (alterouAlgo) {
            Toast.makeText(this, "Alterações salvas.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Nenhuma alteração feita.", Toast.LENGTH_SHORT).show();
        }
    }
}
