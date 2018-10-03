    package br.com.gamadeveloper.whatssapp.activity;

    import android.content.Intent;
    import android.support.annotation.NonNull;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.Toast;

    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.AuthResult;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
    import com.google.firebase.auth.FirebaseAuthInvalidUserException;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.ValueEventListener;

    import br.com.gamadeveloper.whatssapp.Config.ConfiguracaoFirebase;
    import br.com.gamadeveloper.whatssapp.Model.Usuario;
    import br.com.gamadeveloper.whatssapp.R;
    import br.com.gamadeveloper.whatssapp.helper.Base64Custom;
    import br.com.gamadeveloper.whatssapp.helper.Preferencias;

    public class LoginActivity extends AppCompatActivity {

        private EditText email;
        private EditText senha;
        private Button botaoLogar;
        private Usuario usuario;
        private FirebaseAuth autenticacao;
        private ValueEventListener valueEventListenerUsuario;
        private DatabaseReference firebase;
        private String identificadorUsuarioLogado;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            verificarUsuarioLogado();

            email = (EditText) findViewById(R.id.edit_login_email);
            senha = (EditText) findViewById(R.id.edit_login_senha);
            botaoLogar = (Button) findViewById(R.id.bt_logar);

            botaoLogar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    usuario = new Usuario();
                    usuario.setEmail( email.getText().toString() );
                    usuario.setSenha( senha.getText().toString() );

                    //Verifica se foi digitado email e/ou senha:
                    if (email.length() == 0){
                        Toast.makeText(LoginActivity.this, "Todos os campos são obrigatorio!", Toast.LENGTH_LONG).show();

                    }else if (senha.length() == 0) {
                        Toast.makeText(LoginActivity.this, "Todos os campos são obrigatorio!", Toast.LENGTH_LONG).show();
                    }else {
                        validarLogin();
                    }
                }
            });

        }

        private void verificarUsuarioLogado(){
            autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
            if( autenticacao.getCurrentUser() != null ){
                abrirTelaPrincipal();
            }
        }

        private void validarLogin(){

            autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
            autenticacao.signInWithEmailAndPassword(
                    usuario.getEmail(),
                    usuario.getSenha()
            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //Retorna o objeto task, que foi levado para autenticar e Verifica se o obejeto foi autenticado com sucesso

                    if (task.isSuccessful()) {

                        //Salvar preferencias:

                        identificadorUsuarioLogado = Base64Custom.codificarBase64(usuario.getEmail());

                        firebase = ConfiguracaoFirebase.getFirebase()
                                .child("usuarios")
                                .child(identificadorUsuarioLogado);

                        valueEventListenerUsuario = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Usuario usuarioRecuperado = dataSnapshot.getValue(Usuario.class);
                                Preferencias preferencias = new Preferencias(LoginActivity.this);
                                preferencias.salvarDados( identificadorUsuarioLogado, usuarioRecuperado.getNome());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        };

                        firebase.addListenerForSingleValueEvent(valueEventListenerUsuario);



                        abrirTelaPrincipal();
                        Toast.makeText(LoginActivity.this, "Sucesso ao fazer login", Toast.LENGTH_LONG).show();

                    } else {
                        String erroExcecao = "";
                        try{
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            erroExcecao = "Email digitado não existe ou foi desabilidado pelo usuario";
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            erroExcecao = "A senha digitada não esta correta";
                        } catch (Exception e) {
                            erroExcecao = "ao cadastrar fazer login. Tente novamente!";
                            e.printStackTrace();
                        }
                        Toast.makeText(LoginActivity.this, "Erro: " + erroExcecao, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        private void abrirTelaPrincipal(){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        public void abrirCadastroUsuario(View view){

            Intent intent = new Intent(LoginActivity.this, CadastroUsuarioActivity.class);
            startActivity( intent );

        }

    }