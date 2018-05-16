package br.com.seocursos.seocursos;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class RespostaTarefaActivity extends AppCompatActivity {
    private final static String JSON_URL = "https://www.seocursos.com.br/PHP/Android/tarefas.php";
    private String idTarefa;

    TextView pergunta;
    TextInputEditText resposta;
    Button btn;

    ProgressDialogHelper pd;
    SharedPreferencesHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resposta_tarefa);

        helper = new SharedPreferencesHelper(RespostaTarefaActivity.this);

        pergunta = findViewById(R.id.pergunta);
        resposta = findViewById(R.id.resposta);
        btn = findViewById(R.id.confirmar);

        pd = new ProgressDialogHelper(RespostaTarefaActivity.this);

        try {
            Intent intent = getIntent();
            idTarefa = intent.getStringExtra("id");
        }catch(NullPointerException e){
            e.printStackTrace();

            Toast.makeText(this, "Tarefa não encontrada!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(RespostaTarefaActivity.this, TarefasActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarResposta();
            }
        });
        carregar();
    }
    public void enviarResposta(){
        pd.open();

        String idUsuario = helper.getString("id");
        String textResposta = resposta.getText().toString();
        Map<String,String> params = new HashMap<String,String>();
        params.put("answer", "answer");
        params.put("idTarefa", idTarefa);
        params.put("resposta", textResposta);
        params.put("idUsuario", idUsuario);

        StringRequest sr = CRUD.customRequest(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    boolean enviado = jo.getBoolean("resposta");
                    if(enviado){
                        Toast.makeText(RespostaTarefaActivity.this, "Enviado com sucesso!", Toast.LENGTH_SHORT).show();
                    }else{
                        String error = jo.getString("error");
                        Toast.makeText(RespostaTarefaActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                    Intent i = new Intent(RespostaTarefaActivity.this, TarefasActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },RespostaTarefaActivity.this, params);
        RequestQueue rq = VolleySingleton.getInstance(RespostaTarefaActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }
    public void carregar(){
        pd.open();
        StringRequest sr = CRUD.selecionarEditar(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("tarefa");
                    JSONObject objeto = ja.getJSONObject(0);
                    String perguntaTarefa = objeto.getString("descricao");
                    pergunta.setText(perguntaTarefa);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },RespostaTarefaActivity.this, idTarefa);
        RequestQueue rq = VolleySingleton.getInstance(RespostaTarefaActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }
}
