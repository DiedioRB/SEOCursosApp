package br.com.seocursos.seocursos;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class AddDisciplinaActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/disciplinas.php";
    TextInputEditText nome,cargaHoraria,duracao,area;
    RadioGroup nivel,modalidade;
    Spinner cursos, tutores;
    Button btn;

    ArrayAdapter<String> adapterCursos;
    ArrayAdapter<String> adapterTutores;
    ArrayList<Curso> listaCursos;
    ArrayList<String> listaTutores;

    String id;
    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_disciplina);

        pd = new ProgressDialogHelper(AddDisciplinaActivity.this);

        nome = findViewById(R.id.nome);
        cargaHoraria = findViewById(R.id.cargaHoraria);
        duracao = findViewById(R.id.duracao);
        area = findViewById(R.id.area);

        nivel = findViewById(R.id.nivel);
        modalidade = findViewById(R.id.modalidade);
        btn = findViewById(R.id.confirmar);

        adapterCursos = new ArrayAdapter<String>(AddDisciplinaActivity.this, R.layout.support_simple_spinner_dropdown_item);
        adapterTutores = new ArrayAdapter<String>(AddDisciplinaActivity.this, R.layout.support_simple_spinner_dropdown_item);
        cursos = findViewById(R.id.cursos);
        tutores = findViewById(R.id.tutores);
        listaCursos = new ArrayList<Curso>();
        listaTutores = new ArrayList<String>();

        carregar();
        cursos.setAdapter(adapterCursos);
        tutores.setAdapter(adapterTutores);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.open();
                Map<String,String> params = new HashMap<String,String>();
                params.put("nome", nome.getText().toString());
                params.put("cargaHoraria", cargaHoraria.getText().toString());
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
                id = curso.getId();
                params.put("id_curso", id);

                String idTutor = listaTutores.get(tutores.getSelectedItemPosition());
                params.put("idTutor", idTutor);

                StringRequest sr = CRUD.inserir(JSON_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            boolean enviado = jo.getBoolean("resposta");
                            if(enviado) {
                                Toast.makeText(AddDisciplinaActivity.this, getResources().getString(R.string.cadastradoComSucesso), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(AddDisciplinaActivity.this, getResources().getString(R.string.falhaCadastro), Toast.LENGTH_SHORT).show();
                            }
                            Intent i = new Intent(AddDisciplinaActivity.this, DisciplinasActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },params,AddDisciplinaActivity.this);
                RequestQueue rq = VolleySingleton.getInstance(AddDisciplinaActivity.this).getRequestQueue();
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
        StringRequest sr = CRUD.selecionar(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("cursos");
                    for(Integer i=0;i<ja.length();i++){
                        JSONObject objeto = ja.getJSONObject(i);
                        Curso curso = new Curso(objeto.getString("idCurso"),objeto.getString("nome_curso"),null,null,null,null,null,null);
                        listaCursos.add(curso);
                        adapterCursos.add(curso.getNome());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch(NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }, getApplicationContext());

        RequestQueue rq = VolleySingleton.getInstance(AddDisciplinaActivity.this).getRequestQueue();
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });

        rq.add(sr);

        url = "https://www.seocursos.com.br/PHP/Android/disciplinas.php";
        Map<String,String> params = new HashMap<>();
        params.put("getTutores", "getTutores");

        sr = CRUD.customRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("tutores");
                    for(int i=0;i<ja.length();i++){
                        JSONObject objeto = ja.getJSONObject(i);
                        listaTutores.add(objeto.getString("idUsuario"));
                        adapterTutores.add(objeto.getString("nome"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, AddDisciplinaActivity.this, params);

        rq.add(sr);
    }
}
