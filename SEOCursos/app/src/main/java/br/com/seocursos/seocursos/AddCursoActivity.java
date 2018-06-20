package br.com.seocursos.seocursos;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class AddCursoActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/cursos.php";
    LinearLayout formF, formT, formG, formP;
    TextInputEditText nome,area,cargaHoraria,preRequisitos,descricao,preco,dataDisponivel;
    TextInputEditText nivel,duracaoT, duracaoG, notaMecG, duracaoP, notaMecP;
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

        formF = findViewById(R.id.formGratis);
        formT = findViewById(R.id.formTecnico);
        formG = findViewById(R.id.formGraduacao);
        formP = findViewById(R.id.formPosGraduacao);

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

        btn = findViewById(R.id.confirmar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCampos()) {
                    pd.open();
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("nome", nome.getText().toString());
                    params.put("area", area.getText().toString());
                    params.put("cargaHoraria", cargaHoraria.getText().toString());
                    params.put("preRequisitos", preRequisitos.getText().toString());
                    params.put("descricao", descricao.getText().toString());
                    params.put("preco", preco.getText().toString());

                    String tipoCurso, modalidade;
                    RadioGroup modalidadeGroup;
                    switch (tipo.getCheckedRadioButtonId()) {
                        case R.id.gratis:
                            tipoCurso = "F";

                            params.put("tempoDisponivel", dataDisponivel.getText().toString());
                            params.put("nivel", nivel.getText().toString());
                            break;
                        case R.id.tecnico:
                            tipoCurso = "T";

                            modalidadeGroup = findViewById(R.id.modalidadeT);
                            if (modalidadeGroup.getCheckedRadioButtonId() == R.id.semipresencialT) {
                                modalidade = "1";
                            } else {
                                modalidade = "2";
                            }

                            params.put("modalidade", modalidade);
                            params.put("duracao", duracaoT.getText().toString());
                            break;
                        case R.id.graduacao:
                            tipoCurso = "G";

                            modalidadeGroup = findViewById(R.id.modalidadeG);
                            if (modalidadeGroup.getCheckedRadioButtonId() == R.id.semipresencialG) {
                                modalidade = "1";
                            } else {
                                modalidade = "2";
                            }
                            String titulacao;
                            RadioGroup titulacaoGroup = findViewById(R.id.titulacao);
                            if (titulacaoGroup.getCheckedRadioButtonId() == R.id.licenciado) {
                                titulacao = "L";
                            } else {
                                titulacao = "B";
                            }

                            params.put("modalidade", modalidade);
                            params.put("titulacao", titulacao);
                            params.put("duracao", duracaoG.getText().toString());
                            params.put("notaMec", notaMecG.getText().toString());
                            break;
                        case R.id.posGraduacao:
                            tipoCurso = "P";

                            modalidadeGroup = findViewById(R.id.modalidadeP);
                            if (modalidadeGroup.getCheckedRadioButtonId() == R.id.semipresencialP) {
                                modalidade = "1";
                            } else {
                                modalidade = "2";
                            }
                            String status;
                            RadioGroup statusGroup = findViewById(R.id.titulacao);
                            if (statusGroup.getCheckedRadioButtonId() == R.id.licenciado) {
                                status = "L";
                            } else {
                                status = "B";
                            }

                            params.put("modalidade", modalidade);
                            params.put("status", status);
                            params.put("duracao", duracaoP.getText().toString());
                            params.put("notaMec", notaMecP.getText().toString());
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
                                if (enviado) {
                                    Toast.makeText(AddCursoActivity.this, getResources().getString(R.string.cadastradoComSucesso), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddCursoActivity.this, getResources().getString(R.string.falhaCadastro), Toast.LENGTH_SHORT).show();
                                }
                                Intent i = new Intent(AddCursoActivity.this, CursosActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, params, AddCursoActivity.this);
                    RequestQueue rq = VolleySingleton.getInstance(AddCursoActivity.this).getRequestQueue();
                    rq.add(sr);
                    rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            pd.close();
                        }
                    });
                }else{
                    Toast.makeText(AddCursoActivity.this, getResources().getString(R.string.preenchaOsCamposPrimeiro), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean validarCampos(){
        if(!(nome.getText().toString().isEmpty() || area.getText().toString().isEmpty() ||
                cargaHoraria.getText().toString().isEmpty() || preRequisitos.getText().toString().isEmpty() ||
                descricao.getText().toString().isEmpty() || preco.getText().toString().isEmpty()
                || dataDisponivel.getText().toString().isEmpty())){
            boolean isValid = false;
            switch(tipo.getCheckedRadioButtonId()){
                case R.id.gratis:
                    if(!(dataDisponivel.getText().toString().isEmpty() || nivel.getText().toString().isEmpty())){
                        isValid = true;
                    }
                    break;
                case R.id.tecnico:
                    if(!(duracaoT.getText().toString().isEmpty())){
                        isValid = true;
                    }
                    break;
                case R.id.graduacao:
                    if(!(duracaoG.getText().toString().isEmpty() || notaMecG.getText().toString().isEmpty())){
                        isValid = true;
                    }
                    break;
                case R.id.posGraduacao:
                    if(!(duracaoP.getText().toString().isEmpty() || notaMecP.getText().toString().isEmpty())){
                        isValid = true;
                    }
                    break;
            }
            return isValid;
        }else{
            return false;
        }
    }
}
