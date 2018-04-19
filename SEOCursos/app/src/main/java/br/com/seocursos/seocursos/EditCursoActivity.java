package br.com.seocursos.seocursos;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.seocursos.seocursos.Outros.CRUD;

public class EditCursoActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/cursos.php";
    private String id;

    TextInputEditText nome,area,cargaHoraria,preRequisitos,descricao,preco;
    RadioGroup tipo;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_curso);

        Intent intent = getIntent();
        try{
            id = intent.getStringExtra("id");
        }catch(NullPointerException e){
            e.printStackTrace();
            Toast.makeText(EditCursoActivity.this, "Curso não encontrado!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(EditCursoActivity.this, CursosActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

        nome = findViewById(R.id.nome);
        area = findViewById(R.id.area);
        cargaHoraria = findViewById(R.id.cargaHoraria);
        preRequisitos = findViewById(R.id.preRequisitos);
        descricao = findViewById(R.id.descricao);
        preco = findViewById(R.id.preco);
        tipo = findViewById(R.id.tipo);

        carregar();

        btn = findViewById(R.id.confirmar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,String> params = new HashMap<String,String>();
                params.put("idCurso",id);

                params.put("nome", nome.getText().toString());
                params.put("area", area.getText().toString());
                params.put("cargaHoraria", cargaHoraria.getText().toString());
                params.put("preRequisitos", preRequisitos.getText().toString());
                params.put("descricao", descricao.getText().toString());
                params.put("preco", preco.getText().toString());

                String tipoCurso;
                switch(tipo.getCheckedRadioButtonId()){
                    case R.id.gratis:
                        tipoCurso = "F";
                        break;
                    case R.id.tecnico:
                        tipoCurso = "T";
                        break;
                    case R.id.graduacao:
                        tipoCurso = "G";
                        break;
                    case R.id.posGraduacao:
                        tipoCurso = "P";
                        break;
                    default:
                        tipoCurso = "";
                        break;
                }
                params.put("tipo", tipoCurso);

                StringRequest sr = CRUD.editar(JSON_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            boolean enviado = jo.getBoolean("resposta");
                            if(enviado) {
                                Toast.makeText(EditCursoActivity.this, "Editado com sucesso!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(EditCursoActivity.this, "Falha na edição!", Toast.LENGTH_SHORT).show();
                            }
                            Intent i = new Intent(EditCursoActivity.this, CursosActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },params, EditCursoActivity.this);
                RequestQueue rq = VolleySingleton.getInstance(EditCursoActivity.this).getRequestQueue();
                rq.add(sr);
            }
        });
    }

    public void carregar(){
        StringRequest sr = CRUD.selecionarEditar(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("curso");
                    JSONObject objeto = ja.getJSONObject(0);

                    nome.setText(objeto.getString("nome_curso"));
                    area.setText(objeto.getString("area"));
                    cargaHoraria.setText(objeto.getString("carga_horaria"));
                    preRequisitos.setText(objeto.getString("prerequisito"));
                    descricao.setText(objeto.getString("descricao"));
                    preco.setText(objeto.getString("preco"));

                    switch(objeto.getString("tipo_curso")){
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
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        },EditCursoActivity.this, id);
        RequestQueue rq = VolleySingleton.getInstance(EditCursoActivity.this).getRequestQueue();
        rq.add(sr);
    }
}
