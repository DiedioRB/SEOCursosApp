package br.com.seocursos.seocursos;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.seocursos.seocursos.ConstClasses.Disciplina;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class EditTarefaActivity extends AppCompatActivity {
    private final static String JSON_URL = "https://www.seocursos.com.br/PHP/Android/tarefas.php";
    private String id;

    TextInputEditText descricao;
    Spinner spinner;
    Button btn;
    ArrayList<Disciplina> listaDisciplinas;
    ArrayAdapter<String> adapter;

    private String idDisciplina;
    private int spinnerSelect = 0;

    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tarefa);

        pd = new ProgressDialogHelper(EditTarefaActivity.this);

        Intent intent = getIntent();
        try {
            id = intent.getStringExtra("id");
        }catch(NullPointerException e){
            e.printStackTrace();
            Toast.makeText(EditTarefaActivity.this, getResources().getString(R.string.usuarioNaoEncontrado), Toast.LENGTH_SHORT).show();
            Intent i = new Intent(EditTarefaActivity.this,TarefasActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

        descricao = findViewById(R.id.descricao);
        spinner = findViewById(R.id.disciplinas);

        btn = findViewById(R.id.confirmarTarefa);

        adapter = new ArrayAdapter<String>(EditTarefaActivity.this, R.layout.support_simple_spinner_dropdown_item);
        listaDisciplinas = new ArrayList<Disciplina>();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.open();
                Map<String,String> params = new HashMap<String, String>();
                params.put("id_tarefa", id);
                params.put("descricao", descricao.getText().toString());

                Disciplina disciplina = listaDisciplinas.get(spinner.getSelectedItemPosition());
                idDisciplina = disciplina.getId();
                params.put("id_disciplina", idDisciplina);
                SharedPreferencesHelper preferences = new SharedPreferencesHelper(EditTarefaActivity.this);
                String idUsuario = preferences.getString("id");
                params.put("id_usuario", idUsuario);

                StringRequest sr = CRUD.editar(JSON_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            boolean enviado = jo.getBoolean("resposta");
                            if(enviado) {
                                Toast.makeText(EditTarefaActivity.this, getResources().getString(R.string.editadoComSucesso), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(EditTarefaActivity.this, getResources().getString(R.string.falhaEdicao), Toast.LENGTH_SHORT).show();
                            }
                            Intent i = new Intent(EditTarefaActivity.this, TarefasActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },params,EditTarefaActivity.this);
                RequestQueue rq = VolleySingleton.getInstance(EditTarefaActivity.this).getRequestQueue();
                rq.add(sr);
                rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                    @Override
                    public void onRequestFinished(Request<Object> request) {
                        pd.close();
                    }
                });
            }
        });

        carregar();

        spinner.setAdapter(adapter);
        spinner.postDelayed(new Runnable() {
            @Override
            public void run() {
                spinner.setSelection(spinnerSelect);
            }
        }, 900);
    }
    public void carregar(){
        pd.open();
        String cursosURL = "https://www.seocursos.com.br/PHP/Android/disciplinas.php";
        final RequestQueue rq = VolleySingleton.getInstance(EditTarefaActivity.this).getRequestQueue();

        StringRequest sr = CRUD.selecionarEditar(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("tarefa");
                    JSONObject objeto = ja.getJSONObject(0);
                    descricao.setText(objeto.getString("descricao"));
                    idDisciplina = objeto.getString("id_disciplina");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },EditTarefaActivity.this,id);

        final StringRequest cursosRequest = CRUD.selecionar(cursosURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("disciplinas");
                    for(Integer i=0;i<ja.length();i++){
                        JSONObject objeto = ja.getJSONObject(i);
                        Disciplina disciplina = new Disciplina(objeto.getString("id_disciplina"),
                                objeto.getString("nome_disciplina"),
                                null,null,null,null,null,null,null,null);
                        listaDisciplinas.add(disciplina);
                        adapter.add(disciplina.getNome());

                        if(idDisciplina.equals(disciplina.getId())){
                            spinnerSelect = adapter.getPosition(disciplina.getNome());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }, EditTarefaActivity.this);

        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                rq.add(cursosRequest);
                rq.removeRequestFinishedListener(this);
                pd.close();
            }
        });
    }
}
