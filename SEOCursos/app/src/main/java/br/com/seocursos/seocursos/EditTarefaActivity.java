package br.com.seocursos.seocursos;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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

public class EditTarefaActivity extends AppCompatActivity {
    private final static String JSON_URL = "https://www.seocursos.com.br/PHP/Android/tarefas.php";
    private String id;

    TextInputEditText descricao;
    Spinner spinner;
    Button btn;
    ArrayList<Disciplina> listaDisciplinas;
    ArrayAdapter<String> adapter;
    String idDisciplina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tarefa);

        Intent intent = getIntent();
        try {
            id = intent.getStringExtra("id");
        }catch(NullPointerException e){
            e.printStackTrace();
            Toast.makeText(EditTarefaActivity.this, "Usuário não encontrado!", Toast.LENGTH_SHORT).show();
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
                Map<String,String> params = new HashMap<String, String>();
                params.put("descricao", descricao.getText().toString());

                Disciplina disciplina = listaDisciplinas.get(spinner.getSelectedItemPosition());
                idDisciplina = disciplina.getId();
                params.put("id_disciplina", idDisciplina);
                SharedPreferencesSingleton preferences = SharedPreferencesSingleton.getInstance(EditTarefaActivity.this);
                String idUsuario = preferences.getString("id");
                params.put("id_usuario", idUsuario);
                //TODO: depois do login, recuperar id do usuário

                StringRequest sr = CRUD.editar(JSON_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            boolean enviado = jo.getBoolean("resposta");
                            if(enviado) {
                                Toast.makeText(EditTarefaActivity.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(EditTarefaActivity.this, "Falha no cadastro!", Toast.LENGTH_SHORT).show();
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
            }
        });

        carregar();

        spinner.setAdapter(adapter);
    }
    public void carregar(){
        String cursosURL = "https://www.seocursos.com.br/PHP/Android/disciplinas.php";
        RequestQueue rq = VolleySingleton.getInstance(EditTarefaActivity.this).getRequestQueue();

        StringRequest cursosRequest = CRUD.selecionar(cursosURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("disciplinas");
                    for(Integer i=0;i<ja.length();i++){
                        JSONObject objeto = ja.getJSONObject(i);
                        Disciplina disciplina = new Disciplina(objeto.getString("id_disciplina"),objeto.getString("nome_disciplina"),null,null,null,null,null,null,null);
                        listaDisciplinas.add(disciplina);
                        adapter.add(disciplina.getNome());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }, EditTarefaActivity.this);
        rq.add(cursosRequest);

        StringRequest sr = CRUD.selecionarEditar(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("tarefa");
                    JSONObject objeto = ja.getJSONObject(0);
                    descricao.setText(objeto.getString("descricao"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },EditTarefaActivity.this,id);
        rq.add(sr);
    }
}
