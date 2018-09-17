package br.com.gamadeveloper.whatssapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.github.rtoshiro.util.format.text.SimpleMaskTextWatcher;

import java.util.HashMap;
import java.util.Random;

import br.com.gamadeveloper.whatssapp.R;
import br.com.gamadeveloper.whatssapp.helper.Permissao;
import br.com.gamadeveloper.whatssapp.helper.Preferencias;

public class LoginActivity extends AppCompatActivity {


    private EditText nome;
    private EditText codPais;
    private EditText codArea;
    private EditText telefone;
    private Button cadastrar;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Permissao.validaPermissoes(1, this,permissoesNecessarias );

        nome         = (EditText) findViewById(R.id.edit_nome);
        codPais      = (EditText) findViewById(R.id.edit_cod_pais);
        codArea      = (EditText) findViewById(R.id.edit_cod_area);
        telefone     = (EditText) findViewById(R.id.edit_telefone);
        cadastrar = (Button) findViewById(R.id.bt_cadastrar);

        //Definir as mascaras

        SimpleMaskFormatter simpleMaskCodPais = new SimpleMaskFormatter("+NN");
        SimpleMaskFormatter simpleMaskCodArea = new SimpleMaskFormatter("(NN)");
        SimpleMaskFormatter simpleMaskTelefone = new SimpleMaskFormatter("NNNNN-NNNN");

        MaskTextWatcher maskCodPais = new SimpleMaskTextWatcher(codPais, simpleMaskCodPais);
        MaskTextWatcher maskCodArea = new SimpleMaskTextWatcher(codArea, simpleMaskCodArea);
        MaskTextWatcher maskTelefone = new SimpleMaskTextWatcher(telefone, simpleMaskTelefone);

        codPais.addTextChangedListener(maskCodPais);
        codArea.addTextChangedListener(maskCodArea);
        telefone.addTextChangedListener(maskTelefone);

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeUsuario = nome.getText().toString();
                String telefoneCompleto =
                        codPais.getText().toString() +
                        codArea.getText().toString() +
                        telefone.getText().toString();

                String telefoneSemFormatacao = telefoneCompleto.replace("+","");
                telefoneSemFormatacao = telefoneSemFormatacao.replace("(","");
                telefoneSemFormatacao = telefoneSemFormatacao.replace(")","");
                telefoneSemFormatacao = telefoneSemFormatacao.replace("-","");

                //Log.i("Telefone","Tel: "+ telefoneSemFormatacao);

                //Gerar Token (Procure por gerar token via servidor)
                Random randomico = new Random();
                int numeroRandomico = randomico.nextInt( 9999) + 1000;//Gerar numeros entre 1000 e 9999
                String token = String.valueOf(numeroRandomico);
                String mensagemEnvio = "WhatssApp Clone Código de Confirmação: "+ token;

                //Log.i("Randomico","Nr: "+ numeroRandomico);

                //Salvar os dados para validação
                Preferencias preferencias = new Preferencias(getApplicationContext());
                preferencias.salvarUsuarioPreferencias(nomeUsuario, telefoneSemFormatacao, token);

                //Envio de SMS (fone emulador: 15555215554 - Apenas para testar no Emulador)
                telefoneSemFormatacao = "5554";
                boolean enviadoSMS = enviaSMS("+" + telefoneSemFormatacao, mensagemEnvio);

                if(enviadoSMS){ //se for true, ou seja, se foi enviado

                    Intent intent = new Intent(LoginActivity.this, ValidadorActivity.class);
                    startActivity( intent );
                    finish();

                } else {

                    Toast.makeText( LoginActivity.this, "Problema ao enviar o SMS, tente novamente!!", Toast.LENGTH_LONG).show();

                }


                /*HashMap<String, String> usuario = preferencias.getDadosUsuario();
                Log.i("TOKEN", "T:" + usuario.get("token"));*/


            }
        });
    }

    //Envio do SMS
    private Boolean enviaSMS(String telefone, String mensagem){
        try{

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(telefone, null, mensagem, null, null);

            return true;

        }catch (Exception e){
            e.printStackTrace();

            return false;
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            for(int resultado : grantResults){

                if(resultado == PackageManager.PERMISSION_DENIED){
                    alertaValidacaoPermissao();
                }

            }
    }

    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar esta aplicação é necessário aprovar estas permissões");

        builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
