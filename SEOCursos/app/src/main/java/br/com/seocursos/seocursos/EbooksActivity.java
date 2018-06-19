package br.com.seocursos.seocursos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.folioreader.FolioReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

import br.com.seocursos.seocursos.ConstClasses.Ebook;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class EbooksActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/ebooks.php";
    private static final String EBOOKS_URL = "https://www.seocursos.com.br/Ebooks/Livros/";
    private static final int ASK_READ_PERMISSION = 1;

    ListView lv;
    List<Ebook> lista;
    List<Ebook> listaQuery;
    FloatingActionButton fab;
    SearchView sv;

    ProgressDialogHelper pd;
    SharedPreferencesHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebooks);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        pd = new ProgressDialogHelper(EbooksActivity.this);
        helper = new SharedPreferencesHelper(EbooksActivity.this);

        lv = (ListView)findViewById(R.id.lv);
        lista = new ArrayList<Ebook>();
        listaQuery = new ArrayList<Ebook>();
        fab = findViewById(R.id.fab);

        registerForContextMenu(lv);
        if(!helper.getString("privilegio").equals("D")){
            fab.setVisibility(View.GONE);
        }

        lv.setTextFilterEnabled(true);
        sv = findViewById(R.id.sv);
        sv.setOnQueryTextListener(this);
        registerForContextMenu(lv);

        carregar();
    }

    @Override
    public boolean onQueryTextChange(String newText){
        listaQuery.clear();
        if (TextUtils.isEmpty(newText)) {
            listaQuery.addAll(lista);
        } else {
            String queryText = newText.toLowerCase();
            for(Ebook u : lista){
                if(u.getTitulo().toLowerCase().contains(queryText) ||
                        u.getArea().toLowerCase().contains(queryText)){
                    listaQuery.add(u);
                }
            }
        }
        lv.setAdapter(new EbooksActivity.ListViewAdapter(listaQuery, EbooksActivity.this));
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
        if(helper.getString("privilegio").equals("D")) {
            getMenuInflater().inflate(R.menu.edit_menu, menu);
        }else if(helper.getString("privilegio").equals("A")) {
            menu.add(0, v.getId(), 0, getResources().getString(R.string.ler));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Integer pos = info.position;
        Ebook ebook = listaQuery.get(pos);
        final String id = ebook.getId();

        if(item.getTitle() == getResources().getString(R.string.ler)){
            //FolioReader reader = FolioReader.getInstance(EbooksActivity.this);
            //reader.openBook(ebook.getTitulo());
        }
        if(item.getItemId() == R.id.editar){
        //    Intent i = new Intent(EbooksActivity.this, EditEbookActivity.class);
        //    i.putExtra("id", id);
        //    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //    startActivity(i);
        }
        if(item.getItemId() == R.id.excluir){
            AlertDialog.Builder builder = new AlertDialog.Builder(EbooksActivity.this);
            builder.setCancelable(true);
            builder.setTitle(getResources().getString(R.string.desejaExcluirRegistro));
            builder.setPositiveButton(getResources().getString(R.string.sim), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    StringRequest sr = CRUD.excluir(JSON_URL, id.toString(), getApplicationContext());
                    RequestQueue rq = VolleySingleton.getInstance(EbooksActivity.this).getRequestQueue();
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
        //Requisição à página por método POST
        StringRequest sr = CRUD.selecionar(JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Recebe os objetos do JSON
                        try {
                            JSONObject jo = new JSONObject(response);
                            JSONArray ja = jo.getJSONArray("ebooks");
                            //Para cada objeto, adiciona na lista
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject objeto = ja.getJSONObject(i);

                                Ebook ebook= new Ebook(objeto.getString("id_ebook"), objeto.getString("titulo_livro"),
                                        objeto.getString("autor"), objeto.getString("ano_edicao"),
                                        objeto.getString("editora"), objeto.getString("nome"),
                                        objeto.getString("link"));
                                lista.add(ebook);
                                listaQuery.add(ebook);
                            }
                            //Cria um adapter para a lista
                            EbooksActivity.ListViewAdapter adapter = new EbooksActivity.ListViewAdapter(lista, getApplicationContext());
                            lv.setAdapter(adapter);
                        } catch (JSONException e) {
                            //Caso haja excessões com o JSON
                            e.printStackTrace();
                        }
                    }
                },EbooksActivity.this);
        //Adiciona a requisição à fila
        RequestQueue rq = VolleySingleton.getInstance(EbooksActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
    }

    //Classe interna para criar o adapter da classe externa
    class ListViewAdapter extends ArrayAdapter<Ebook> {
        //Lista com os adapter e contexto do aplicativo
        private List<Ebook> lista;
        private Context contexto;

        //Método construtor
        private ListViewAdapter(List<Ebook> lista, Context contexto){
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
            Ebook ebook = lista.get(position);
            //Define conteúdo do item
            titulo.setText(ebook.getTitulo().toString());
            subtitulo.setText(ebook.getArea().toString());
            //Retorna a View (Item)
            return listViewItem;
        }
    }
}
