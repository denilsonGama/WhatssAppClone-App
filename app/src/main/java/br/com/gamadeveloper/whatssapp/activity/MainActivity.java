package br.com.gamadeveloper.whatssapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.gamadeveloper.whatssapp.Adapter.TabAdapter;
import br.com.gamadeveloper.whatssapp.Config.ConfiguracaoFirebase;
import br.com.gamadeveloper.whatssapp.Model.Contato;
import br.com.gamadeveloper.whatssapp.Model.Usuario;
import br.com.gamadeveloper.whatssapp.R;
import br.com.gamadeveloper.whatssapp.helper.Base64Custom;
import br.com.gamadeveloper.whatssapp.helper.Preferencias;
import br.com.gamadeveloper.whatssapp.helper.SlidingTabLayout;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth usuarioFirebase;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private String identificadorContato;
    private DatabaseReference firebase;//Objeto para manipular o BD Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuarioFirebase = ConfiguracaoFirebase.getFirebaseAutenticacao();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        viewPager = (ViewPager) findViewById(R.id.vp_pagina);

        //Configurar sliding tabs
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this,R.color.colorAccente));

        //Configurar adapter
        TabAdapter tabAdapter = new TabAdapter( getSupportFragmentManager() );
        viewPager.setAdapter(tabAdapter);

        slidingTabLayout.setViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId() ){
            case R.id.item_sair :
                deslogarUsuario();
                return true;
            case R.id.item_configuracoes :
                return true;
            case R.id.item_adcionar:
                abrirCadastroContato();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void abrirCadastroContato(){

        //Objeto Dialog criado
        AlertDialog.Builder alertDialog  = new AlertDialog.Builder(MainActivity.this);

        //Configrando alert
        alertDialog.setTitle("Novo contato");
        alertDialog.setMessage("E-mail do usuário");
        alertDialog.setCancelable(false);//Não será possivel cancelar fora do Dialog

        //Criando a caixa de texto dentro da Dialog
        final EditText editText = new EditText(MainActivity.this);
        alertDialog.setView( editText);//recebe um objeto View e exibe na view


        //Configurando botões
        alertDialog.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Recuperando o email digitado no Dialog
                String emailContato = editText.getText().toString();

                //Valida a digitação
                if(emailContato.isEmpty()){
                    Toast.makeText(MainActivity.this, "Preencha com um e-mail", Toast.LENGTH_LONG).show();
                }else {

                    //Verificar se o usuário já esta cadastrado no nosso app
                    identificadorContato = Base64Custom.codificarBase64(emailContato);

                    //Recuperar a intancia firebase
                    firebase = ConfiguracaoFirebase.getFirebase().child("usuarios").child( identificadorContato);

                    //Apenas uma consulta. O Firebase não verificará atualizações
                    firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Se o dataSnapshot não é nulo, existe o usuário no Nó usuarios
                            if( dataSnapshot.getValue() != null){


                                //Estrutura de gravaçao no firebase
                                /*
                                +contatos
                                    +denilson.gama@ (identificador usuario logado
                                        +joseane.soares (identificador contato)
                                            dados do usuario
                                        +willian.diniz (identificador contato)
                                            dados do usuario
                                 */

                                //Recuperando dados do usuario contato. Retorna objeto do tipo usuario
                                Usuario usuarioContato = dataSnapshot.getValue(Usuario.class );

                                //Recuperando dados do usuario no preferencias
                                Preferencias preferencias = new Preferencias(MainActivity.this);
                                String identificadorUsuarioLogado  = preferencias.getIdentificador();


                                usuarioFirebase.getCurrentUser().getEmail();//Usuário logado
                                firebase = ConfiguracaoFirebase.getFirebase();
                                firebase = firebase.child("contatos")
                                                   .child( identificadorUsuarioLogado)
                                                   .child( identificadorContato);

                                Contato contato = new Contato();
                                contato.setIdentificadorUsuario( identificadorContato );
                                contato.setEmail(usuarioContato.getEmail() );
                                contato.setNome( usuarioContato.getNome() );
                                firebase.setValue(contato);

                                Toast.makeText(MainActivity.this, "Usuário cadastrado com sucesso",
                                        Toast.LENGTH_LONG ).show();

                            }else{
                                Toast.makeText(MainActivity.this, "Usuário não possui cadastro",
                                        Toast.LENGTH_LONG ).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.create();//Criar a Dialog
        alertDialog.show();//Exibir a Dialog

    }


    private void deslogarUsuario(){

        usuarioFirebase.signOut();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
