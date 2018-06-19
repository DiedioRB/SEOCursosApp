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
import br.com.seocursos.seocursos.ConstClasses.CursoGraduacao;
import br.com.seocursos.seocursos.ConstClasses.CursoGratis;
import br.com.seocursos.seocursos.ConstClasses.CursoTecnico;
import br.com.seocursos.seocursos.ConstClasses.Disciplina;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class EditDisciplinaActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/disciplinas.php";
    private String id;

    TextInputEditText nome,cargaHoraria,duracao,area;
    RadioGroup nivel,modalidade;
    Spinner cursos,tutores;
    Button btn;

    ArrayAdapter<String> adapterCursos;
    ArrayAdapter<String> adapterTutores;
    ArrayList<Curso> listaCursos;
    ArrayList<String> listaTutores;

    private String idCurso;
    private String idTutor;
    private int spinnerSelectCurso = 0;
    private int spinnerSelectTutor = 0;

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
            Toast.makeText(EditDisciplinaActivity.this, getResources().getString(R.string.disciplina), Toast.LENGTH_SHORT).show();
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

        adapterCursos = new ArrayAdapter<String>(EditDisciplinaActivity.this, R.layout.support_simple_spinner_dropdown_item);
        adapterTutores = new ArrayAdapter<String>(EditDisciplinaActivity.this, R.layout.support_simple_spinner_dropdown_item);
        cursos = findViewById(R.id.cursos);
        tutores = findViewById(R.id.tutores);
        listaCursos = new ArrayList<Curso>();
        listaTutores = new ArrayList<String>();

        carregar();
        cursos.setAdapter(adapterCursos);
        tutores.setAdapter(adapterTutores);

    //    tutores.postDelayed(new Runnable() {
    //        @Override
    //        public void run() {
//
    //        }
    //    }, 900);

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

                String idTutor = listaTutores.get(tutores.getSelectedItemPosition());
                params.put("idTutor", idTutor);

                StringRequest sr = CRUD.editar(JSON_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            boolean enviado = jo.getBoolean("resposta");
                            if(enviado) {
                                Toast.makeText(EditDisciplinaActivity.this, getResources().getString(R.string.editadoComSucesso), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(EditDisciplinaActivity.this, getResources().getString(R.string.falhaEdicao), Toast.LENGTH_SHORT).show();
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
        final String url = "https://seocursos.com.br/PHP/Android/cursos.php";
        final RequestQueue rq = VolleySingleton.getInstance(EditDisciplinaActivity.this).getRequestQueue();
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
                            objeto.getString("nome_curso"), objeto.getString("id_usuario"));
                    nome.setText(disciplina.getNome());
                    cargaHoraria.setText(disciplina.getCargaHoraria());
                    duracao.setText(disciplina.getDuracao());
                    area.setText(disciplina.getArea());

                    idCurso = disciplina.getIdCurso();
                    idTutor = disciplina.getIdTutor();

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
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                rq.removeRequestFinishedListener(this);
                StringRequest sr = CRUD.selecionar(url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            JSONArray ja = jo.getJSONArray("cursos");
                            for(Integer i=0;i<ja.length();i++){
                                JSONObject objeto = ja.getJSONObject(i);
                                Curso curso = new Curso(objeto.getString("idCurso"),objeto.getString("nome_curso"),
                                        null,null,null,null,null,null);
                                listaCursos.add(curso);
                                adapterCursos.add(curso.getNome());

                                if(idCurso.equals(curso.getId())){
                                    spinnerSelectCurso = adapterCursos.getPosition(curso.getNome());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch(NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }, getApplicationContext());
                rq.add(sr);
                rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                    @Override
                    public void onRequestFinished(Request<Object> request) {
                        rq.removeRequestFinishedListener(this);

                        Map<String,String> params = new HashMap<>();
                        params.put("getTutores", "getTutores");
                        StringRequest sr = CRUD.customRequest(JSON_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jo = new JSONObject(response);
                                    JSONArray ja = jo.getJSONArray("tutores");
                                    for(int i=0;i<ja.length();i++){
                                        JSONObject objeto = ja.getJSONObject(i);
                                        listaTutores.add(objeto.getString("idUsuario"));
                                        adapterTutores.add(objeto.getString("nome"));

                                        if(idTutor.equals(objeto.getString("idUsuario"))){
                                            spinnerSelectTutor = adapterTutores.getPosition(objeto.getString("nome"));
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, EditDisciplinaActivity.this, params);
                        rq.add(sr);

                        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                            @Override
                            public void onRequestFinished(Request<Object> request) {
                                rq.removeRequestFinishedListener(this);
                                cursos.setSelection(spinnerSelectCurso);
                                tutores.setSelection(spinnerSelectTutor);
                            }
                        });
                    }
                });
            }
        });
    }
}
