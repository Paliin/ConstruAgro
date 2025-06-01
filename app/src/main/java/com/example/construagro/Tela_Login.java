package com.example.construagro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Tela_Login extends AppCompatActivity {

    private TextView Criar_cadastro;
    private EditText login_usuario, login_senha;
    private Button Button_Entrar;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        IniciarComponentes();

        // botão de criar cadastro
        Criar_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Tela_Login.this, Tela_Cadastro.class);
                startActivity(intent);
            }
        });

        //BOTÃO DE ENTRAR

        Button_Entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = login_usuario.getText().toString();
                String senha = login_senha.getText().toString();

                if (email.isEmpty() || senha.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, "Preencha todos os campos!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    AutenticarUsuario(v);
                }
            }
        });
    }

    //metodo para autenticar o usuario
    private void AutenticarUsuario(View v){
        String entrada = login_usuario.getText().toString().trim();
        String senha = login_senha.getText().toString().trim();

        if (entrada.contains("@")) {
            // É um e-mail, pode logar direto
            LoginComEmail(entrada, senha, v);
        } else {
            // É um nome de usuário, buscar no Firestore
            FirebaseFirestore.getInstance().collection("Usuarios")
                    .whereEqualTo("nome", entrada)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String emailEncontrado = queryDocumentSnapshots.getDocuments().get(0).getString("email");
                            LoginComEmail(emailEncontrado, senha, v);
                        } else {
                            mostrarErro(v, "Usuário não encontrado");
                        }
                    })
                    .addOnFailureListener(e -> {
                        mostrarErro(v, "Erro ao buscar usuário: " + e.getMessage());
                    });
        }
    }

    //Se o login for sucedido, ir para próxima tela
    private void LoginComEmail(String email, String senha, View v){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(Tela_Login.this, Tela_Menu.class);
                        startActivity(intent);
                    } else {
                        mostrarErro(v, "Usuário ou senha inválidos!");
                    }
                });
    }

    //metodo para que o usuario conectado, permaneca conectado depois de logar mesmo se fechar o aplicativo
    private void mostrarErro(View v, String mensagem){
        Snackbar snackbar = Snackbar.make(v, mensagem, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }

    private void IniciarComponentes() {
        Criar_cadastro = findViewById(R.id.Criar_cadastro);
        login_usuario = findViewById(R.id.login_usuario);
        login_senha = findViewById(R.id.login_senha);
        progressBar = findViewById(R.id.progressBar);
        Button_Entrar = findViewById(R.id.Button_Entrar);

    }

}