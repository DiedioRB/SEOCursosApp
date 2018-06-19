package br.com.seocursos.seocursos;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.seocursos.seocursos.Fragments.AdministradorMenu;
import br.com.seocursos.seocursos.Fragments.AlunoMenu;
import br.com.seocursos.seocursos.Fragments.TutorMenu;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class MinhaContaActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/usuarios.php";
    private String id;

    TextView nome, email, privilegio;
    NetworkImageView imagem;
    LinearLayout container;

    ProgressDialogHelper pd;
    SharedPreferencesHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minha_conta);

        pd = new ProgressDialogHelper(MinhaContaActivity.this);
        helper = new SharedPreferencesHelper(MinhaContaActivity.this);

        nome = findViewById(R.id.nome);
        email = findViewById(R.id.email);
        privilegio = findViewById(R.id.privilegio);
        imagem = findViewById(R.id.userImage);
        container = findViewById(R.id.container);

        id = helper.getString("id");
        nome.setText(helper.getString("nome"));
        email.setText(helper.getString("email"));

        String privilegio = "";
        Fragment fragment = null;
        switch(helper.getString("privilegio")){
            case "A":
                privilegio = getResources().getString(R.string.aluno);
                fragment = AlunoMenu.newInstance();
                break;
            case "T":
                privilegio = getResources().getString(R.string.tutor);
                fragment = TutorMenu.newInstance();
                break;
            case "D":
                privilegio = getResources().getString(R.string.administrador);
                fragment = AdministradorMenu.newInstance();
                break;
        }
        this.privilegio.setText(privilegio);

        carregarImagem();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.context_menu, menu);
        MenuItem item = menu.findItem(R.id.excluir);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.editar){
            Intent i = new Intent(MinhaContaActivity.this, EditUsuarioActivity.class);
            i.putExtra("id", id);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    public void carregarImagem(){
        pd.open();

        StringRequest sr = CRUD.selecionarEditar(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("usuario");
                    JSONObject objeto = ja.getJSONObject(0);
                    String foto = objeto.getString("foto");

                    ImageLoader il = VolleySingleton.getInstance(getApplicationContext()).getImageLoader();
                    imagem.setImageUrl("https://www.seocursos.com.br/Imagens/Login/"+foto,il);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, MinhaContaActivity.this, id);
        RequestQueue rq = VolleySingleton.getInstance(MinhaContaActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }
}
