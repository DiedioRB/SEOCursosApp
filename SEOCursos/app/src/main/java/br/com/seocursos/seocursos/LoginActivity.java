package br.com.seocursos.seocursos;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import br.com.seocursos.seocursos.ConstClasses.Usuario;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class LoginActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/usuarios.php";
    private final int RC_SIGN_IN = 2;
    private String emailInput;

    TextInputEditText email,senha;
    Button btn;
    SharedPreferencesHelper helper;

    ProgressDialogHelper pd;

    CallbackManager callbackManager;
    LoginButton loginButton;
    GoogleSignInClient client;

    SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Login com Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        client = GoogleSignIn.getClient(this, gso);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        //Login com facebook
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String email = object.getString("email");
                            fazerLogin(email);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
            }
        });

        helper = new SharedPreferencesHelper(LoginActivity.this);
        if(helper.getBoolean("login")){
            Toast.makeText(this, "Você já está online!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        pd = new ProgressDialogHelper(LoginActivity.this);

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
    @Override
    public void onStart(){
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        googleLogin(account);
    }

    public void fazerLogin(){
        pd.open();
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

                        /**
                         * os parâmetros para sesão são login(b), id, nome, email, privilegio
                         */
                        helper.setBoolean("login", true);
                        helper.setString("id", id);
                        helper.setString("nome", nome);
                        helper.setString("email", email);
                        helper.setString("privilegio", tipoUsuario);

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
                pd.close();
            }
        });
    }
    public void fazerLogin(String email){
        Map<String,String> params = new HashMap<String,String>();
        params.put("emailLogin", "emailLogin");
        params.put("email", email);

        StringRequest sr = CRUD.customRequest(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jo = new JSONObject(response);
                    boolean encontrado = jo.getBoolean("resposta");
                    if(encontrado) {
                        JSONObject objeto = jo.getJSONObject("dados");
                        String id = objeto.getString("id");
                        String nome = objeto.getString("nome");
                        String email = objeto.getString("email");
                        String tipoUsuario = objeto.getString("tipo_usuario");

                        helper.setBoolean("login", true);
                        helper.setString("id", id);
                        helper.setString("nome", nome);
                        helper.setString("email", email);
                        helper.setString("privilegio", tipoUsuario);

                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }else{
                        String error = jo.getString("error");
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, LoginActivity.this, params);
        RequestQueue rq = VolleySingleton.getInstance(LoginActivity.this).getRequestQueue();
        rq.add(sr);
    }

    private void googleSignIn(){
        Intent signInIntent = client.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
        pd.open();
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
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            googleLogin(account);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public void googleLogin(GoogleSignInAccount account){
        if(account != null){
            String email = account.getEmail();
            fazerLogin(email);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
}
