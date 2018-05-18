package br.com.seocursos.seocursos;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.seocursos.seocursos.ConstClasses.RespostaTarefa;
import br.com.seocursos.seocursos.ConstClasses.Tarefa;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class RespostasActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/tarefas.php";
    private String id;

    SearchView sv;
    ListView lv;
    List<RespostaTarefa> lista;
    List<RespostaTarefa> listaQuery;

    ProgressDialogHelper pd;
    SharedPreferencesHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respostas);

        pd = new ProgressDialogHelper(RespostasActivity.this);
        helper = new SharedPreferencesHelper(RespostasActivity.this);

        if(!helper.getBoolean("login")){
            Intent i = new Intent(RespostasActivity.this, TarefasActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            startActivity(i);
        }

        sv = findViewById(R.id.sv);
        lv = findViewById(R.id.lv);

        lista = new ArrayList<>();
        listaQuery = new ArrayList<>();

        lv.setTextFilterEnabled(true);
        sv.setOnQueryTextListener(this);

        Intent i = getIntent();
        try{
            id = i.getStringExtra("id");
        }catch(NullPointerException e){
            e.printStackTrace();
            i = new Intent(RespostasActivity.this, TarefasActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            startActivity(i);
        }

        carregar();
    }
    @Override
    public boolean onQueryTextChange(String newText){
        listaQuery.clear();
        if (TextUtils.isEmpty(newText)) {
            listaQuery.addAll(lista);
        } else {
            String queryText = newText.toLowerCase();
            for(RespostaTarefa u : lista){
                if(u.getAluno().toLowerCase().contains(queryText) ||
                        u.getResposta().toLowerCase().contains(queryText)){
                    listaQuery.add(u);
                }
            }
        }
        lv.setAdapter(new ListViewAdapter(listaQuery, RespostasActivity.this));
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query){
        return false;
    }

    public void carregar(){
        pd.open();
        lista.clear();

        Map<String,String> params = new HashMap<String,String>();

        params.put("respostas", "respostas");
        params.put("id", id);

        StringRequest sr = CRUD.customRequest(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    Toast.makeText(RespostasActivity.this, response, Toast.LENGTH_SHORT).show();
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("respostas");
                    for(int i=0;i<ja.length();i++){
                        Toast.makeText(RespostasActivity.this, "retornado valor "+i, Toast.LENGTH_SHORT).show();
                        JSONObject objeto = ja.getJSONObject(i);
                        RespostaTarefa resposta = new RespostaTarefa(objeto.getString("id_resposta"), objeto.getString("resposta"), objeto.getString("id_usuario"), objeto.getString("nome"));
                        lista.add(resposta);
                        listaQuery.add(resposta);
                    }
                    ListViewAdapter adapter = new ListViewAdapter(lista, RespostasActivity.this);

                    lv.setAdapter(adapter);
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        },RespostasActivity.this, params);
        RequestQueue rq = VolleySingleton.getInstance(RespostasActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }

    //Classe interna para criar o adapter da classe externa
    class ListViewAdapter extends ArrayAdapter<RespostaTarefa> {
        //Lista com os adapter e contexto do aplicativo
        private List<RespostaTarefa> lista;
        private Context contexto;

        //Método construtor
        private ListViewAdapter(List<RespostaTarefa> lista, Context contexto){
            //para a classe mãe
            super(contexto, R.layout.list_item, lista);

            this.lista = lista;
            this.contexto = contexto;
        }

        //Método que retorna o item para o ListView
        public View getView(int position, View convertView, ViewGroup parent){
            //Recebe o inflater do contexto
            LayoutInflater inflater = LayoutInflater.from(this.contexto);
            //Item que será retornado
            View listViewItem = inflater.inflate(R.layout.list_item, null, true);
            TextView titulo = listViewItem.findViewById(R.id.titulo);
            TextView subtitulo = listViewItem.findViewById(R.id.subtitulo);
            //Recebe o item da posição solicitada
            RespostaTarefa resposta = lista.get(position);
            String subtext = resposta.getAluno();
            //Define conteúdo do item
            titulo.setTextSize(18);
            titulo.setText(resposta.getResposta());
            subtitulo.setText(subtext);
            //Retorna a View (Item)
            return listViewItem;
        }
    }
}
