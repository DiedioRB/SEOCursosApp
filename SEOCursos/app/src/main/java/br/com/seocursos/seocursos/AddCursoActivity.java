package br.com.seocursos.seocursos;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class AddCursoActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/cursos.php";
    TextInputEditText nome,area,cargaHoraria,preRequisitos,descricao,preco;
    RadioGroup tipo;
    Button btn;

    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_curso);

        pd = new ProgressDialogHelper(AddCursoActivity.this);

        nome = findViewById(R.id.nome);
        area = findViewById(R.id.area);
        cargaHoraria = findViewById(R.id.cargaHoraria);
        preRequisitos = findViewById(R.id.preRequisitos);
        descricao = findViewById(R.id.descricao);
        preco = findViewById(R.id.preco);
        tipo = findViewById(R.id.tipo);

        btn = findViewById(R.id.confirmar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.open();
                Map<String,String> params = new HashMap<String,String>();

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

                StringRequest sr = CRUD.inserir(JSON_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            boolean enviado = jo.getBoolean("resposta");
                            if(enviado) {
                                Toast.makeText(AddCursoActivity.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(AddCursoActivity.this, "Falha no cadastro!", Toast.LENGTH_SHORT).show();
                            }
                            Intent i = new Intent(AddCursoActivity.this, CursosActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },params, AddCursoActivity.this);
                RequestQueue rq = VolleySingleton.getInstance(AddCursoActivity.this).getRequestQueue();
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
}
