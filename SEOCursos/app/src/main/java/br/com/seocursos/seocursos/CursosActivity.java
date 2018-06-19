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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
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

import br.com.seocursos.seocursos.ConstClasses.Curso;
import br.com.seocursos.seocursos.ConstClasses.CursoGraduacao;
import br.com.seocursos.seocursos.ConstClasses.CursoGratis;
import br.com.seocursos.seocursos.ConstClasses.CursoTecnico;
import br.com.seocursos.seocursos.ConstClasses.Usuario;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class CursosActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/cursos.php";
    ListView lvCursos;
    List<Curso> lista;
    List<Curso> listaQuery;
    FloatingActionButton fab;
    SearchView sv;

    ProgressDialogHelper pd;
    SharedPreferencesHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursos);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        pd = new ProgressDialogHelper(CursosActivity.this);
        helper = new SharedPreferencesHelper(CursosActivity.this);

        lvCursos = (ListView)findViewById(R.id.lvCursos);
        lista = new ArrayList<Curso>();
        listaQuery = new ArrayList<Curso>();
        fab = findViewById(R.id.fabCursos);

        if(helper.getString("privilegio").equals("D")) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(CursosActivity.this, AddCursoActivity.class);
                    startActivity(i);
                }
            });
            registerForContextMenu(lvCursos);
            lvCursos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    view.performLongClick();
                }
            });
        }else{
            fab.setVisibility(View.GONE);
            if(helper.getString("privilegio").equals("A")){
                lvCursos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent j = new Intent(CursosActivity.this, InfoCursoActivity.class);
                        Curso curso = listaQuery.get(i);
                        j.putExtra("id",curso.getId());
                        j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(j);
                    }
                });
            }
        }

        sv = findViewById(R.id.svCursos);
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
            for(Curso u : lista){
                if(u.getNome().toLowerCase().contains(queryText) ||
                        u.getDescricao().toLowerCase().contains(queryText)){
                    listaQuery.add(u);
                }
            }
        }
        lvCursos.setAdapter(new CursosActivity.ListViewAdapter(listaQuery, CursosActivity.this));
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query){
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(getResources().getString(R.string.selecioneAcao));
        getMenuInflater().inflate(R.menu.edit_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Integer pos = info.position;
        Curso curso = listaQuery.get(pos);
        final String id = curso.getId();

        if(item.getItemId() == R.id.editar){
            Intent i = new Intent(CursosActivity.this, EditCursoActivity.class);
            i.putExtra("id", id);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        if(item.getItemId() == R.id.excluir){
            AlertDialog.Builder builder = new AlertDialog.Builder(CursosActivity.this);
            builder.setCancelable(true);
            builder.setTitle(getResources().getString(R.string.desejaExcluirRegistro));
            builder.setPositiveButton(getResources().getString(R.string.sim), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("deleteId", id.toString());

                    StringRequest sr = CRUD.excluir(JSON_URL, id.toString(), getApplicationContext());
                    RequestQueue rq = VolleySingleton.getInstance(CursosActivity.this).getRequestQueue();
                    rq.add(sr);
                    lvCursos.setAdapter(null);
                    lista.clear();
                    listaQuery.clear();
                    carregar();
                }
            }).setNegativeButton(getResources().getString(R.string.nao), null);
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
                            JSONArray ja = jo.getJSONArray("cursos");
                            //Para cada objeto, adiciona na lista
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject objeto = ja.getJSONObject(i);
                                String id = objeto.getString("idCurso"), nome = objeto.getString("nome_curso"), area = objeto.getString("area"),
                                        preRequisito = objeto.getString("prerequisito"),
                                        descricao = objeto.getString("descricao"), tipo = objeto.getString("tipo_curso"),
                                        cargaHoraria = objeto.getString("carga_horaria");
                                Double preco = objeto.getDouble("preco");

                                Curso curso = new Curso(id, nome, preco, area, cargaHoraria, preRequisito, descricao, tipo);
                                String disponivel, nivel, modalidade, duracao, titulacao, notaMec, status;
                                switch(tipo){
                                    case "F":
                                        disponivel = objeto.getString("tempo_disponivel");
                                        nivel = objeto.getString("nivel");

                                        curso = new CursoGratis(curso, disponivel, nivel);
                                        break;
                                    case "T":
                                        modalidade = objeto.getString("id_modalidade");
                                        duracao = objeto.getString("duracao");

                                        curso = new CursoTecnico(curso, modalidade, duracao);
                                        break;
                                    case "G":
                                        modalidade = objeto.getString("id_modalidade");
                                        titulacao = objeto.getString("titulacao");
                                        duracao = objeto.getString("duracao");
                                        notaMec = objeto.getString("nota_avaliacao");

                                        curso = new CursoGraduacao(curso, modalidade, titulacao, duracao, notaMec);
                                        break;
                                    case "P":
                                        modalidade = objeto.getString("id_modalidade");
                                        status = objeto.getString("estado");
                                        duracao = objeto.getString("duracao");
                                        notaMec = objeto.getString("nota_avaliacao");

                                        curso = new CursoGraduacao(curso, modalidade, status, duracao, notaMec);
                                        break;
                                }
                                lista.add(curso);
                                listaQuery.add(curso);
                            }
                            //Cria um adapter para a lista
                            ListViewAdapter adapter = new ListViewAdapter(listaQuery, getApplicationContext());
                            lvCursos.setAdapter(adapter);
                        } catch (JSONException e) {
                            //Caso haja excessões com o JSON
                            e.printStackTrace();
                        }
                    }
                },CursosActivity.this);
        //Adiciona a requisição à fila
        RequestQueue rq = VolleySingleton.getInstance(CursosActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }

    //Classe interna para criar o adapter da classe externa
    class ListViewAdapter extends ArrayAdapter<Curso> {
        //Lista com os adapter e contexto do aplicativo
        private List<Curso> lista;
        private Context contexto;

        //Método construtor
        private ListViewAdapter(List<Curso> lista, Context contexto){
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
            Curso curso = lista.get(position);
            //Define conteúdo do item
            titulo.setText(curso.getNome().toString());
            subtitulo.setText(curso.getDescricao().toString());
            //Retorna a View (Item)
            return listViewItem;
        }
    }
}
