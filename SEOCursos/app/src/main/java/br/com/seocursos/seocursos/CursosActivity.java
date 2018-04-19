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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.seocursos.seocursos.ConstClasses.Curso;
import br.com.seocursos.seocursos.ConstClasses.Disciplina;
import br.com.seocursos.seocursos.Outros.CRUD;

public class CursosActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/cursos.php";
    ListView lvCursos;
    List<Curso> lista;
    FloatingActionButton fab;
    SearchView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursos);

        lvCursos = (ListView)findViewById(R.id.lvCursos);
        lista = new ArrayList<Curso>();
        fab = findViewById(R.id.fabCursos);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CursosActivity.this, AddCursoActivity.class);
                startActivity(i);
            }
        });

        registerForContextMenu(lvCursos);

        lvCursos.setTextFilterEnabled(true);
        sv = findViewById(R.id.svCursos);
        sv.setOnQueryTextListener(this);

        carregar();
    }

    @Override
    public boolean onQueryTextChange(String newText){
        if (TextUtils.isEmpty(newText)) {
            lvCursos.clearTextFilter();
        } else {
            lvCursos.setFilterText(newText);
        }
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
        Curso curso = lista.get(pos);
        final String id = curso.getId();

        if(item.getTitle() == "Editar"){
            Intent i = new Intent(CursosActivity.this, EditCursoActivity.class);
            i.putExtra("id", id);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        if(item.getTitle() == "Excluir"){
            AlertDialog.Builder builder = new AlertDialog.Builder(CursosActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Deseja excluir esse registro?");
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("deleteId", id.toString());

                    StringRequest sr = CRUD.excluir(JSON_URL, id.toString(), getApplicationContext());
                    RequestQueue rq = VolleySingleton.getInstance(CursosActivity.this).getRequestQueue();
                    rq.add(sr);
                    lvCursos.setAdapter(null);
                    lista.clear();
                    carregar();
                }
            }).setNegativeButton("Não", null);
            builder.create().show();
        }
        return true;
    }

    public void carregar(){
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
                                Integer cargaHoraria = objeto.getInt("carga_horaria");
                                String id = objeto.getString("id_curso"), nome = objeto.getString("nome_curso"), area = objeto.getString("area"),
                                        preRequisito = objeto.getString("prerequisito"),
                                        descricao = objeto.getString("descricao"), tipo = objeto.getString("tipo_curso");
                                Double preco = objeto.getDouble("preco");

                                Curso curso = new Curso(id, nome, preco, area, cargaHoraria, preRequisito, descricao, tipo);
                                lista.add(curso);
                            }
                            //Cria um adapter para a lista
                            ListViewAdapter adapter = new ListViewAdapter(lista, getApplicationContext());
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
    }

    //Classe interna para criar o adapter da classe externa
    class ListViewAdapter extends ArrayAdapter<Curso> {
        //Lista com os adapter e contexto do aplicativo
        private List<Curso> lista;
        private Context contexto;
        private List<Curso> orig;

        //Método construtor
        private ListViewAdapter(List<Curso> lista, Context contexto){
            //para a classe mãe
            super(contexto, R.layout.list_item, lista);

            this.lista = lista;
            this.contexto = contexto;
        }

        @Override
        public int getCount() {
            return lista.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    final FilterResults oReturn = new FilterResults();
                    final ArrayList<Curso> results = new ArrayList<Curso>();
                    if (orig == null) {
                        orig = lista;
                    }
                    if (constraint != null) {
                        if (orig != null && orig.size() > 0) {
                            for (final Curso g : orig) {
                                if (g.getNome().toLowerCase().contains(constraint.toString())) {
                                    results.add(g);
                                }
                            }
                        }
                        oReturn.values = results;
                    }
                    return oReturn;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    lista = (ArrayList<Curso>) results.values;
                    notifyDataSetChanged();
                }
            };
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
