package br.com.seocursos.seocursos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ValidaCPF;

public class AddUsuarioActivity extends AppCompatActivity {
    private final static String JSON_URL = "https://www.seocursos.com.br/PHP/Android/usuarios.php";
    private static final int GALLERY_REQUEST = 1;
    private Bitmap imagem;

    TextInputEditText nome,senha,confSenha,email,cpf,cep,endereco,numero,cidade,estado,telefone;
    RadioGroup sexo,modalidade;
    Button btn;
    ImageView iv;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_usuario);
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
        telefone = (TextInputEditText)findViewById(R.id.telUsuario);

        sexo = (RadioGroup)findViewById(R.id.sexoUsuario);
        modalidade = (RadioGroup)findViewById(R.id.modalidadeUsuario);
        btn = (Button)findViewById(R.id.confirmarUsuario);

        //Aplica máscara
        MaskEditTextChangedListener maskCPF = new MaskEditTextChangedListener("###.###.###-##",cpf);
        MaskEditTextChangedListener maskCEP = new MaskEditTextChangedListener("##.###-###",cep);

        cpf.addTextChangedListener(maskCPF);
        cep.addTextChangedListener(maskCEP);
        telefone.addTextChangedListener(new PhoneNumberFormattingTextWatcher("BR"));
        iv = findViewById(R.id.imageView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                params.put("tel", telefone.getText().toString());

                String sexoS,modalidadeS;
                if(sexo.getCheckedRadioButtonId() == R.id.masculinoUsuario){
                    sexoS = "M";
                }else{
                    sexoS = "F";
                }
                if(modalidade.getCheckedRadioButtonId() == R.id.semipresencialUsuario){
                    modalidadeS = "1";
                }else{
                    modalidadeS = "2";
                }

                params.put("sexo", sexoS);
                params.put("modalidade", modalidadeS);

                //Imagens
                String imagemUsuario = getStringImage(imagem);
                params.put("foto", imagemUsuario);

                StringRequest sr = CRUD.inserir(JSON_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            boolean enviado = jo.getBoolean("resposta");

                            if(enviado) {
                                Toast.makeText(AddUsuarioActivity.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(AddUsuarioActivity.this, "Falha no cadastro!", Toast.LENGTH_SHORT).show();
                            }
                            Intent getI = getIntent();
                            Intent i;
                            boolean toLogin = getI.getBooleanExtra("toLogin", false);
                            if(toLogin) {
                                i = new Intent(AddUsuarioActivity.this, LoginActivity.class);
                            }else {
                                i = new Intent(AddUsuarioActivity.this, UsuariosActivity.class);
                            }
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },params,getApplicationContext());
                RequestQueue rq = VolleySingleton.getInstance(AddUsuarioActivity.this).getRequestQueue();
                rq.add(sr);
            }
        });

        cpf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    String sendCpf = cpf.getText().toString();
                    sendCpf = sendCpf.replace(".", "");
                    sendCpf = sendCpf.replace("-", "");
                    boolean isCPF = ValidaCPF.isCPF(sendCpf);
                    if(!isCPF){
                        btn.setClickable(false);
                        btn.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                        btn.setTextColor(getResources().getColor(R.color.darkGrey));
                        Toast.makeText(AddUsuarioActivity.this, "CPF Inválido!", Toast.LENGTH_SHORT).show();
                    }else{
                        btn.setClickable(true);
                        btn.setBackgroundColor(getResources().getColor(R.color.blueAccent2));
                        btn.setTextColor(getResources().getColor(R.color.white));
                    }
                }
            }
        });
        cep.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
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
                            Toast.makeText(AddUsuarioActivity.this, "CEP Inválido!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    RequestQueue rq = VolleySingleton.getInstance(AddUsuarioActivity.this).getRequestQueue();
                    rq.add(sr);
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
                Toast.makeText(this, "Erro ao receber a imagem: Imagem não encontrada!", Toast.LENGTH_SHORT).show();
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
}
