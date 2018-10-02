package br.com.gamadeveloper.whatssapp.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.gamadeveloper.whatssapp.Config.ConfiguracaoFirebase;
import br.com.gamadeveloper.whatssapp.Model.Mensagem;
import br.com.gamadeveloper.whatssapp.R;
import br.com.gamadeveloper.whatssapp.helper.Base64Custom;
import br.com.gamadeveloper.whatssapp.helper.Preferencias;

public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editMensagem;
    private ImageButton btEnviar;
    private DatabaseReference firebase;
    private ListView listView;
    private ArrayList<String> mensagens;
    private ArrayAdapter adapter;
    private ValueEventListener valueEventListenerMensagem;


    //dados do destinatario da conversa
    private String idUsuarioDestinatario;
    private String nomeUsuarioDestinatario;

    //dados do remetente
    private String idUsuarioRemetente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = (Toolbar) findViewById(R.id.tb_conversa);
        editMensagem = (EditText) findViewById(R.id.edit_mensagem);
        btEnviar = (ImageButton) findViewById(R.id.bt_enviar);
        listView = (ListView) findViewById(R.id.lv_conversas);

        //dados do usuario logado
        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        idUsuarioRemetente = preferencias.getIdentificador();

        //utilizado para passar dados entre activitys
        Bundle extra = getIntent().getExtras();//Recupera dados passados no extra

        //testando
        if (extra != null){
            nomeUsuarioDestinatario = extra.getString("nome");//Passado na putExtra do ContatosFragment
            String emailDestinatario = extra.getString("email");
            idUsuarioDestinatario = Base64Custom.codificarBase64(emailDestinatario);
        }

        //Configurar a toolbar
        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        //Monta o ListView de mensagens
        mensagens = new ArrayList<>();
        adapter = new ArrayAdapter(
                ConversaActivity.this,
                android.R.layout.simple_list_item_1,
                mensagens
        );
        listView.setAdapter(adapter);

        //Recuperar as mensagens do Firebase
        firebase = ConfiguracaoFirebase.getFirebase()
                    .child("mensagens")
                    .child(idUsuarioRemetente)
                    .child(idUsuarioDestinatario);
        //Criar a listner para mensagem (objeto ValueEventListner)
        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //limpar mensagens
                mensagens.clear();

                //Recupera as mensagens
                for(DataSnapshot dados: dataSnapshot.getChildren()){ //Percorre a estrutura mensagens no fireBase
                    Mensagem mensagem = dados.getValue( Mensagem.class);
                    mensagens.add( mensagem.getMensagem());

                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener( valueEventListenerMensagem);



        //Enviar Mensagem
        btEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoMensagem = editMensagem.getText().toString();

                //Slavar apenas se exixtir alguma mensagem
                if(textoMensagem.isEmpty()){
                    Toast.makeText(ConversaActivity.this, "Digite uma mensagem para enviar", Toast.LENGTH_LONG).toString();
                }else{
                    //Para salvar dados é preciso de uma classe model
                    //objeto mensagem
                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRemetente);
                    mensagem.setMensagem(textoMensagem);

                    salvarMensagem(idUsuarioRemetente,idUsuarioDestinatario, mensagem);
                    editMensagem.setText("");//Limpar a caixa de texto apos enviar
                }

            }
        });

    }


    private boolean salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem){
        try {

            firebase = ConfiguracaoFirebase.getFirebase().child("mensagens");
            firebase.child(idRemetente)//Quem enviou a mensagem
                    .child(idDestinatario) //Quem recebeu a mensagem + mensagem
                    .push() //Gerar identificador por mensagem
                    .setValue(mensagem);

            return true;

        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerMensagem);
    }
}
