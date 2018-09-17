package br.com.gamadeveloper.whatssapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.HashMap;

import br.com.gamadeveloper.whatssapp.R;
import br.com.gamadeveloper.whatssapp.helper.Preferencias;

public class ValidadorActivity extends AppCompatActivity {

    private EditText codigoValidacao;
    private Button validar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validador);

        codigoValidacao = (EditText) findViewById(R.id.edit_cod_validacao);
        validar = (Button) findViewById(R.id.bt_validacao);

        SimpleMaskFormatter simpleMaskCodigoValidacao = new SimpleMaskFormatter( "NNNN");
        MaskTextWatcher mascaraCodigoValidacao = new MaskTextWatcher( codigoValidacao, simpleMaskCodigoValidacao);

        codigoValidacao.addTextChangedListener( mascaraCodigoValidacao);

        validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Recuperar dados das preferencias do usuario
                Preferencias preferencias = new Preferencias( ValidadorActivity.this); //passando o contexto da activity
                HashMap<String, String> usuario = preferencias.getDadosUsuario();

                String tokenGerado = usuario.get( "token");//Recupera o token gerado pela aplicação
                String tokenDigitado = codigoValidacao.getText().toString();//Recupera o token digitado pelo usuario

                //Validando
                if(tokenDigitado.equals(tokenGerado)){//se o Digitado = Gerado
                    Toast.makeText( ValidadorActivity.this, "Token Valido!!", Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText( ValidadorActivity.this, "Token Inválido!!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
