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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class AddEnqueteActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/enquetes.php";

    TextInputEditText pergunta,respostaA,respostaB,respostaC,respostaD,respostaE;
    Button btn;

    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_enquete);

        pd = new ProgressDialogHelper(AddEnqueteActivity.this);

        pergunta = findViewById(R.id.pergunta);
        respostaA = findViewById(R.id.respostaA);
        respostaB = findViewById(R.id.respostaB);
        respostaC = findViewById(R.id.respostaC);
        respostaD = findViewById(R.id.respostaD);
        respostaE = findViewById(R.id.respostaE);

        btn = findViewById(R.id.confirmar);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.open();
                Map<String,String> params = new HashMap<String,String>();

                params.put("pergunta", pergunta.getText().toString());
                params.put("valorA", respostaA.getText().toString());
                params.put("valorB", respostaB.getText().toString());
                params.put("valorC", respostaC.getText().toString());
                params.put("valorD", respostaD.getText().toString());
                params.put("valorE", respostaE.getText().toString());

                StringRequest sr = CRUD.inserir(JSON_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            boolean enviado = jo.getBoolean("resposta");
                            if(enviado) {
                                Toast.makeText(AddEnqueteActivity.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(AddEnqueteActivity.this, "Falha no cadastro!", Toast.LENGTH_SHORT).show();
                            }
                            Intent i = new Intent(AddEnqueteActivity.this, TarefasActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },params,AddEnqueteActivity.this);
                RequestQueue rq = VolleySingleton.getInstance(AddEnqueteActivity.this).getRequestQueue();
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
