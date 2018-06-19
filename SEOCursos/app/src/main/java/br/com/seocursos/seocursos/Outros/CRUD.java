package br.com.seocursos.seocursos.Outros;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.seocursos.seocursos.R;

/**
 * Created by Aluno on 07/03/2018.
 */

public class CRUD {

    //Personalizado
    public static StringRequest customRequest(String url, Response.Listener<String> responseListener,
                                              final Context contexto, final Map<String, String> params){
        StringRequest sr = new StringRequest(Request.Method.POST, url, responseListener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(contexto, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            protected Map<String,String> getParams() throws com.android.volley.AuthFailureError{
                return params;
            }
        };
        return sr;
    }

    //Selecionar
    public static StringRequest selecionar(String url, Response.Listener<String> responseListener, final Context contexto){
        StringRequest sr = new StringRequest(Request.Method.POST, url, responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(contexto, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError{
                Map<String,String> params = new HashMap<String, String>();
                params.put("select","select");
                return params;
            }
        };

        return sr;
    }

    //Selecionar apenas o registro para edição
    public static StringRequest selecionarEditar(String url, Response.Listener<String> responseListener, final Context contexto,
                                                 final String id){
        StringRequest sr = new StringRequest(Request.Method.POST, url, responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(contexto, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError{
                Map<String,String> params = new HashMap<String, String>();
                params.put("editId",id);
                return params;
            }
        };
        return sr;
    }

    //Inserir dados
    public static StringRequest inserir(String url, Response.Listener<String> responseListener, final Map<String, String> params, final Context contexto){
        StringRequest sr = new StringRequest(Request.Method.POST, url, responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(contexto, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError{
                params.put("insert","insert");
                return params;
            }
        };
        return sr;
    }

    //Edita o registro pelo id
    public static StringRequest editar(String url, Response.Listener<String> responseListener, final Map<String, String> params, final Context contexto){
        StringRequest sr = new StringRequest(Request.Method.POST, url, responseListener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(contexto, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError{
                params.put("editar", "editar");
                return params;
            }
        };
        return sr;
    }

    //Exclui o registro pelo id
    public static StringRequest excluir(String url, final String id, final Context contexto){
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject resposta = new JSONObject(response);
                    boolean deletado = resposta.getBoolean("resposta");
                    if(deletado){
                        Toast.makeText(contexto, contexto.getResources().getString(R.string.excluidoComSucesso), Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(contexto, contexto.getResources().getString(R.string.falhaExclusao), Toast.LENGTH_LONG).show();
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(contexto, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            //Adiciona os parâmetros
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("deleteId", id);

                return params;
            }
        };
        return sr;
    }
}
