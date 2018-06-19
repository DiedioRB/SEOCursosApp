package br.com.seocursos.seocursos;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.seocursos.seocursos.ConstClasses.Chat;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class ChatActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/chat.php";
    private String idUsuario;

    ListView lv;
    TextInputEditText mensagem;
    Button btn;
    SwipyRefreshLayout swipe;

    List<Chat> lista;

    SharedPreferencesHelper helper;
    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        pd = new ProgressDialogHelper(ChatActivity.this);

        helper = new SharedPreferencesHelper(ChatActivity.this);
        idUsuario = helper.getString("id");

        lv = findViewById(R.id.list);
        mensagem = findViewById(R.id.mensagem);
        btn = findViewById(R.id.confirmar);
        swipe = findViewById(R.id.swipe);

        lista = new ArrayList<>();

        swipe.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                carregar();
            }
        });

        carregar();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.open();
                String msg = mensagem.getText().toString();
                mensagem.setText("");

                Map<String,String> params = new HashMap<String,String>();
                params.put("mensagem",msg);
                params.put("idUsuario", idUsuario);

                StringRequest sr = CRUD.inserir(JSON_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        carregar();
                    }
                },params,ChatActivity.this);
                RequestQueue rq = VolleySingleton.getInstance(ChatActivity.this).getRequestQueue();
                rq.add(sr);
                rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                    @Override
                    public void onRequestFinished(Request<Object> request) {
                        pd.close();
                    }
                });
            }
        });
    }
    public void carregar(){
        pd.open();
        lista.clear();
        StringRequest sr = CRUD.selecionar(JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    JSONArray ja = jo.getJSONArray("chat");
                    for(int i=0;i<ja.length(); i++){
                        JSONObject objeto = ja.getJSONObject(i);
                        String id = objeto.getString("id_usuario");
                        String nome = objeto.getString("nome");
                        String mensagem = objeto.getString("mensagem");

                        nome = nome.replace("\\", "").replace("&quot;", "\"")
                                .replace("&lt;", "<").replace("&gt;", ">");
                        mensagem = mensagem.replace("\\", "").replace("&quot;", "\"")
                                .replace("&lt;", "<").replace("&gt;", ">");

                        Chat chat = new Chat(id, nome, mensagem);

                        lista.add(chat);
                        System.out.println(chat.getNome()+" "+chat.getMensagem());
                    }
                    ChatAdapter adapter = new ChatAdapter(lista, ChatActivity.this);
                    lv.setAdapter(adapter);

                    lv.post(new Runnable() {
                        @Override
                        public void run() {
                            lv.smoothScrollToPosition(lista.size());
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, ChatActivity.this);
        RequestQueue rq = VolleySingleton.getInstance(ChatActivity.this).getRequestQueue();
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pd.close();
            }
        });
        if(swipe.isRefreshing()){
            swipe.setRefreshing(false);
        }
    }

    //Classe interna para criar o adapter da classe externa
    class ChatAdapter extends ArrayAdapter<Chat> {
        //Lista com os adapter e contexto do aplicativo
        private List<Chat> lista;
        private Context contexto;

        //Método construtor
        private ChatAdapter(List<Chat> lista, Context contexto){
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
            View listViewItem = inflater.inflate(R.layout.chat_balloon, null, true);
            TextView info = listViewItem.findViewById(R.id.info);
            TextView mensagem = listViewItem.findViewById(R.id.mensagem);

            //Recebe o item da posição solicitada
            Chat chat = lista.get(position);
            //Define conteúdo do item
            info.setText(chat.getNome());
            mensagem.setText(chat.getMensagem());

            if (chat.getId().equals(idUsuario)) {
                info.setGravity(Gravity.END);
                mensagem.setGravity(Gravity.END);
            }

            //Retorna a View (Item)
            return listViewItem;
        }
    }
}
