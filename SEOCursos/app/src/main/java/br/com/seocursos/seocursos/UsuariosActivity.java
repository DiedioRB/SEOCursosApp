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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.seocursos.seocursos.ConstClasses.Usuario;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class UsuariosActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/usuarios.php";
    ListView lvUsuarios;
    List<Usuario> lista;
    List<Usuario> listaQuery;
    FloatingActionButton fab;
    SearchView sv;

    RequestQueue rq;
    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        pd = new ProgressDialogHelper(UsuariosActivity.this);

        lvUsuarios = (ListView)findViewById(R.id.lvUsuarios);
        lista = new ArrayList<Usuario>();
        listaQuery = new ArrayList<Usuario>();
        fab = (FloatingActionButton)findViewById(R.id.fabUsuarios);

        rq = VolleySingleton.getInstance(UsuariosActivity.this).getRequestQueue();
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UsuariosActivity.this, AddUsuarioActivity.class);
                i.putExtra("toLogin", false);
                startActivity(i);
            }
        });
        lvUsuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.performLongClick();
            }
        });
        registerForContextMenu(lvUsuarios);

        sv = findViewById(R.id.svUsuarios);
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
            for(Usuario u : lista){
                if(u.getNome().toLowerCase().contains(queryText) ||
                        u.getEmail().toLowerCase().contains(queryText)){
                    listaQuery.add(u);
                }
            }
        }
        lvUsuarios.setAdapter(new ListViewAdapter(listaQuery, UsuariosActivity.this));
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
        Usuario usuario = listaQuery.get(pos);
        final String id = usuario.getId();

        if(item.getTitle() == "Editar"){
            Intent i = new Intent(UsuariosActivity.this, EditUsuarioActivity.class);
            i.putExtra("id", id);
            startActivity(i);
        }
        if(item.getTitle() == "Excluir"){
            AlertDialog.Builder builder = new AlertDialog.Builder(UsuariosActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Deseja excluir esse registro?");
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("deleteId", id.toString());

                    StringRequest sr = CRUD.excluir(JSON_URL, id.toString(), getApplicationContext());
                    RequestQueue rq = VolleySingleton.getInstance(UsuariosActivity.this).getRequestQueue();
                    rq.add(sr);
                    lvUsuarios.setAdapter(null);
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
        //Limpa a lista antes de enchê-la
        lista.clear();
        //Requisição à página por método POST
        StringRequest sr = CRUD.selecionar(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Recebe os objetos do JSON
                try{
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("usuarios");
                    //Para cada objeto, adiciona na lista
                    for(int i=0;i<ja.length();i++){
                        JSONObject objeto = ja.getJSONObject(i);
                        Integer numero=objeto.getInt("numero");
                        String id=objeto.getString("id_usuario"),nome=objeto.getString("nome"),email=objeto.getString("email"),
                                foto=objeto.getString("foto"),cpf=objeto.getString("cpf"),
                                cep=objeto.getString("cep"),endereco=objeto.getString("endereco"),
                                cidade=objeto.getString("cidade"),estado=objeto.getString("estado"),
                                sexo=objeto.getString("sexo"),tipoUsuario=objeto.getString("tipo_usuario");

                        Usuario usuario = new Usuario(id, nome, email, foto, sexo, tipoUsuario,
                                cpf, cep, endereco, numero, cidade, estado);
                        lista.add(usuario);
                        listaQuery.add(usuario);
                    }
                    //Cria um adapter para a lista
                    ListViewAdapter adapter = new ListViewAdapter(lista, getApplicationContext());
                    lvUsuarios.setAdapter(adapter);
                }catch(JSONException e){
                    //Caso haja excessões com o JSON
                    e.printStackTrace();
                }
            }
        },getApplicationContext());
        //Adiciona a requisição à fila
        rq.add(sr);
    }

    //Classe interna para criar o adapter da classe externa
    class ListViewAdapter extends ArrayAdapter<Usuario> {
        //Lista com os adapter e contexto do aplicativo
        private List<Usuario> lista;
        private Context contexto;

        //Método construtor
        private ListViewAdapter(List<Usuario> lista, Context contexto){
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
            View listViewItem = inflater.inflate(R.layout.list_item_user, null, true);

            NetworkImageView foto = listViewItem.findViewById(R.id.userImage);
            TextView titulo = listViewItem.findViewById(R.id.titulo);
            TextView subtitulo = listViewItem.findViewById(R.id.subtitulo);

            ImageLoader il = VolleySingleton.getInstance(getApplicationContext()).getImageLoader();

            //Recebe o item da posição solicitada
            Usuario usuario = lista.get(position);
            //Define conteúdo do item
            foto.setImageUrl("https://www.seocursos.com.br/Imagens/Login/"+usuario.getFoto(),il);
            titulo.setText(usuario.getNome().toString());
            subtitulo.setText(usuario.getEmail().toString());

            //Retorna a View (Item)
            return listViewItem;
        }
    }
}
