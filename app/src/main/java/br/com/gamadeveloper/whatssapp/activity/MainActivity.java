package br.com.gamadeveloper.whatssapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.gamadeveloper.whatssapp.Config.ConfiguracaoFirebase;
import br.com.gamadeveloper.whatssapp.R;

public class MainActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private FirebaseAuth usuarioAutemticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inicializar usuarioAutenticação
        usuarioAutemticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("WhatssApp");
        setSupportActionBar( toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); //Classe utilizada para exibir menus
        inflater.inflate(R.menu.menu_main, menu);// Pega o menu e exibe na tela
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId() ){
            case R.id.item_sair:
                deslogarUsuario();
                return true;
            case R.id.item_configuracoes:
                return true;
            default:
                return super.onOptionsItemSelected(item);//boa pratica, finalizar com default
        }
    }

    public void deslogarUsuario(){
        usuarioAutemticacao.signOut();
        //Direcionar o usuario:
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity( intent );
        finish();
    }
}