package br.com.seocursos.seocursos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class RespostaEnqueteActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/enquetes.php";
    private String idEnquete;
    TextView titulo;
    RadioGroup radioGroup;
    RadioButton respostaA,respostaB,respostaC,respostaD,respostaE;
    TextView valorA,valorB,valorC,valorD,valorE;
    ProgressBar progressA,progressB,progressC,progressD,progressE;
    LinearLayout progress,answers;
    Button btn;

    SharedPreferencesHelper helper;
    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resposta_enquete);

        Intent i = getIntent();
        try{
            idEnquete = i.getStringExtra("id");
        }catch(NullPointerException e){
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.enqueteNaoEncontrada), Toast.LENGTH_SHORT).show();
            i = new Intent(RespostaEnqueteActivity.this, EnquetesActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

        helper = new SharedPreferencesHelper(RespostaEnqueteActivity.this);
        pd = new ProgressDialogHelper(RespostaEnqueteActivity.this);

        titulo = findViewById(R.id.titulo);
        radioGroup = findViewById(R.id.respostas);
        respostaA = findViewById(R.id.respostaA);
        respostaB = findViewById(R.id.respostaB);
        respostaC = findViewById(R.id.respostaC);
        respostaD = findViewById(R.id.respostaD);
        respostaE = findViewById(R.id.respostaE);

        valorA = findViewById(R.id.valorA);
        valorB = findViewById(R.id.valorB);
        valorC = findViewById(R.id.valorC);
        valorD = findViewById(R.id.valorD);
        valorE = findViewById(R.id.valorE);
        progressA = findViewById(R.id.progressA);
        progressB = findViewById(R.id.progressB);
        progressC = findViewById(R.id.progressC);
        progressD = findViewById(R.id.progressD);
        progressE = findViewById(R.id.progressE);

        progress = findViewById(R.id.progress);
        answers = findViewById(R.id.answers);

        btn = findViewById(R.id.enviar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarResposta();
            }
        });

        carregar();
    }
    public void carregar(){
        pd.open();

        final String idUsuario = helper.getString("id");

        Map<String,String> params = new HashMap<String,String>();
        params.put("pergunta", "pergunta");
        params.put("idEnquete", idEnquete);
        params.put("idUsuario", idUsuario);

        StringRequest sr = CRUD.customRequest(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    boolean temResposta = jo.getBoolean("temResposta");
                    if(temResposta || helper.getString("privilegio").equals("D")){
                        progress.setVisibility(View.VISIBLE);

                        JSONArray ja = jo.getJSONArray("votos");
                        JSONArray enqueteArray = jo.getJSONArray("enquete");
                        JSONObject enquete = enqueteArray.getJSONObject(0);
                        titulo.setText(enquete.getString("pergunta"));

                        int votos[] = {0,0,0,0,0};
                        int totalVotos = 0;

                        for(int i=0;i<ja.length();i++){
                            JSONObject objeto = ja.getJSONObject(i);

                            int voto = objeto.getInt("voto")-1;
                            int quantidadeVotos = objeto.getInt("quantidade");
                            votos[voto] = quantidadeVotos;
                            totalVotos += quantidadeVotos;
                        }

                        int porcentagemA=0,porcentagemB=0,porcentagemC=0,porcentagemD=0,porcentagemE=0;
                        try {
                            porcentagemA = (votos[0] * 100) / totalVotos;
                            porcentagemB = (votos[1] * 100) / totalVotos;
                            porcentagemC = (votos[2] * 100) / totalVotos;
                            porcentagemD = (votos[3] * 100) / totalVotos;
                            porcentagemE = (votos[4] * 100) / totalVotos;
                        }catch(ArithmeticException e){}

                        String textA = enquete.getString("valorA")+"("+porcentagemA+"%)";
                        String textB = enquete.getString("valorB")+"("+porcentagemB+"%)";
                        String textC = enquete.getString("valorC")+"("+porcentagemC+"%)";
                        String textD = enquete.getString("valorD")+"("+porcentagemD+"%)";
                        String textE = enquete.getString("valorE")+"("+porcentagemE+"%)";

                        valorA.setText(textA);
                        valorB.setText(textB);
                        valorC.setText(textC);
                        valorD.setText(textD);
                        valorE.setText(textE);

                        progressA.setProgress(porcentagemA);
                        progressB.setProgress(porcentagemB);
                        progressC.setProgress(porcentagemC);
                        progressD.setProgress(porcentagemD);
                        progressE.setProgress(porcentagemE);
                    }else{
                        answers.setVisibility(View.VISIBLE);

                        JSONArray ja = jo.getJSONArray("enquete");
                        JSONObject objeto = ja.getJSONObject(0);

                        titulo.setText(objeto.getString("pergunta"));
                        respostaA.setText(objeto.getString("valorA"));
                        respostaB.setText(objeto.getString("valorB"));
                        respostaC.setText(objeto.getString("valorC"));
                        respostaD.setText(objeto.getString("valorD"));
                        respostaE.setText(objeto.getString("valorE"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, RespostaEnqueteActivity.this, params);
        RequestQueue rq = VolleySingleton.getInstance(RespostaEnqueteActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }
    public void enviarResposta(){
        pd.open();
        String idUsuario = helper.getString("id");

        String voto;
        switch(radioGroup.getCheckedRadioButtonId()){
            case R.id.respostaA:
                voto = "1";
                break;
            case R.id.respostaB:
                voto = "2";
                break;
            case R.id.respostaC:
                voto = "3";
                break;
            case R.id.respostaD:
                voto = "4";
                break;
            case R.id.respostaE:
                voto = "5";
                break;
            default:
                voto = "1";
                break;
        }

        Map<String,String> params = new HashMap<String,String>();
        params.put("resposta","resposta");
        params.put("idUsuario", idUsuario);
        params.put("idEnquete", idEnquete);
        params.put("voto", voto);

        StringRequest sr = CRUD.customRequest(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    boolean enviado = jo.getBoolean("resposta");
                    if(enviado){
                        Toast.makeText(RespostaEnqueteActivity.this, getResources().getString(R.string.enviadoComSucesso), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(RespostaEnqueteActivity.this, getResources().getString(R.string.falhaEnvio), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent i = new Intent(RespostaEnqueteActivity.this, EnquetesActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        },RespostaEnqueteActivity.this, params);
        RequestQueue rq = VolleySingleton.getInstance(RespostaEnqueteActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }
}
