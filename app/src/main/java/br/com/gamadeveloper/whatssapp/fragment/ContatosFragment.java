package br.com.gamadeveloper.whatssapp.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.gamadeveloper.whatssapp.Adapter.ContatoAdapter;
import br.com.gamadeveloper.whatssapp.Config.ConfiguracaoFirebase;
import br.com.gamadeveloper.whatssapp.Model.Contato;
import br.com.gamadeveloper.whatssapp.R;
import br.com.gamadeveloper.whatssapp.helper.Preferencias;

public class ContatosFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<Contato> contatos;//ArrayList para um objeto Contato
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerContatos;


    public ContatosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        //Verificar a atualização apenas quando o fragmento for incializado, e não no OnCreate
        firebase.addValueEventListener( valueEventListenerContatos);
    }

    @Override
    public void onStop() {
        super.onStop();
        //Removendo o eventListner para que não fique esperando por atualizações do Firebase
        firebase.removeEventListener( valueEventListenerContatos);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inicia o ArrayList
        contatos = new ArrayList<>();

        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_contatos, container, false);
        //Monta o listView e o adapter
        listView = (ListView) view.findViewById(R.id.lv_contatos);

        /*comentado para criação do adapter customizado
                adapter = new ArrayAdapter(
                          getActivity(),
                          R.layout.lista_contatos,
                          contatos
         //Este adapter traz um Array de Strings e retorna apenas 1 item

        );*/

        //adapter customizado:




        adapter = new ContatoAdapter(getActivity(), contatos);
        listView.setAdapter( adapter);
        //Recuperando os contatos no firebase
        Preferencias preferencias = new Preferencias(getActivity());
        String identificadorUsuarioLogado = preferencias.getIdentificador();

        firebase = ConfiguracaoFirebase.getFirebase()
                     .child("contatos")
                     .child(identificadorUsuarioLogado);

        //Notificar alterações na estrutura
        valueEventListenerContatos = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Limpar a lista
                contatos.clear();

                //listando os contatos
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Contato contato = dados.getValue(Contato.class);
                    contatos.add(contato);//Armazenando um objeto no array Contato
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        return view;
    }
}
