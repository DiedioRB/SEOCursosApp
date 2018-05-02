package br.com.seocursos.seocursos;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import br.com.seocursos.seocursos.ConstClasses.Curso;
import br.com.seocursos.seocursos.ConstClasses.Disciplina;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class EditDisciplinaActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/disciplinas.php";
    private String id;
    TextInputEditText nome,cargaHoraria,duracao,area;
    RadioGroup nivel,modalidade;
    Spinner cursos;
    Button btn;

    ArrayAdapter<String> adapter;
    ArrayList<Curso> listaCursos;

    String idCurso;
    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_disciplina);

        pd = new ProgressDialogHelper(EditDisciplinaActivity.this);

        Intent intent = getIntent();

        try{
            id = intent.getStringExtra("id");
        }catch(NullPointerException e){
            e.printStackTrace();
            Toast.makeText(EditDisciplinaActivity.this, "Disciplina não encontrada!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(EditDisciplinaActivity.this, DisciplinasActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

        nome = findViewById(R.id.nome);
        cargaHoraria = findViewById(R.id.cargaHoraria);
        duracao = findViewById(R.id.duracao);
        area = findViewById(R.id.area);

        nivel = findViewById(R.id.nivel);
        modalidade = findViewById(R.id.modalidade);
        btn = findViewById(R.id.confirmar);

        adapter = new ArrayAdapter<String>(EditDisciplinaActivity.this, R.layout.support_simple_spinner_dropdown_item);
        cursos = findViewById(R.id.cursos);
        listaCursos = new ArrayList<Curso>();

        carregar();
        cursos.setAdapter(adapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.open();
                Map<String,String> params = new HashMap<String,String>();
                params.put("idDisciplina", id);
                params.put("nome", nome.getText().toString());
                params.put("cargaHoraria", cargaHoraria.getText().toString());
                params.put("nivel", area.getText().toString());
                params.put("duracao", duracao.getText().toString());
                params.put("area", area.getText().toString());

                String nivelDisciplina,modalidadeDisciplina;
                switch(nivel.getCheckedRadioButtonId()){
                    case R.id.gratis:
                        nivelDisciplina = "F";
                        break;
                    case R.id.tecnico:
                        nivelDisciplina = "T";
                        break;
                    case R.id.graduacao:
                        nivelDisciplina = "G";
                        break;
                    case R.id.posGraduacao:
                        nivelDisciplina = "P";
                        break;
                    default:
                        nivelDisciplina = null;
                        break;
                }
                if(modalidade.getCheckedRadioButtonId() == R.id.semiPresencial){
                    modalidadeDisciplina = "1";
                }else{
                    modalidadeDisciplina = "2";
                }
                params.put("nivel", nivelDisciplina);
                params.put("modalidade", modalidadeDisciplina);

                Curso curso = listaCursos.get(cursos.getSelectedItemPosition());
                idCurso = curso.getId();
                params.put("idCurso", idCurso);
                StringRequest sr = CRUD.editar(JSON_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            boolean enviado = jo.getBoolean("resposta");
                            if(enviado) {
                                Toast.makeText(EditDisciplinaActivity.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(EditDisciplinaActivity.this, "Falha no cadastro!", Toast.LENGTH_SHORT).show();
                            }
                            Intent i = new Intent(EditDisciplinaActivity.this, DisciplinasActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },params,EditDisciplinaActivity.this);
                RequestQueue rq = VolleySingleton.getInstance(EditDisciplinaActivity.this).getRequestQueue();
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
        String url = "https://seocursos.com.br/PHP/Android/cursos.php";
        RequestQueue rq = VolleySingleton.getInstance(EditDisciplinaActivity.this).getRequestQueue();
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });

        StringRequest sr = CRUD.selecionarEditar(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("disciplina");
                    JSONObject objeto = ja.getJSONObject(0);
                    Disciplina disciplina = new Disciplina(objeto.getString("id_disciplina"), objeto.getString("nome_disciplina"),
                    objeto.getString("nivel"), objeto.getString("carga_horaria"),objeto.getString("area"),
                            objeto.getString("duracao"),objeto.getString("id_modalidade"),objeto.getString("id_curso"),
                            objeto.getString("nome_curso"));
                    nome.setText(disciplina.getNome());
                    cargaHoraria.setText(disciplina.getCargaHoraria());
                    duracao.setText(disciplina.getDuracao());
                    area.setText(disciplina.getArea());

                    switch(disciplina.getNivel()){
                        case "F":
                            ((RadioButton)findViewById(R.id.gratis)).setChecked(true);
                            break;
                        case "T":
                            ((RadioButton)findViewById(R.id.tecnico)).setChecked(true);
                            break;
                        case "G":
                            ((RadioButton)findViewById(R.id.graduacao)).setChecked(true);
                            break;
                        case "P":
                            ((RadioButton)findViewById(R.id.posGraduacao)).setChecked(true);
                            break;
                        default:
                            break;
                    }
                    switch(disciplina.getModalidade()){
                        case "1":
                            ((RadioButton)findViewById(R.id.semiPresencial)).setChecked(true);
                            break;
                        case "2":
                            ((RadioButton)findViewById(R.id.ead)).setChecked(true);
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },EditDisciplinaActivity.this, id);
        rq.add(sr);
        sr = CRUD.selecionar(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("cursos");
                    for(Integer i=0;i<ja.length();i++){
                        JSONObject objeto = ja.getJSONObject(i);
                        Curso curso = new Curso(objeto.getString("id_curso"),objeto.getString("nome_curso"),null,null,null,null,null,null);
                        listaCursos.add(curso);
                        adapter.add(curso.getNome());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch(NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }, getApplicationContext());

        rq.add(sr);
    }
}
