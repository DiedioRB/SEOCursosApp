package br.com.seocursos.seocursos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class InfoCursoActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/cursos.php";
    private String id;
    TextView titulo, nivel, preco, cargaHoraria, area, preRequisitos, descricao;
    Button inscreverSe, voltar;

    ProgressDialogHelper pd;
    SharedPreferencesHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_curso);

        pd = new ProgressDialogHelper(InfoCursoActivity.this);
        helper = new SharedPreferencesHelper(InfoCursoActivity.this);

        titulo = findViewById(R.id.titulo);
        nivel = findViewById(R.id.nivel);
        preco = findViewById(R.id.preco);
        cargaHoraria = findViewById(R.id.cargaHoraria);
        area = findViewById(R.id.area);
        preRequisitos = findViewById(R.id.preRequisitos);
        descricao = findViewById(R.id.descricao);

        inscreverSe = findViewById(R.id.inscreverSe);
        voltar = findViewById(R.id.voltar);

        inscreverSe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InfoCursoActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Confirmar Inscrição");
                builder.setMessage("Deseja realmente cadastrar-se em "+titulo.getText().toString()+"?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        inscricao();
                    }
                }).setNegativeButton("Não", null);

                builder.create().show();
            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InfoCursoActivity.this, CursosActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        Intent i = getIntent();
        try{
            this.id = i.getStringExtra("id");
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        carregar();
    }
    public void carregar(){
        pd.open();
        StringRequest sr = CRUD.selecionarEditar(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("curso");
                    JSONObject objeto = ja.getJSONObject(0);

                    String curso, nivelCurso;
                    curso = objeto.getString("nome_curso");
                    nivelCurso = objeto.getString("tipo_curso");
                    switch(nivelCurso){
                        case "F":
                            nivel.setText(" "+getString(R.string.gratis));
                            break;
                        case "T":
                            nivel.setText(" "+getString(R.string.tecnico));
                            break;
                        case "G":
                            nivel.setText(" "+getString(R.string.graduacao));
                            break;
                        case "P":
                            nivel.setText(" "+getString(R.string.posGraduacao));
                            break;
                    }

                    titulo.setText(" "+curso);
                    preco.setText(" R$ "+objeto.getString("preco"));
                    cargaHoraria.setText(" "+objeto.getString("carga_horaria")+" horas");
                    area.setText(" "+objeto.getString("area"));
                    preRequisitos.setText(" "+objeto.getString("prerequisito"));
                    descricao.setText(" "+objeto.getString("descricao"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, InfoCursoActivity.this, id);
        RequestQueue rq = VolleySingleton.getInstance(InfoCursoActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }
    public void inscricao(){
        pd.open();
        Map<String,String> params = new HashMap<String,String>();
        params.put("inscricao", "inscricao");
        params.put("idUsuario", helper.getString("id"));
        params.put("idCurso", id);

        StringRequest sr = CRUD.customRequest(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    boolean enviado = jo.getBoolean("resposta");
                    if(enviado){
                        Toast.makeText(InfoCursoActivity.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    }else{
                        String error = jo.getString("error");
                        Toast.makeText(InfoCursoActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                    Intent i = new Intent(InfoCursoActivity.this, CursosActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },InfoCursoActivity.this, params);
        RequestQueue rq = VolleySingleton.getInstance(InfoCursoActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }
}
