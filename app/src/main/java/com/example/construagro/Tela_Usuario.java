package com.example.construagro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Tela_Usuario extends AppCompatActivity {

    private TextView nomeUsuarioTextView, emailUsuarioTextView;
    private Button btnAlterarDados, btnDesconectar;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_usuario);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nomeUsuarioTextView = findViewById(R.id.nome_usuario);
        emailUsuarioTextView = findViewById(R.id.email_usuario);
        btnAlterarDados = findViewById(R.id.btn_alterar_dados);
        btnDesconectar = findViewById(R.id.btn_desconectar);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String email = user.getEmail();
            String nome = user.getDisplayName();

            emailUsuarioTextView.setText("Email: " + email);
            if (nome != null && !nome.isEmpty()) {
                nomeUsuarioTextView.setText("Nome: " + nome);
            } else {
                nomeUsuarioTextView.setText("Nome: não disponível");
            }
        } else {
            Toast.makeText(this, "Nenhum usuário conectado", Toast.LENGTH_SHORT).show();
        }

        // Desconectar
        btnDesconectar.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(this, "Sessão encerrada", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Tela_Login.class)); // ajuste para sua tela de login
            finish();
        });

        // Alterar dados (você pode abrir uma tela futura)
        btnAlterarDados.setOnClickListener(v -> {
            Toast.makeText(this, "Função 'Alterar dados' em desenvolvimento", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, TelaAlterarDados.class));
        });
    }
}
