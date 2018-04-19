package br.com.seocursos.seocursos;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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

import br.com.seocursos.seocursos.ConstClasses.Usuario;
import br.com.seocursos.seocursos.Outros.CRUD;

public class LoginActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/usuarios.php";
    private String emailInput;

    TextInputEditText email,senha;
    Button btn;
    SharedPreferencesSingleton helper;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        helper = SharedPreferencesSingleton.getInstance(LoginActivity.this);
        if(helper.getBoolean("login")){
            Toast.makeText(this, "Você já está online!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        buildProgressDialog();

        email = findViewById(R.id.email);
        senha = findViewById(R.id.senha);
        btn = findViewById(R.id.confirmar);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(email.getText().toString().isEmpty() || senha.getText().toString().isEmpty())) {
                    fazerLogin();
                }else{
                    Toast.makeText(LoginActivity.this, "Preencha os campos primeiro!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        senha.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btn.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    public void fazerLogin(){
        openProgressDialog();
        Map<String,String> params = new HashMap<String,String>();
        params.put("login", "login");
        params.put("email", email.getText().toString());
        params.put("senha", senha.getText().toString());

        StringRequest sr = CRUD.customRequest(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    boolean encontrado = jo.getBoolean("resposta");
                    if(encontrado){
                        String id,nome,email,foto,sexo,tipoUsuario,cpf,cep,endereco,cidade,estado;
                        int numero;
                        JSONObject objeto = jo.getJSONObject("dados");

                        id = objeto.getString("id");
                        nome = objeto.getString("nome");
                        email = objeto.getString("email");
                        foto = objeto.getString("foto");
                        sexo = objeto.getString("sexo");
                        tipoUsuario = objeto.getString("tipo_usuario");
                        cpf = objeto.getString("cpf");
                        cep = objeto.getString("cep");
                        endereco = objeto.getString("endereco");
                        numero = objeto.getInt("numero");
                        cidade = objeto.getString("cidade");
                        estado = objeto.getString("estado");

                        Usuario usuario = new Usuario(id, nome,email,foto,sexo,tipoUsuario,cpf,cep,endereco,numero,cidade,estado);

                        String tipoString = usuario.getTipoString();
                        helper.setBoolean("login", true);
                        helper.setString("id", id);
                        helper.setString("nome", nome);
                        helper.setString("email", email);

                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }else{
                        String errorMessage = jo.getString("error");
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, LoginActivity.this, params);
        RequestQueue rq = VolleySingleton.getInstance(LoginActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                closeProgressDialog();
            }
        });
    }
    public void cadastro(View v){
        Intent i = new Intent(LoginActivity.this, AddUsuarioActivity.class);
        i.putExtra("toLogin", true);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
    public void openPromptSenha(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Recuperação de Senha");
        builder.setCancelable(true);

        final EditText input = new EditText(LoginActivity.this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setHint("E-mail");
        if(emailInput != null){
            input.setText(emailInput);
        }
        builder.setView(input);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                emailInput = input.getText().toString();
                if(!emailInput.equals("")){
                    sendMail(emailInput);
                    email.setText(emailInput);
                }else{
                    Toast.makeText(LoginActivity.this, "Digiter um e-mail válido!", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                emailInput = input.getText().toString();
                dialogInterface.cancel();
            }
        });

        builder.create().show();
    }
    public void sendMail(String email){
        Map<String,String> params = new HashMap<String,String>();
        params.put("recoverPassword", "recoverPassword");
        params.put("email", email);

        StringRequest sr = CRUD.customRequest(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objeto = new JSONObject(response);
                    boolean enviado = objeto.getBoolean("resposta");
                    if(enviado){
                        Toast.makeText(LoginActivity.this, "Uma mensagem foi enviada ao seu e-mail!\nVocê tem uma semana para recuperar a senha!", Toast.LENGTH_SHORT).show();
                    }else{
                        String error = objeto.getString("error");
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },LoginActivity.this, params);
        RequestQueue rq = VolleySingleton.getInstance(LoginActivity.this).getRequestQueue();
        rq.add(sr);
    }
    public void buildProgressDialog(){
        pd = new ProgressDialog(LoginActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Aguarde...");
    }
    public void openProgressDialog(){
        pd.show();
    }
    public void closeProgressDialog(){
        pd.dismiss();
    }
}
