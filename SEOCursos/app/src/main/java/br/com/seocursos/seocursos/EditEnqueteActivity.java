package br.com.seocursos.seocursos;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class EditEnqueteActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/enquetes.php";
    private String id;

    TextInputEditText pergunta,respostaA,respostaB,respostaC,respostaD,respostaE;
    Button btn;

    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_enquete);

        pd = new ProgressDialogHelper(EditEnqueteActivity.this);

        pergunta = findViewById(R.id.pergunta);
        respostaA = findViewById(R.id.respostaA);
        respostaB = findViewById(R.id.respostaB);
        respostaC = findViewById(R.id.respostaC);
        respostaD = findViewById(R.id.respostaD);
        respostaE = findViewById(R.id.respostaE);

        btn = findViewById(R.id.confirmar);

        try {
            Intent i = getIntent();
            id = i.getStringExtra("id");
        }catch(NullPointerException e){
            e.printStackTrace();
            Intent i = new Intent(EditEnqueteActivity.this,EnquetesActivity.class);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.nenhumRegistroEnviado), Toast.LENGTH_SHORT).show();
            startActivity(i);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCampos()) {
                    pd.open();
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("id_enquete", id);
                    params.put("questao", pergunta.getText().toString());
                    params.put("valorA", respostaA.getText().toString());
                    params.put("valorB", respostaB.getText().toString());
                    params.put("valorC", respostaC.getText().toString());
                    params.put("valorD", respostaD.getText().toString());
                    params.put("valorE", respostaE.getText().toString());

                    StringRequest sr = CRUD.editar(JSON_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jo = new JSONObject(response);
                                boolean enviado = jo.getBoolean("resposta");
                                if (enviado) {
                                    Toast.makeText(EditEnqueteActivity.this, getResources().getString(R.string.editadoComSucesso), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditEnqueteActivity.this, getResources().getString(R.string.falhaEdicao), Toast.LENGTH_SHORT).show();
                                }
                                Intent i = new Intent(EditEnqueteActivity.this, EnquetesActivity.class);
                                startActivity(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, params, EditEnqueteActivity.this);
                    RequestQueue rq = VolleySingleton.getInstance(EditEnqueteActivity.this).getRequestQueue();
                    rq.add(sr);
                    rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            pd.close();
                        }
                    });
                }else{
                    Toast.makeText(EditEnqueteActivity.this, getResources().getString(R.string.preenchaOsCamposPrimeiro), Toast.LENGTH_SHORT).show();
                }
            }
        });

        carregar();
    }

    public void carregar(){
        pd.open();
        String url = "https://www.seocursos.com.br/PHP/Android/enquetes.php";

        StringRequest sr = CRUD.selecionarEditar(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("enquete");
                    for(int i=0;i<ja.length();i++){
                        JSONObject objeto = ja.getJSONObject(i);

                        String perguntaO,valorA,valorB,valorC,valorD,valorE;
                        perguntaO = objeto.getString("pergunta");
                        valorA = objeto.getString("valorA");
                        valorB = objeto.getString("valorB");
                        valorC = objeto.getString("valorC");
                        valorD = objeto.getString("valorD");
                        valorE = objeto.getString("valorE");

                        pergunta.setText(perguntaO);
                        respostaA.setText(valorA);
                        respostaB.setText(valorB);
                        respostaC.setText(valorC);
                        respostaD.setText(valorD);
                        respostaE.setText(valorE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },EditEnqueteActivity.this,id);
        RequestQueue rq = VolleySingleton.getInstance(EditEnqueteActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }
    public boolean validarCampos(){
        return !(pergunta.getText().toString().isEmpty() || respostaA.getText().toString().isEmpty() ||
                respostaB.getText().toString().isEmpty() || respostaC.getText().toString().isEmpty() ||
                respostaD.getText().toString().isEmpty() || respostaE.getText().toString().isEmpty());
    }
}
