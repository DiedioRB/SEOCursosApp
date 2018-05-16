package br.com.seocursos.seocursos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
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

import br.com.seocursos.seocursos.ConstClasses.Tarefa;
import br.com.seocursos.seocursos.ConstClasses.Usuario;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class TarefasActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/tarefas.php";
    ListView lvTarefas;
    List<Tarefa> lista;
    List<Tarefa> listaQuery;
    FloatingActionButton fab;
    SearchView sv;

    ProgressDialogHelper pd;
    SharedPreferencesHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarefas);

        pd = new ProgressDialogHelper(TarefasActivity.this);
        helper = new SharedPreferencesHelper(TarefasActivity.this);

        lvTarefas = (ListView)findViewById(R.id.lvTarefas);
        lista = new ArrayList<Tarefa>();
        listaQuery = new ArrayList<Tarefa>();
        fab = findViewById(R.id.fabTarefas);

        if(!helper.getString("privilegio").equals("A")) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(TarefasActivity.this, AddTarefaActivity.class);
                    startActivity(i);
                }
            });
            lvTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //       Intent i = new Intent(TarefasActivity.this, RespostasActivity.class);
                    //       i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //       startActivity(i);
                }
            });
            if(helper.getString("privilegio").equals("D")){
                registerForContextMenu(lvTarefas);
                lvTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        view.performLongClick();
                    }
                });
            }
        }else{
            fab.setVisibility(View.GONE);
            lvTarefas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TarefasActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("Responder tarefa");
                    builder.setMessage("Deseja responder a essa tarefa?");
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int in) {
                            Tarefa tarefa = lista.get(i);
                            String id = tarefa.getId();
                            Intent intent = new Intent(TarefasActivity.this, RespostaTarefaActivity.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                        }
                    }).setNegativeButton("Não", null);
                    builder.create().show();
                }
            });
        }

        lvTarefas.setTextFilterEnabled(true);
        sv = findViewById(R.id.svTarefas);
        sv.setOnQueryTextListener(this);

        carregar();
    }

    @Override
    public boolean onQueryTextChange(String newText){
        listaQuery.clear();
        if (TextUtils.isEmpty(newText)) {
            listaQuery.addAll(lista);
        } else {
            String queryText = newText.toLowerCase();
            for(Tarefa u : lista){
                if(u.getDescricao().toLowerCase().contains(queryText)){
                    listaQuery.add(u);
                }
            }
        }
        lvTarefas.setAdapter(new TarefasActivity.ListViewAdapter(listaQuery, TarefasActivity.this));
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query){
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Selecione a Ação");
        menu.add(0,v.getId(),0,"Editar");
        menu.add(0,v.getId(),0,"Excluir");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Integer pos = info.position;
        Tarefa tarefa = listaQuery.get(pos);
        final String id = tarefa.getId();

        if(item.getTitle() == "Editar"){
            Intent i = new Intent(TarefasActivity.this, EditTarefaActivity.class);
            i.putExtra("id",id);
            startActivity(i);
        }
        if(item.getTitle() == "Excluir"){
            AlertDialog.Builder builder = new AlertDialog.Builder(TarefasActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Deseja excluir esse registro?");
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("deleteId", id.toString());

                    StringRequest sr = CRUD.excluir(JSON_URL, id.toString(), getApplicationContext());
                    RequestQueue rq = VolleySingleton.getInstance(TarefasActivity.this).getRequestQueue();
                    rq.add(sr);
                    lvTarefas.setAdapter(null);
                    lista.clear();
                    carregar();
                }
            }).setNegativeButton("Não", null);
            builder.create().show();
        }
        return true;
    }

    public void carregar(){
        pd.open();
        lista.clear();
        //Requisição à página por método POST
        StringRequest sr = CRUD.selecionar(JSON_URL,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Recebe os objetos do JSON
                try{
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("tarefas");
                    //Para cada objeto, adiciona na lista
                    for(int i=0;i<ja.length();i++){
                        JSONObject objeto = ja.getJSONObject(i);
                        Integer idDisciplina=objeto.getInt("id_disciplina"),
                                idTutor=objeto.getInt("id_usuario");
                        String id=objeto.getString("id_tarefa"),descricao=objeto.getString("descricao"),disciplina=objeto.getString("disciplina"),
                                tutor=objeto.getString("tutor");
                        String dataEnvio=objeto.getString("data_envio");

                        Tarefa tarefa = new Tarefa(id, descricao, dataEnvio, idDisciplina, idTutor, disciplina, tutor);
                        lista.add(tarefa);
                        listaQuery.add(tarefa);
                    }
                    //Cria um adapter para a lista
                    ListViewAdapter adapter = new ListViewAdapter(lista, getApplicationContext());
                    lvTarefas.setAdapter(adapter);
                }catch(JSONException e){
                    //Caso haja excessões com o JSON
                    e.printStackTrace();
                }
            }
        },TarefasActivity.this);
        //Adiciona a requisição à fila
        RequestQueue rq = VolleySingleton.getInstance(TarefasActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }

    //Classe interna para criar o adapter da classe externa
    class ListViewAdapter extends ArrayAdapter<Tarefa> {
        //Lista com os adapter e contexto do aplicativo
        private List<Tarefa> lista;
        private Context contexto;

        //Método construtor
        private ListViewAdapter(List<Tarefa> lista, Context contexto){
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
}
