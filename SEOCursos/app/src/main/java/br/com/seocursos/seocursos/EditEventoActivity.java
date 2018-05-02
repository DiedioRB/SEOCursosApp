package br.com.seocursos.seocursos;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.seocursos.seocursos.ConstClasses.Evento;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class EditEventoActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/eventos.php";
    private String id;
    
    TextInputEditText nome,local,dia,telefone,preco;
    CheckBox dinheiro,cartaoCredito,cartaoDebito;
    Button btn;

    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_evento);

        pd = new ProgressDialogHelper(EditEventoActivity.this);

        Intent intent = getIntent();
        try{
            id = intent.getStringExtra("id");
            carregar();
        }catch(NullPointerException e){
            e.printStackTrace();
            Toast.makeText(EditEventoActivity.this, "Evento não encontrado!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(EditEventoActivity.this, EventosActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

        nome = findViewById(R.id.nome);
        local = findViewById(R.id.local);
        dia = findViewById(R.id.dia);
        telefone = findViewById(R.id.telefone);
        preco = findViewById(R.id.preco);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            telefone.addTextChangedListener(new PhoneNumberFormattingTextWatcher("BR"));
        }

        dinheiro = findViewById(R.id.dinheiro);
        cartaoCredito = findViewById(R.id.cartaoCredito);
        cartaoDebito = findViewById(R.id.cartaoDebito);

        btn = findViewById(R.id.confirmar);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.open();
                Map<String,String> params = new HashMap<String,String>();

                try {
                    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                    Date data = null;
                    data = formato.parse(dia.getText().toString());
                    formato = new SimpleDateFormat("yyyy-MM-dd");
                    String dia = formato.format(data);
                    params.put("idEvento", id);
                    params.put("nome_evento", nome.getText().toString());
                    params.put("lugar", local.getText().toString());
                    params.put("dia", dia);
                    params.put("telefone", telefone.getText().toString());
                    params.put("valor", preco.getText().toString());

                    String dinheiroP = "", cartaoCreditoP = "", cartaoDebitoP = "";

                    if (dinheiro.isChecked()) {
                        dinheiroP = "Dinheiro";
                    }
                    if (cartaoCredito.isChecked()) {
                        cartaoCreditoP = "Cartão de Crédito";
                    }
                    if (cartaoDebito.isChecked()) {
                        cartaoDebitoP = "Cartão de Débito";
                    }

                    params.put("dinheiro", dinheiroP);
                    params.put("cartaoCredito", cartaoCreditoP);
                    params.put("cartaoDebito", cartaoDebitoP);
                    StringRequest sr = CRUD.editar(JSON_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jo = new JSONObject(response);
                                boolean enviado = jo.getBoolean("resposta");
                                if (enviado) {
                                    Toast.makeText(EditEventoActivity.this, "Editado com sucesso!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditEventoActivity.this, "Falha na edição!", Toast.LENGTH_SHORT).show();
                                }
                                Intent i = new Intent(EditEventoActivity.this, EventosActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, params, EditEventoActivity.this);
                    RequestQueue rq = VolleySingleton.getInstance(EditEventoActivity.this).getRequestQueue();
                    rq.add(sr);
                    rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            pd.close();
                        }
                    });
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void carregar(){
        pd.open();
        StringRequest sr = CRUD.selecionarEditar(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("evento");
                    JSONObject objeto = ja.getJSONObject(0);
                    Evento evento = new Evento(objeto.getString("id_evento"),objeto.getString("nome_evento"),
                            objeto.getString("lugar"), objeto.getString("dia"), objeto.getString("telefone"),
                            objeto.getString("valor"), objeto.getString("forma_pagamento"));

                    nome.setText(evento.getNome());
                    local.setText(evento.getLugar());
                    dia.setText(evento.getDia());
                    telefone.setText(evento.getTelefone());
                    preco.setText(evento.getValor());

                    String pagamentoDinheiro = null;
                    String pagamentoCredito = null;
                    String pagamentoDebito = null;

                    pagamentoDinheiro = evento.getFormaPagamento().replace("Dinheiro","");
                    pagamentoCredito = evento.getFormaPagamento().replace("Cartão de Crédito","");
                    pagamentoDebito = evento.getFormaPagamento().replace("Cartão de Débito","");

                    if(!pagamentoDinheiro.equals(evento.getFormaPagamento())){
                        dinheiro.setChecked(true);
                    }
                    if(!pagamentoCredito.equals(evento.getFormaPagamento())){
                        cartaoCredito.setChecked(true);
                    }
                    if(!pagamentoDebito.equals(evento.getFormaPagamento())){
                        cartaoDebito.setChecked(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },EditEventoActivity.this, id);
        RequestQueue rq = VolleySingleton.getInstance(EditEventoActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }
}
