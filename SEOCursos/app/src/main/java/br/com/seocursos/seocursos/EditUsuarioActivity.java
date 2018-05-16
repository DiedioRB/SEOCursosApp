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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import br.com.jansenfelipe.androidmask.MaskEditTextChangedListener;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;
import br.com.seocursos.seocursos.Outros.ValidaCPF;

public class EditUsuarioActivity extends AppCompatActivity {
    private String id = "";
    private final static String JSON_URL = "https://www.seocursos.com.br/PHP/Android/usuarios.php";
    private static final int GALLERY_REQUEST = 1;
    private Bitmap imagem = null;
    private String image;

    TextInputEditText nome, email, cpf, cep, endereco, numero, cidade, estado, telefone;
    RadioGroup sexo, modalidade;
    Button btn;
    NetworkImageView foto;

    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_usuario);

        pd = new ProgressDialogHelper(EditUsuarioActivity.this);

        //Recupera os elementos pelo ID
        nome = (TextInputEditText) findViewById(R.id.nomeUsuario);
        email = (TextInputEditText) findViewById(R.id.emailUsuario);
        cpf = (TextInputEditText) findViewById(R.id.cpfUsuario);
        cep = (TextInputEditText) findViewById(R.id.cepUsuario);
        endereco = (TextInputEditText) findViewById(R.id.enderecoUsuario);
        numero = (TextInputEditText) findViewById(R.id.numeroUsuario);
        cidade = (TextInputEditText) findViewById(R.id.cidadeUsuario);
        estado = (TextInputEditText) findViewById(R.id.estadoUsuario);
        telefone = (TextInputEditText) findViewById(R.id.telUsuario);
        sexo = (RadioGroup) findViewById(R.id.sexoUsuario);
        modalidade = (RadioGroup) findViewById(R.id.modalidadeUsuario);
        btn = (Button) findViewById(R.id.confirmarUsuario);

        //Aplica máscara
        MaskEditTextChangedListener maskCPF = new MaskEditTextChangedListener("###.###.###-##", cpf);
        MaskEditTextChangedListener maskCEP = new MaskEditTextChangedListener("##.###-###", cep);

        cpf.addTextChangedListener(maskCPF);
        cep.addTextChangedListener(maskCEP);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            telefone.addTextChangedListener(new PhoneNumberFormattingTextWatcher("BR"));
        }
        foto = findViewById(R.id.userImage);

        //Recupera o ID para edição, caso houver
        Intent i = getIntent();
        if (i != null) {
            Bundle b = getIntent().getExtras();
            if (b != null) {
                String receiveId = b.getString("id");
                this.id = receiveId.toString();
                carregar();
            }
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.open();
                final Map<String, String> params = new HashMap<String, String>();

                params.put("id_usuario", id);
                params.put("nome", nome.getText().toString());
                params.put("email", email.getText().toString());
                params.put("cpf", cpf.getText().toString());
                params.put("cep", cep.getText().toString());
                params.put("endereco", endereco.getText().toString());
                params.put("numero", numero.getText().toString());
                params.put("cidade", cidade.getText().toString());
                params.put("estado", estado.getText().toString());
                params.put("tel", telefone.getText().toString());

                if(imagem != null) {
                    String imagemUsuario = getStringImage(imagem);
                    params.put("foto", imagemUsuario);
                    params.put("newImage", "newImage");
                }else{
                    params.put("foto", image);
                }

                String sexoS, modalidadeS;
                if (sexo.getCheckedRadioButtonId() == R.id.masculinoUsuario) {
                    sexoS = "M";
                } else {
                    sexoS = "F";
                }
                if (modalidade.getCheckedRadioButtonId() == R.id.semipresencialUsuario) {
                    modalidadeS = "1";
                } else {
                    modalidadeS = "2";
                }

                params.put("sexo", sexoS);
                params.put("modalidade", modalidadeS);
                StringRequest sr = CRUD.editar(JSON_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            boolean enviado = jo.getBoolean("resposta");
                            if (enviado) {
                                Toast.makeText(EditUsuarioActivity.this, "Editado com sucesso!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditUsuarioActivity.this, "Falha na edição!", Toast.LENGTH_SHORT).show();
                            }
                            Intent i = new Intent(EditUsuarioActivity.this, UsuariosActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, params, getApplicationContext());
                RequestQueue rq = VolleySingleton.getInstance(EditUsuarioActivity.this).getRequestQueue();
                rq.add(sr);
                rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                    @Override
                    public void onRequestFinished(Request<Object> request) {
                        pd.close();
                    }
                });
            }
        });

        cpf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    pd.open();
                    String sendCpf = cpf.getText().toString();
                    sendCpf = sendCpf.replace(".", "");
                    sendCpf = sendCpf.replace("-", "");
                    boolean isCPF = ValidaCPF.isCPF(sendCpf);
                    if (!isCPF) {
                        btn.setClickable(false);
                        btn.setBackgroundColor(getResources().getColor(R.color.lightGrey));
                        btn.setTextColor(getResources().getColor(R.color.darkGrey));
                        Toast.makeText(EditUsuarioActivity.this, "CPF Inválido!", Toast.LENGTH_SHORT).show();
                    } else {
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
                if (!b) {
                    pd.open();
                    String sendCep = cep.getText().toString();
                    sendCep = sendCep.replace(".", "");
                    sendCep = sendCep.replace("-", "");
                    String url = "https://viacep.com.br/ws/" + sendCep + "/json/unicode/";
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
                            Toast.makeText(EditUsuarioActivity.this, "CEP Inválido!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    RequestQueue rq = VolleySingleton.getInstance(EditUsuarioActivity.this).getRequestQueue();
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
        foto.setOnClickListener(new View.OnClickListener() {
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
                foto.setImageBitmap(imagem);
            }catch(FileNotFoundException e){
                e.printStackTrace();
                Toast.makeText(this, "Erro ao receber a imagem: Imagem não encontrada!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void carregar(){
        pd.open();
        StringRequest sr = CRUD.selecionarEditar(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    boolean recebido = jo.getBoolean("resposta");
                    if(recebido) {
                        JSONArray ja = jo.getJSONArray("usuario");
                        JSONObject objeto = ja.getJSONObject(0);

                        String idO = objeto.getString("id_usuario"), numeroO = objeto.getString("numero");
                        String nomeO = objeto.getString("nome"), emailO = objeto.getString("email"),
                                fotoO = objeto.getString("foto"), cpfO = objeto.getString("cpf"),
                                cepO = objeto.getString("cep"), enderecoO = objeto.getString("endereco"),
                                cidadeO = objeto.getString("cidade"), estadoO = objeto.getString("estado"),
                                sexoO = objeto.getString("sexo"), tipoUsuarioO = objeto.getString("tipo_usuario");
                        String telefoneO = objeto.getString("telefone");

                        nome.setText(nomeO);
                        email.setText(emailO);
                        cpf.setText(cpfO);
                        cep.setText(cepO);
                        endereco.setText(enderecoO);
                        numero.setText(numeroO);
                        cidade.setText(cidadeO);
                        estado.setText(estadoO);
                        telefone.setText(telefoneO);

                        image = fotoO;

                        if (sexoO.equals("M")) {
                            sexo.check(R.id.masculinoUsuario);
                        } else {
                            sexo.check(R.id.femininoUsuario);
                        }

                        ImageLoader il = VolleySingleton.getInstance(getApplicationContext()).getImageLoader();
                        foto.setImageUrl("https://www.seocursos.com.br/Imagens/Login/"+fotoO,il);
                    }else{
                        String error = jo.getString("error");
                        Toast.makeText(EditUsuarioActivity.this, error, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(EditUsuarioActivity.this, UsuariosActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, getApplicationContext(), id);
        RequestQueue rq = VolleySingleton.getInstance(EditUsuarioActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }
    public String getStringImage(Bitmap imagem){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imagem.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] b = outputStream.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);

        return temp;
    }
}