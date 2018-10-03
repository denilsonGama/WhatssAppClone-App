package br.com.gamadeveloper.whatssapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.gamadeveloper.whatssapp.Model.Mensagem;
import br.com.gamadeveloper.whatssapp.R;
import br.com.gamadeveloper.whatssapp.helper.Preferencias;

public class MensagemAdapter extends ArrayAdapter<Mensagem> {

    private Context context;
    private ArrayList<Mensagem> mensagens;

    public MensagemAdapter(@NonNull Context c, @NonNull ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.context = c;
        this.mensagens = objects;
    }

    //montando a view de mensagens

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        //verifica se a lista esta preenchida
        if(mensagens != null){

            //Recupera dados do remetente
            Preferencias preferencias = new Preferencias(context);
            String idUsuarioRemetente = preferencias.getIdentificador();


            //Mensagens diferente de nula, inicializa a montagem do layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            //Recupera a mensagem
            Mensagem mensagem = mensagens.get(position);


            //monta o loyout apartir do xml
            if(idUsuarioRemetente.equals(mensagem.getIdUsuario())){
                view = inflater.inflate(R.layout.item_mensagem_direita, parent, false);
            }else{
                view = inflater.inflate(R.layout.item_mensagem_esquerda, parent, false);           }

            //Recuperar o elemento para exibição
            TextView textoMensagem = (TextView) view.findViewById(R.id.tv_mensagem);
            textoMensagem.setText(mensagem.getMensagem());
        }

        return view;
    }
}
