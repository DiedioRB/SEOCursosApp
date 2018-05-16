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
import br.com.seocursos.seocursos.ConstClasses.Usuario;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class DisciplinasActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/disciplinas.php";
    ListView lvDisciplinas;
    List<Disciplina> lista;
    List<Disciplina> listaQuery;
    FloatingActionButton fab;
    SearchView sv;

    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disciplinas);

        pd = new ProgressDialogHelper(DisciplinasActivity.this);

        lvDisciplinas = (ListView)findViewById(R.id.lvDisciplinas);
        lista = new ArrayList<Disciplina>();
        listaQuery = new ArrayList<Disciplina>();
        registerForContextMenu(lvDisciplinas);

        fab = findViewById(R.id.fabDisciplinas);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DisciplinasActivity.this, AddDisciplinaActivity.class);
                startActivity(i);
            }
        });
        lvDisciplinas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.performLongClick();
            }
        });

        lvDisciplinas.setTextFilterEnabled(true);
        sv = findViewById(R.id.svDisciplinas);
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
            for(Disciplina u : lista){
                if(u.getNome().toLowerCase().contains(queryText) ||
                        u.getArea().toLowerCase().contains(queryText)){
                    listaQuery.add(u);
                }
            }
        }
        lvDisciplinas.setAdapter(new DisciplinasActivity.ListViewAdapter(listaQuery, DisciplinasActivity.this));
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
        Disciplina disciplina = listaQuery.get(pos);
        final String id = disciplina.getId();

        if(item.getTitle() == "Editar"){
            Intent i = new Intent(DisciplinasActivity.this, EditDisciplinaActivity.class);
            i.putExtra("id", id);
            startActivity(i);
        }
        if(item.getTitle() == "Excluir"){
            AlertDialog.Builder builder = new AlertDialog.Builder(DisciplinasActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Deseja excluir esse registro?");
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("deleteId", id.toString());

                    StringRequest sr = CRUD.excluir(JSON_URL, id.toString(), getApplicationContext());
                    RequestQueue rq = VolleySingleton.getInstance(DisciplinasActivity.this).getRequestQueue();
                    rq.add(sr);
                    lvDisciplinas.setAdapter(null);
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
        //Requisição à página por método POST
        StringRequest sr = CRUD.selecionar(JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Recebe os objetos do JSON
                        try {
                            JSONObject jo = new JSONObject(response);
                            JSONArray ja = jo.getJSONArray("disciplinas");
                            //Para cada objeto, adiciona na lista
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject objeto = ja.getJSONObject(i);

                                String id = objeto.getString("id_disciplina"), nome = objeto.getString("nome_disciplina"),
                                        nivel = objeto.getString("nivel"),cargaHoraria = objeto.getString("carga_horaria"),
                                        area = objeto.getString("area"),duracao = objeto.getString("duracao"),
                                        curso = objeto.getString("nome_curso"),modalidade = objeto.getString("id_modalidade"),
                                        idCurso = objeto.getString("id_curso");

                                Disciplina disciplina = new Disciplina(id, nome, nivel, cargaHoraria, area, duracao, modalidade, idCurso, curso);
                                lista.add(disciplina);
                                listaQuery.add(disciplina);
                            }
                            //Cria um adapter para a lista
                            ListViewAdapter adapter = new ListViewAdapter(lista, getApplicationContext());
                            lvDisciplinas.setAdapter(adapter);
                        } catch (JSONException e) {
                            //Caso haja excessões com o JSON
                            e.printStackTrace();
                        }
                    }
                }, getApplicationContext());
        //Adiciona a requisição à fila
        RequestQueue rq = VolleySingleton.getInstance(DisciplinasActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }
    //Classe interna para criar o adapter da classe externa
    class ListViewAdapter extends ArrayAdapter<Disciplina> {
        //Lista com os adapter e contexto do aplicativo
        private List<Disciplina> lista;
        private Context contexto;

        //Método construtor
        private ListViewAdapter(List<Disciplina> lista, Context contexto){
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
            Disciplina disciplina = lista.get(position);
            //Define conteúdo do item
            titulo.setText(disciplina.getNome().toString());
            subtitulo.setText(disciplina.getCurso().toString());
            //Retorna a View (Item)
            return listViewItem;
        }
    }
}
