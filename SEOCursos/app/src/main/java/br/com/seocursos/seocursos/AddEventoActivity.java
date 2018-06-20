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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class AddEventoActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/eventos.php";

    TextInputEditText nome,local,dia,telefone,preco;
    CheckBox dinheiro,cartaoCredito,cartaoDebito;
    Button btn;

    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_evento);

        pd = new ProgressDialogHelper(AddEventoActivity.this);

        nome = findViewById(R.id.nome);
        local = findViewById(R.id.local);
        dia = findViewById(R.id.dia);
        telefone = findViewById(R.id.telefone);
        preco = findViewById(R.id.preco);

        MaskEditTextChangedListener diaMask = new MaskEditTextChangedListener("##/##/####", dia);
        dia.addTextChangedListener(diaMask);

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
                if(validarCampos()) {
                    pd.open();
                    Map<String, String> params = new HashMap<String, String>();

                    try {
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Date data = null;
                        data = formato.parse(dia.getText().toString());
                        formato = new SimpleDateFormat("yyyy-MM-dd");
                        String dia = formato.format(data);
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
                        StringRequest sr = CRUD.inserir(JSON_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jo = new JSONObject(response);
                                    boolean enviado = jo.getBoolean("resposta");
                                    if (enviado) {
                                        Toast.makeText(AddEventoActivity.this, getResources().getString(R.string.cadastradoComSucesso), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AddEventoActivity.this, getResources().getString(R.string.falhaCadastro), Toast.LENGTH_SHORT).show();
                                    }
                                    Intent i = new Intent(AddEventoActivity.this, EventosActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, params, AddEventoActivity.this);
                        RequestQueue rq = VolleySingleton.getInstance(AddEventoActivity.this).getRequestQueue();
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
                }else{
                    Toast.makeText(AddEventoActivity.this, getResources().getString(R.string.preenchaOsCamposPrimeiro), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public boolean validarCampos(){
        return !(nome.getText().toString().isEmpty() || local.getText().toString().isEmpty() ||
                dia.getText().toString().isEmpty() || telefone.getText().toString().isEmpty() ||
                preco.getText().toString().isEmpty());
    }
}
