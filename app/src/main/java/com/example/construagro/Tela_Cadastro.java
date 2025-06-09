package com.example.construagro;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Tela_Cadastro extends AppCompatActivity {

    // criando as credenciais pra salvar no firebase
    private EditText nome_usuario, email_usuario, senha_usuario;
    private Button criar_cadastro;

    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        IniciarComponents();

        criar_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                String nome = nome_usuario.getText().toString().trim();
                String email = email_usuario.getText().toString().trim();
                String senha = senha_usuario.getText().toString().trim();

                // VERIFICANDO CAMPOS
                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(V, "Preencha todos os campos!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                } else {
                    CadastrarUsuario(V, email, senha);
                }
            }
        });
    }

    // CADASTRO NO FIREBASE
    private void CadastrarUsuario(View V, String email, String senha) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            SalvarDadosUsuario();

                            Snackbar snackbar = Snackbar.make(V, "Usuário cadastrado com sucesso!", Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.WHITE);
                            snackbar.setTextColor(Color.BLACK);
                            snackbar.show();
                        } else {
                            String erro;
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                erro = "Digite uma senha com no mínimo 6 caracteres";
                            } catch (FirebaseAuthUserCollisionException e) {
                                erro = "Este E-mail já está sendo utilizado";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                erro = "O formato do E-mail é invalido";
                            } catch (Exception e) {
                                erro = "Erro ao Cadastrar Usúario";
                            }
                            Snackbar snackbar = Snackbar.make(V, erro, Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.WHITE);
                            snackbar.setTextColor(Color.BLACK);
                            snackbar.show();

                        }
                    }
                });
    }

    //a função salva o nome e ID do usuario no Firebase
    private void SalvarDadosUsuario(){ // salvar dados no firebase do nome do usuario
        String nome = nome_usuario.getText().toString();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail(); // pega o e-mail do usuário atual
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> usuarios = new HashMap<>();
        usuarios.put("nome", nome);
        usuarios.put("email", email);

        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid(); // pega o ID do usuário atual
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);
        documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("db", "Sucesso ao salvar os dados");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("db_error", "Erro ao salvar os dados" + e.toString());
            }
        });
    }

    private void IniciarComponents() {
        nome_usuario = findViewById(R.id.Nome_usuario);
        email_usuario = findViewById(R.id.Email_usuario);
        senha_usuario = findViewById(R.id.Senha_usuario);
        criar_cadastro = findViewById(R.id.Bt_criar_cadastro);
    }


}
