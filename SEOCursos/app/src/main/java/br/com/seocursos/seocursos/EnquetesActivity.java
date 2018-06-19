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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import br.com.seocursos.seocursos.ConstClasses.Enquete;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class EnquetesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/enquetes.php";
    ListView lv;
    List<Enquete> lista;
    List<Enquete> listaQuery;
    FloatingActionButton fab;
    SearchView sv;

    ProgressDialogHelper pd;
    SharedPreferencesHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquetes);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        pd = new ProgressDialogHelper(EnquetesActivity.this);
        helper = new SharedPreferencesHelper(EnquetesActivity.this);

        lv = (ListView) findViewById(R.id.lv);
        lista = new ArrayList<Enquete>();
        listaQuery = new ArrayList<Enquete>();
        fab = findViewById(R.id.fab);

        if(!helper.getString("privilegio").equals("A")) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(EnquetesActivity.this, AddEnqueteActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });
            if(helper.getString("privilegio").equals("D")) {
                registerForContextMenu(lv);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        view.performLongClick();
                    }
                });
            }
        }else{
            fab.setVisibility(View.GONE);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                    String idEnquete = listaQuery.get(i).getId();

                    Intent intent = new Intent(EnquetesActivity.this, RespostaEnqueteActivity.class);
                    intent.putExtra("id", idEnquete);
                    startActivity(intent);
                }
            });
        }

        lv.setTextFilterEnabled(true);
        sv = findViewById(R.id.sv);
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
            for(Enquete u : lista){
                if(u.getPergunta().toLowerCase().contains(queryText)){
                    listaQuery.add(u);
                }
            }
        }
        lv.setAdapter(new EnquetesActivity.ListViewAdapter(listaQuery, EnquetesActivity.this));
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query){
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(getResources().getString(R.string.selecioneAcao));
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        menu.add(0,v.getId(),0,getResources().getString(R.string.resultado));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Integer pos = info.position;
        Enquete enquete = listaQuery.get(pos);
        final String id = enquete.getId();

        if (item.getTitle() == getResources().getString(R.string.resultado)){
            Intent i = new Intent(EnquetesActivity.this, RespostaEnqueteActivity.class);
            i.putExtra("id",id);
            startActivity(i);
        }
        if (item.getItemId() == R.id.editar) {
            Intent i = new Intent(EnquetesActivity.this, EditEnqueteActivity.class);
            i.putExtra("id",id);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        if (item.getItemId() == R.id.excluir) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EnquetesActivity.this);
            builder.setCancelable(true);
            builder.setTitle(getResources().getString(R.string.desejaExcluirRegistro));
            builder.setPositiveButton(getResources().getString(R.string.sim), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("deleteId", id.toString());

                    StringRequest sr = CRUD.excluir(JSON_URL, id.toString(), getApplicationContext());
                    RequestQueue rq = VolleySingleton.getInstance(EnquetesActivity.this).getRequestQueue();
                    rq.add(sr);
                    lv.setAdapter(null);
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
        lista.clear();
        //Requisição à página por método POST
        StringRequest sr = CRUD.selecionar(JSON_URL,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Recebe os objetos do JSON
                try{
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("enquetes");
                    //Para cada objeto, adiciona na lista
                    for(int i=0;i<ja.length();i++){
                        JSONObject objeto = ja.getJSONObject(i);
                        String id=objeto.getString("id_enquete"),pergunta=objeto.getString("pergunta"),
                                valorA=objeto.getString("valorA"),valorB=objeto.getString("valorB"),
                                valorC=objeto.getString("valorC"),valorD=objeto.getString("valorD"),
                                valorE=objeto.getString("valorE");

                        Enquete enquete = new Enquete(id, pergunta, valorA, valorB, valorC, valorD, valorE);
                        lista.add(enquete);
                        listaQuery.add(enquete);
                    }
                    //Cria um adapter para a lista
                    ListViewAdapter adapter = new ListViewAdapter(lista, getApplicationContext());
                    lv.setAdapter(adapter);
                }catch(JSONException e){
                    //Caso haja excessões com o JSON
                    e.printStackTrace();
                }
            }
        },EnquetesActivity.this);
        //Adiciona a requisição à fila
        RequestQueue rq = VolleySingleton.getInstance(EnquetesActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }

    //Classe interna para criar o adapter da classe externa
    class ListViewAdapter extends ArrayAdapter<Enquete> {
        //Lista com os adapter e contexto do aplicativo
        private List<Enquete> lista;
        private Context contexto;

        //Método construtor
        private ListViewAdapter(List<Enquete> lista, Context contexto){
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
            Enquete enquete = lista.get(position);
            //Define conteúdo do item
            titulo.setTextSize(18);
            titulo.setText(enquete.getPergunta().toString());
            subtitulo.setVisibility(View.GONE);
            //Retorna a View (Item)
            return listViewItem;
        }
    }
}
