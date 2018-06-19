package br.com.seocursos.seocursos;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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

import br.com.seocursos.seocursos.ConstClasses.Disciplina;
import br.com.seocursos.seocursos.ConstClasses.Tarefa;
import br.com.seocursos.seocursos.ConstClasses.VideoAula;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class MenuDisciplinaActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final String AULAS_URL = "https://www.seocursos.com.br/PHP/Android/videoAulas.php";
    private static final String TAREFAS_URL = "https://www.seocursos.com.br/PHP/Android/tarefas.php";
    private String idDisciplina;

    LinearLayout videoAulas, tarefas;
    ListView lvVideos, lvTarefas;
    SearchView sv;
    TabLayout tabs;

    List<Tarefa> listaTarefas;
    List<Tarefa> listaQueryTarefas;
    List<VideoAula> listaVideoAulas;
    List<VideoAula> listaQueryVideoAulas;

    ProgressDialogHelper pd;
    SharedPreferencesHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_disciplina);

        try{
            Intent i = getIntent();
            idDisciplina = i.getStringExtra("id");
        }catch(NullPointerException e){
            e.printStackTrace();
            Intent i = new Intent(MenuDisciplinaActivity.this, DisciplinasActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

        helper = new SharedPreferencesHelper(this);
        pd = new ProgressDialogHelper(this);

        videoAulas = findViewById(R.id.videoAulas);
        tarefas = findViewById(R.id.tarefas);
        tabs = findViewById(R.id.tabs);
        sv = findViewById(R.id.sv);
        lvVideos = findViewById(R.id.lv1);
        lvTarefas = findViewById(R.id.lv2);

        listaTarefas = new ArrayList<>();
        listaQueryTarefas = new ArrayList<>();
        listaVideoAulas = new ArrayList<>();
        listaQueryVideoAulas = new ArrayList<>();

        lvVideos.setTextFilterEnabled(true);
        lvTarefas.setTextFilterEnabled(true);
        sv.setOnQueryTextListener(this);

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0){
                    tarefas.setVisibility(View.GONE);
                    videoAulas.setVisibility(View.VISIBLE);
                }
                if(tab.getPosition() == 1){
                    tarefas.setVisibility(View.VISIBLE);
                    videoAulas.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        lvVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                VideoAula video = listaQueryVideoAulas.get(i);

                pd.open();
                Map<String,String> params = new HashMap<String,String>();
                params.put("assistido", "assistido");
                params.put("idUsuario", helper.getString("id"));
                params.put("idVideoaula", video.getId());

                StringRequest sr = CRUD.customRequest(AULAS_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            boolean enviado = jo.getBoolean("resposta");
                            if(!enviado){
                                Toast.makeText(MenuDisciplinaActivity.this, getResources().getString(R.string.falhaAoConfirmarVisualizacao), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, MenuDisciplinaActivity.this, params);
                RequestQueue rq = VolleySingleton.getInstance(MenuDisciplinaActivity.this).getRequestQueue();
                rq.add(sr);
                rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                    @Override
                    public void onRequestFinished(Request<Object> request) {
                        pd.close();
                    }
                });

                Intent intent = new Intent(MenuDisciplinaActivity.this, AssistirActivity.class);
                intent.putExtra("video", video);
                startActivity(intent);
            }
        });

        carregar();
    }
    @Override
    public boolean onQueryTextChange(String newText){
        listaQueryTarefas.clear();
        listaQueryVideoAulas.clear();
        if (TextUtils.isEmpty(newText)) {
            listaQueryTarefas.addAll(listaTarefas);
            listaQueryVideoAulas.addAll(listaVideoAulas);
        } else {
            String queryText = newText.toLowerCase();
            for(Tarefa u : listaTarefas){
                if(u.getDescricao().toLowerCase().contains(queryText) ||
                        u.getDisciplina().toLowerCase().contains(queryText)){
                    listaQueryTarefas.add(u);
                }
            }
            for(VideoAula u : listaVideoAulas){
                if(u.getTitulo().toLowerCase().contains(queryText) ||
                        u.getDisciplina().toLowerCase().contains(queryText)){
                    listaQueryVideoAulas.add(u);
                }
            }
        }
        lvTarefas.setAdapter(new TarefaListAdapter(listaQueryTarefas, MenuDisciplinaActivity.this));
        lvVideos.setAdapter(new VideoAulaListAdapter(listaQueryVideoAulas, MenuDisciplinaActivity.this));
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query){
        return false;
    }

    public void carregar(){
        pd.open();

        Map<String,String> params = new HashMap<>();
        params.put("select", "select");
        params.put("idDisciplina", idDisciplina);

        RequestQueue rq = VolleySingleton.getInstance(MenuDisciplinaActivity.this).getRequestQueue();
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });

        StringRequest sr = CRUD.customRequest(AULAS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("videoAulas");
                    for(int i=0;i<ja.length();i++){
                        JSONObject objeto = ja.getJSONObject(i);
                        VideoAula videoAula = new VideoAula(objeto.getString("id_videoaula"),
                                objeto.getString("titulo"), objeto.getString("link"),
                                objeto.getString("tipo_video"), objeto.getString("id_disciplina"),
                                objeto.getString("nome_disciplina"));

                        listaVideoAulas.add(videoAula);
                        listaQueryVideoAulas.add(videoAula);
                    }
                    VideoAulaListAdapter adapter = new VideoAulaListAdapter(listaVideoAulas, MenuDisciplinaActivity.this);
                    lvVideos.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this, params);
        rq.add(sr);

        sr = CRUD.customRequest(TAREFAS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("tarefas");
                    for(int i=0;i<ja.length();i++){
                        JSONObject objeto = ja.getJSONObject(i);
                        Integer idDisciplina=objeto.getInt("id_disciplina"),
                                idTutor=objeto.getInt("id_usuario");
                        String id=objeto.getString("id_tarefa"),descricao=objeto.getString("descricao"),
                                disciplina=objeto.getString("disciplina"), tutor=objeto.getString("tutor");
                        String dataEnvio=objeto.getString("data_envio");

                        Tarefa tarefa = new Tarefa(id, descricao, dataEnvio, idDisciplina, idTutor, disciplina, tutor);

                        listaTarefas.add(tarefa);
                        listaQueryTarefas.add(tarefa);
                    }
                    TarefaListAdapter adapter = new TarefaListAdapter(listaTarefas, MenuDisciplinaActivity.this);
                    lvTarefas.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this, params);
        rq.add(sr);
    }

    //Classe interna para criar o adapter da classe externa
    class TarefaListAdapter extends ArrayAdapter<Tarefa> {
        //Lista com os adapter e contexto do aplicativo
        private List<Tarefa> lista;
        private Context contexto;

        //Método construtor
        private TarefaListAdapter(List<Tarefa> lista, Context contexto){
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
            Tarefa tarefa = lista.get(position);
            String subtext = tarefa.getDisciplina().toString()+" - "+tarefa.getTutor().toString();
            //Define conteúdo do item
            titulo.setTextSize(18);
            titulo.setText(tarefa.getDescricao().toString());
            subtitulo.setText(subtext);
            //Retorna a View (Item)
            return listViewItem;
        }
    }
    //Classe interna para criar o adapter da classe externa
    class VideoAulaListAdapter extends ArrayAdapter<VideoAula> {
        //Lista com os adapter e contexto do aplicativo
        private List<VideoAula> lista;
        private Context contexto;

        //Método construtor
        private VideoAulaListAdapter(List<VideoAula> lista, Context contexto){
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
            VideoAula videoAula = lista.get(position);
            //Define conteúdo do item
            titulo.setText(videoAula.getTitulo().toString());
            subtitulo.setText(videoAula.getDisciplina().toString());
            //Retorna a View (Item)
            return listViewItem;
        }
    }
}
