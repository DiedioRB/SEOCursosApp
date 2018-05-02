package br.com.seocursos.seocursos;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class ChatActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/chat.php";
    private String idUsuario;

    LinearLayout chat;
    TextInputEditText mensagem;
    Button btn;

    SharedPreferencesHelper helper;
    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        pd = new ProgressDialogHelper(ChatActivity.this);

        helper = new SharedPreferencesHelper(ChatActivity.this);
        idUsuario = helper.getString("id");

        chat = findViewById(R.id.chat);
        mensagem = findViewById(R.id.mensagem);
        btn = findViewById(R.id.confirmar);

        carregar();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.open();
                String msg = mensagem.getText().toString();
                mensagem.setText("");

                Map<String,String> params = new HashMap<String,String>();
                params.put("mensagem",msg);
                params.put("idUsuario", idUsuario);

                StringRequest sr = CRUD.inserir(JSON_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        carregar();
                    }
                },params,ChatActivity.this);
                RequestQueue rq = VolleySingleton.getInstance(ChatActivity.this).getRequestQueue();
                rq.add(sr);
                rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                    @Override
                    public void onRequestFinished(Request<Object> request) {
                        pd.close();
                    }
                });
            }
        });
    }
    public void carregar(){
        pd.open();
        chat.removeAllViews();
        StringRequest sr = CRUD.selecionar(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("chat");
                    for(int i=0;i<ja.length(); i++){
                        JSONObject objeto = ja.getJSONObject(i);
                        String id = objeto.getString("id_usuario");
                        String nome = objeto.getString("nome");
                        String mensagem = objeto.getString("mensagem");

                        LayoutInflater inflater = LayoutInflater.from(ChatActivity.this);
                        View v = inflater.inflate(R.layout.chat_balloon,null);
                        TextView info = v.findViewById(R.id.info);
                        TextView msg = v.findViewById(R.id.mensagem);

                        info.setText(nome);
                        msg.setText(mensagem);

                        if (id.equals(idUsuario)) {
                            info.setGravity(Gravity.END);
                            msg.setGravity(Gravity.END);
                        }

                        chat.addView(v);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, ChatActivity.this);
        RequestQueue rq = VolleySingleton.getInstance(ChatActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }
}
