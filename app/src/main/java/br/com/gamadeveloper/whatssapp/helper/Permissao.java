package br.com.gamadeveloper.whatssapp.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.wrappers.PackageManagerWrapper;

import java.util.ArrayList;
import java.util.List;

public class Permissao {
    public static boolean validaPermissoes(int requestCode, Activity activity, String[] Permissoes ){

        if(Build.VERSION.SDK_INT >=23){

            List<String> listaPermissoes = new ArrayList<String>();

            //Percorre as permissões passadas e verifica se já foi liberada
            for(String permissao: Permissoes){
                boolean validaPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if(!validaPermissao) listaPermissoes.add(permissao);
            }

            //Caso a lista esteja vazia, não pedir permissão
            if(listaPermissoes.isEmpty() ) return true;

            String[] novasPermissoes = new String [ listaPermissoes.size()];
            listaPermissoes.toArray( novasPermissoes);

            //Solicita permissão
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode);


        }

        return true;

    }

}
