package br.com.gamadeveloper.whatssapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.gamadeveloper.whatssapp.Model.Contato;
import br.com.gamadeveloper.whatssapp.R;

public class ContatoAdapter extends ArrayAdapter<Contato> {
    private ArrayList<Contato> contatos;
    private Context context;


    public ContatoAdapter(@NonNull Context c, ArrayList<Contato> objects) {
        super(c, 0, objects);
        this.contatos = objects;
        this.context = c;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;

        //Verifica se a lista esta vazia. Só exibe se tiver contatos
        if( contatos != null){
            //A lista não esta vazia, entao exiba:
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(context.LAYOUT_INFLATER_SERVICE);//Serviço de montagem de layout
            //Montar a view apartir do xml
            view = inflater.inflate(R.layout.lista_contatos, parent, false);

            //Recupera os elementos para exibir na view
            TextView nomeContato = (TextView) view.findViewById(R.id.tv_nome);
            TextView emailContato = (TextView) view.findViewById(R.id.tv_email);

            Contato contato = contatos.get( position );
            nomeContato.setText(contato.getNome());
            emailContato.setText(contato.getEmail());
        }

        return view;
    }
}
