package br.com.seocursos.seocursos;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class EditCursoActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/cursos.php";
    private String id;

    LinearLayout formF, formT, formG, formP;
    TextInputEditText nome,area,cargaHoraria,preRequisitos,descricao,preco, dataDisponivel;
    RadioGroup tipo;
    Button btn;

    TextInputEditText disponivelAte, nivel, duracaoT, duracaoG, notaMecG, duracaoP, notaMecP;
    RadioGroup modalidadeT, modalidadeG, titulacao, modalidadeP, status;

    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_curso);

        pd = new ProgressDialogHelper(EditCursoActivity.this);

        Intent intent = getIntent();
        try{
            id = intent.getStringExtra("id");
        }catch(NullPointerException e){
            e.printStackTrace();
            Toast.makeText(EditCursoActivity.this, getResources().getString(R.string.cursoNaoEncontrado), Toast.LENGTH_SHORT).show();
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

        formF = findViewById(R.id.formGratis);
        formT = findViewById(R.id.formTecnico);
        formG = findViewById(R.id.formGraduacao);
        formP = findViewById(R.id.formPosGraduacao);

        disponivelAte = findViewById(R.id.disponivel);
        nivel = findViewById(R.id.nivel);
        duracaoT = findViewById(R.id.duracaoT);
        duracaoG = findViewById(R.id.duracaoG);
        duracaoP = findViewById(R.id.duracaoP);
        notaMecG = findViewById(R.id.notaMecG);
        notaMecP = findViewById(R.id.notaMecP);

        tipo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.gratis:
                        formF.setVisibility(View.VISIBLE);
                        formT.setVisibility(View.GONE);
                        formG.setVisibility(View.GONE);
                        formP.setVisibility(View.GONE);
                        break;
                    case R.id.tecnico:
                        formF.setVisibility(View.GONE);
                        formT.setVisibility(View.VISIBLE);
                        formG.setVisibility(View.GONE);
                        formP.setVisibility(View.GONE);
                        break;
                    case R.id.graduacao:
                        formF.setVisibility(View.GONE);
                        formT.setVisibility(View.GONE);
                        formG.setVisibility(View.VISIBLE);
                        formP.setVisibility(View.GONE);
                        break;
                    case R.id.posGraduacao:
                        formF.setVisibility(View.GONE);
                        formT.setVisibility(View.GONE);
                        formG.setVisibility(View.GONE);
                        formP.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });

        dataDisponivel = findViewById(R.id.disponivel);

        MaskEditTextChangedListener dataMask = new MaskEditTextChangedListener("##/##/####", dataDisponivel);
        dataDisponivel.addTextChangedListener(dataMask);

        carregar();

        btn = findViewById(R.id.confirmar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.open();
                Map<String,String> params = new HashMap<String,String>();
                params.put("idCurso",id);

                params.put("nome", nome.getText().toString());
                params.put("area", area.getText().toString());
                params.put("cargaHoraria", cargaHoraria.getText().toString());
                params.put("preRequisitos", preRequisitos.getText().toString());
                params.put("descricao", descricao.getText().toString());
                params.put("preco", preco.getText().toString());

                String tipoCurso, modalidade;
                RadioGroup modalidadeGroup;
                switch(tipo.getCheckedRadioButtonId()){
                    case R.id.gratis:
                        tipoCurso = "F";

                        params.put("tempoDisponivel", ((TextInputEditText)findViewById(R.id.disponivel)).getText().toString());
                        params.put("nivel", ((TextInputEditText)findViewById(R.id.nivel)).getText().toString());
                        break;
                    case R.id.tecnico:
                        tipoCurso = "T";

                        modalidadeGroup = findViewById(R.id.modalidadeT);
                        if(modalidadeGroup.getCheckedRadioButtonId() == R.id.semipresencialT){
                            modalidade = "1";
                        }else{
                            modalidade = "2";
                        }

                        params.put("modalidade", modalidade);
                        params.put("duracao", ((TextInputEditText)findViewById(R.id.duracaoT)).getText().toString());
                        break;
                    case R.id.graduacao:
                        tipoCurso = "G";

                        modalidadeGroup = findViewById(R.id.modalidadeG);
                        if(modalidadeGroup.getCheckedRadioButtonId() == R.id.semipresencialG){
                            modalidade = "1";
                        }else{
                            modalidade = "2";
                        }
                        String titulacao;
                        RadioGroup titulacaoGroup = findViewById(R.id.titulacao);
                        if(titulacaoGroup.getCheckedRadioButtonId() == R.id.licenciado){
                            titulacao = "L";
                        }else{
                            titulacao = "B";
                        }

                        params.put("modalidade", modalidade);
                        params.put("titulacao", titulacao);
                        params.put("duracao", ((TextInputEditText)findViewById(R.id.duracaoG)).getText().toString());
                        params.put("notaMec", ((TextInputEditText)findViewById(R.id.notaMecG)).getText().toString());
                        break;
                    case R.id.posGraduacao:
                        tipoCurso = "P";

                        modalidadeGroup = findViewById(R.id.modalidadeP);
                        if(modalidadeGroup.getCheckedRadioButtonId() == R.id.semipresencialP){
                            modalidade = "1";
                        }else{
                            modalidade = "2";
                        }
                        String status;
                        RadioGroup statusGroup = findViewById(R.id.titulacao);
                        if(statusGroup.getCheckedRadioButtonId() == R.id.licenciado){
                            status = "L";
                        }else{
                            status = "B";
                        }

                        params.put("modalidade", modalidade);
                        params.put("status", status);
                        params.put("duracao", ((TextInputEditText)findViewById(R.id.duracaoP)).getText().toString());
                        params.put("notaMec", ((TextInputEditText)findViewById(R.id.notaMecP)).getText().toString());
                        break;
                    default:
                        tipoCurso = "";
                        break;
                }
                params.put("tipo", tipoCurso);

                StringRequest sr = CRUD.editar(JSON_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(EditCursoActivity.this, response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jo = new JSONObject(response);
                            boolean enviado = jo.getBoolean("resposta");
                            if(enviado) {
                                Toast.makeText(EditCursoActivity.this, getResources().getString(R.string.editadoComSucesso), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(EditCursoActivity.this, getResources().getString(R.string.falhaEdicao), Toast.LENGTH_SHORT).show();
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

                    String duracao;
                    switch(objeto.getString("tipo_curso")){
                        case "F":
                            ((RadioButton)findViewById(R.id.gratis)).setChecked(true);
                            String disponivelAteO = objeto.getString("tempo_disponivel");
                            String nivelO = objeto.getString("nivel");

                            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                            ParsePosition pos = new ParsePosition(0);
                            Date data = formato.parse(disponivelAteO,pos);
                            formato = new SimpleDateFormat("dd/MM/yyyy");
                            String date = formato.format(data);

                            disponivelAte.setText(date);
                            nivel.setText(nivelO);

                            break;
                        case "T":
                            ((RadioButton)findViewById(R.id.tecnico)).setChecked(true);

                            duracao = objeto.getString("duracaoT");
                            duracaoT.setText(duracao);

                            String modalidadeT = objeto.getString("modalidadeT");
                            if(modalidadeT.equals("1")) {
                                ((RadioButton)findViewById(R.id.semipresencialT)).setChecked(true);
                            }else{
                                ((RadioButton)findViewById(R.id.eadT)).setChecked(true);
                            }
                            break;
                        case "G":
                            ((RadioButton)findViewById(R.id.graduacao)).setChecked(true);

                            duracao = objeto.getString("duracaoG");
                            duracaoG.setText(duracao);

                            String notaMecGO = objeto.getString("notaMecG");
                            if(!notaMecGO.equals("null")) {
                                notaMecG.setText(notaMecGO);
                            }

                            String modalidadeG = objeto.getString("modalidadeG");
                            if(modalidadeG.equals("1")) {
                                ((RadioButton)findViewById(R.id.semipresencialG)).setChecked(true);
                            }else{
                                ((RadioButton)findViewById(R.id.eadG)).setChecked(true);
                            }

                            String titulacaoO = objeto.getString("titulacao");
                            if(titulacaoO.equals("L")){
                                ((RadioButton)findViewById(R.id.licenciado)).setChecked(true);
                            }else{
                                ((RadioButton)findViewById(R.id.bacharel)).setChecked(true);
                            }
                            break;
                        case "P":
                            ((RadioButton)findViewById(R.id.posGraduacao)).setChecked(true);

                            duracao = objeto.getString("duracaoP");
                            duracaoP.setText(duracao);

                            String notaMecPO = objeto.getString("notaMecP");
                            if(!notaMecPO.equals("null")) {
                                notaMecP.setText(notaMecPO);
                            }

                            String modalidadeP = objeto.getString("modalidadeP");
                            if(modalidadeP.equals("1")) {
                                ((RadioButton)findViewById(R.id.semipresencialP)).setChecked(true);
                            }else{
                                ((RadioButton)findViewById(R.id.eadP)).setChecked(true);
                            }

                            String statusO = objeto.getString("estado");
                            if(statusO.equals("L")){
                                ((RadioButton)findViewById(R.id.latoSensu)).setChecked(true);
                            }else{
                                ((RadioButton)findViewById(R.id.strictuSensu)).setChecked(true);
                            }
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
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }
}
