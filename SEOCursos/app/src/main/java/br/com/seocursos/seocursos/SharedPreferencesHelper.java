package br.com.seocursos.seocursos;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by Aluno on 13/04/2018.
 */

class SharedPreferencesHelper {
    public static final String PREFS_NAME = "SEOPreferences";

    private static SharedPreferencesHelper instance;
    private static Context contexto;

    private static SharedPreferences preferences;

    private SharedPreferences getPreferences() {
        return preferences;
    }

    public SharedPreferencesHelper(Context contexto) {
        this.contexto = contexto;
        preferences = contexto.getSharedPreferences(PREFS_NAME, 0);
    }

    private SharedPreferences.Editor getEditor(){
        return preferences.edit();
    }

    public void setString(String name, String value){
        SharedPreferences.Editor editor = getEditor();
        editor.putString(name,value);

        editor.commit();
    }
    public void setBoolean(String name, boolean value){
        SharedPreferences.Editor editor = getEditor();
        editor.putBoolean(name,value);

        editor.commit();
    }
    public String getString(String name){
        String retorno = getPreferences().getString(name, null);

        return retorno;
    }
    public boolean getBoolean(String name){
        boolean retorno = getPreferences().getBoolean(name, false);

        return retorno;
    }
}
