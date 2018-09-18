package br.com.gamadeveloper.whatssapp.Config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class ConfiguracaoFirebase { //Não pode ser extendida

    private static DatabaseReference referenciaFirebase;//Recebe a configuração do Firebase
    private static FirebaseAuth autenticacao;


    //Metodo responsavel pela recuperaçaõ dos dados do firebase
    public static DatabaseReference getFirebase(){

        if(referenciaFirebase == null){
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaFirebase;
    }

    //Metodo responsavel pela AUTENTICACAO dos USUARIO do firebase
    public static FirebaseAuth getFirebaseAutenticacao(){
        if(autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }
}
