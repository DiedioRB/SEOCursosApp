package br.com.seocursos.seocursos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;
import br.com.seocursos.seocursos.Outros.ValidaCPF;

public class AddUsuarioActivity extends AppCompatActivity {
    private final static String JSON_URL = "https://www.seocursos.com.br/PHP/Android/usuarios.php";
    private static final int GALLERY_REQUEST = 1;
    private Bitmap imagem = null;
    private String privilegio = "A";

    TextInputEditText nome,senha,confSenha,email,cpf,cep,endereco,numero,cidade,estado;
    RadioGroup sexo;
    Button btn;
    ImageView iv;

    LinearLayout formAluno, formTutor;
    TextInputEditText telUsuario, telRes, telCel, dataNascimento, curso, instituicao, anoConclusao;

    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_usuario);

        pd = new ProgressDialogHelper(AddUsuarioActivity.this);

        //Recupera os elementos pelo ID
        nome = (TextInputEditText)findViewById(R.id.nomeUsuario);
        senha = (TextInputEditText)findViewById(R.id.senhaUsuario);
        confSenha = (TextInputEditText)findViewById(R.id.confSenhaUsuario);
        email = (TextInputEditText)findViewById(R.id.emailUsuario);
        cpf = (TextInputEditText)findViewById(R.id.cpfUsuario);
        cep = (TextInputEditText)findViewById(R.id.cepUsuario);
        endereco = (TextInputEditText)findViewById(R.id.enderecoUsuario);
        numero = (TextInputEditText)findViewById(R.id.numeroUsuario);
        cidade = (TextInputEditText)findViewById(R.id.cidadeUsuario);
        estado = (TextInputEditText)findViewById(R.id.estadoUsuario);

        sexo = (RadioGroup)findViewById(R.id.sexoUsuario);
        btn = (Button)findViewById(R.id.confirmarUsuario);

        formAluno = findViewById(R.id.formAluno);
        formTutor = findViewById(R.id.formTutor);

        telUsuario = findViewById(R.id.telUsuario);
        telRes = findViewById(R.id.telResidencial);
        telCel = findViewById(R.id.telCelular);
        dataNascimento = findViewById(R.id.dataNascimento);
        curso = findViewById(R.id.curso);
        instituicao = findViewById(R.id.instituicao);
        anoConclusao = findViewById(R.id.anoConclusao);

        Intent i =  getIntent();
        privilegio = i.getStringExtra("privilegio");
        if(privilegio == null){
            privilegio = "A";
        }

        switch(privilegio){
            case "A":
                formAluno.setVisibility(View.VISIBLE);
                formTutor.setVisibility(View.GONE);
                break;
            case "T":
                formAluno.setVisibility(View.GONE);
                formTutor.setVisibility(View.VISIBLE);
                break;
            case "D":
                formAluno.setVisibility(View.GONE);
                formTutor.setVisibility(View.GONE);
                break;
        }

        //Aplica máscara
        MaskEditTextChangedListener maskCPF = new MaskEditTextChangedListener("###.###.###-##",cpf);
        MaskEditTextChangedListener maskCEP = new MaskEditTextChangedListener("##.###-###",cep);
        MaskEditTextChangedListener maskData = new MaskEditTextChangedListener("##/##/####",dataNascimento);

        cpf.addTextChangedListener(maskCPF);
        cep.addTextChangedListener(maskCEP);
        dataNascimento.addTextChangedListener(maskData);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            telUsuario.addTextChangedListener(new PhoneNumberFormattingTextWatcher("BR"));
            telRes.addTextChangedListener(new PhoneNumberFormattingTextWatcher("BR"));
            telCel.addTextChangedListener(new PhoneNumberFormattingTextWatcher("BR"));
        }
        iv = findViewById(R.id.imageView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCampos()) {
                    pd.open();
                    Map<String, String> params = new HashMap<String, String>();

                    params.put("nome", nome.getText().toString());
                    params.put("senha", senha.getText().toString());
                    params.put("email", email.getText().toString());
                    params.put("cpf", cpf.getText().toString());
                    params.put("cep", cep.getText().toString());
                    params.put("endereco", endereco.getText().toString());
                    params.put("numero", numero.getText().toString());
                    params.put("cidade", cidade.getText().toString());
                    params.put("estado", estado.getText().toString());

                    switch (privilegio) {
                        case "A":
                            params.put("tipoUsuario", "A");

                            params.put("telUsuario", telUsuario.getText().toString());
                            break;
                        case "T":
                            params.put("tipoUsuario", "T");
                            params.put("telCel", telCel.getText().toString());
                            params.put("telRes", telRes.getText().toString());

                            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                            ParsePosition pos = new ParsePosition(0);
                            Date data = formato.parse(dataNascimento.getText().toString(), pos);
                            formato = new SimpleDateFormat("yyyy-MM-dd");
                            String date = formato.format(data);

                            params.put("dataNascimento", date);
                            params.put("curso", curso.getText().toString());
                            params.put("instituicao", instituicao.getText().toString());
                            params.put("anoConclusao", anoConclusao.getText().toString());
                            break;
                        case "D":
                            params.put("tipoUsuario", "D");
                            break;
                    }

                    String sexoS;
                    if (sexo.getCheckedRadioButtonId() == R.id.masculinoUsuario) {
                        sexoS = "M";
                    } else {
                        sexoS = "F";
                    }

                    params.put("sexo", sexoS);

                    //Imagens
                    if (imagem != null) {
                        String imagemUsuario = getStringImage(imagem);
                        params.put("foto", imagemUsuario);
                    }

                    StringRequest sr = CRUD.inserir(JSON_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(AddUsuarioActivity.this, response, Toast.LENGTH_SHORT).show();
                            try {
                                JSONObject jo = new JSONObject(response);
                                boolean enviado = jo.getBoolean("resposta");

                                if (enviado) {
                                    Toast.makeText(AddUsuarioActivity.this, getResources().getString(R.string.cadastradoComSucesso), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddUsuarioActivity.this, getResources().getString(R.string.falhaCadastro), Toast.LENGTH_SHORT).show();
                                }
                                Intent i = getIntent();
                                boolean toLogin = i.getBooleanExtra("toLogin", false);
                                if (toLogin) {
                                    i = new Intent(AddUsuarioActivity.this, LoginActivity.class);
                                } else {
                                    i = new Intent(AddUsuarioActivity.this, UsuariosActivity.class);
                                }
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, params, getApplicationContext());
                    RequestQueue rq = VolleySingleton.getInstance(AddUsuarioActivity.this).getRequestQueue();
                    rq.add(sr);
                    rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            pd.close();
                        }
                    });
                }else{
                    Toast.makeText(AddUsuarioActivity.this, getResources().getString(R.string.preenchaOsCamposPrimeiro), Toast.LENGTH_SHORT).show();
                }
            }
        });

        cpf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    pd.open();
                    String sendCpf = cpf.getText().toString();
                    sendCpf = sendCpf.replace(".", "");
                    sendCpf = sendCpf.replace("-", "");
                    boolean isCPF = ValidaCPF.isCPF(sendCpf);
                    if(!isCPF){
                        btn.setClickable(false);
                        btn.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                        btn.setTextColor(getResources().getColor(R.color.darkGrey));
                        Toast.makeText(AddUsuarioActivity.this, getResources().getString(R.string.cpfInvalido), Toast.LENGTH_SHORT).show();
                    }else{
                        btn.setClickable(true);
                        btn.setBackgroundColor(getResources().getColor(R.color.blueAccent2));
                        btn.setTextColor(getResources().getColor(R.color.white));
                    }
                    pd.close();
                }
            }
        });
        cep.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    pd.open();
                    String sendCep = cep.getText().toString();
                    sendCep = sendCep.replace(".", "");
                    sendCep = sendCep.replace("-", "");
                    String url = "https://viacep.com.br/ws/"+sendCep+"/json/unicode/";
                    StringRequest sr = new StringRequest(url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject objeto = new JSONObject(response);
                                String enderecoO = objeto.getString("logradouro"), cidadeO = objeto.getString("localidade"),
                                        estadoO = objeto.getString("uf");

                                endereco.setText(enderecoO);
                                cidade.setText(cidadeO);
                                estado.setText(estadoO);
                                btn.setClickable(true);
                                btn.setBackgroundColor(getResources().getColor(R.color.blueAccent2));
                                btn.setTextColor(getResources().getColor(R.color.white));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            btn.setClickable(false);
                            btn.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                            btn.setTextColor(getResources().getColor(R.color.darkGrey));
                            Toast.makeText(AddUsuarioActivity.this, getResources().getString(R.string.cepInvalido), Toast.LENGTH_SHORT).show();
                        }
                    });
                    RequestQueue rq = VolleySingleton.getInstance(AddUsuarioActivity.this).getRequestQueue();
                    rq.add(sr);
                    rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            pd.close();
                        }
                    });
                }
            }
        });
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, GALLERY_REQUEST);
            }
        });
    }
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data){
        super.onActivityResult(reqCode, resultCode, data);

        if(resultCode == RESULT_OK && reqCode == GALLERY_REQUEST){
            try{
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                imagem = BitmapFactory.decodeStream(imageStream);
                iv.setImageBitmap(imagem);
            }catch(FileNotFoundException e){
                e.printStackTrace();
                Toast.makeText(this, getResources().getString(R.string.imagemNaoEncontrada), Toast.LENGTH_SHORT).show();
            }
        }
    }
    public String getStringImage(Bitmap imagem){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imagem.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] b = outputStream.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);

        return temp;
    }

    public boolean validarCampos(){
        if(!(nome.getText().toString().isEmpty() || senha.getText().toString().isEmpty() ||
                confSenha.getText().toString().isEmpty() || email.getText().toString().isEmpty() ||
                cpf.getText().toString().isEmpty() || cep.getText().toString().isEmpty() ||
                endereco.getText().toString().isEmpty() || numero.getText().toString().isEmpty() ||
                cidade.getText().toString().isEmpty() || estado.getText().toString().isEmpty())){
            boolean isValid = false;
            switch(privilegio){
                case "A":
                    if(!(telUsuario.getText().toString().isEmpty())){
                        isValid = true;
                    }
                    break;
                case "T":
                    if(!(telCel.getText().toString().isEmpty() || telRes.getText().toString().isEmpty() ||
                            dataNascimento.getText().toString().isEmpty() || curso.getText().toString().isEmpty() ||
                            instituicao.getText().toString().isEmpty() || anoConclusao.getText().toString().isEmpty())){
                        isValid = true;
                    }
                    break;
                case "D":
                    isValid = true;
                    break;
            }
            if(!(senha.getText().toString().equals(confSenha.getText().toString()))){
                Toast.makeText(this, getResources().getString(R.string.senhasAmbiguas), Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            return isValid;
        }else{
            return false;
        }
    }
}
