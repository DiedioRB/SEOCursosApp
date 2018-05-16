package br.com.seocursos.seocursos;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import br.com.seocursos.seocursos.Fragments.AdministradorMenu;
import br.com.seocursos.seocursos.Fragments.AlunoMenu;
import br.com.seocursos.seocursos.Fragments.TutorMenu;

public class MainActivity extends AppCompatActivity {
    private String nome;

    LinearLayout container;
    SharedPreferencesHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent(getIntent());

        container = findViewById(R.id.container);

        helper = new SharedPreferencesHelper(MainActivity.this);
        if (!helper.getBoolean("login")) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        nome = helper.getString("nome");

        try {
            Fragment menu = null;
            String privilegio = helper.getString("privilegio");
            if (privilegio != null) {
                switch (helper.getString("privilegio")) {
                    case "A":
                        menu = AlunoMenu.newInstance();
                        break;
                    case "T":
                        menu = TutorMenu.newInstance();
                        break;
                    case "D":
                        menu = AdministradorMenu.newInstance();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, "Erro ao carregar o menu!", Toast.LENGTH_SHORT).show();
                        sair();
                        break;
                }
                LayoutInflater inflater = getLayoutInflater();
                View v = menu.onCreateView(inflater, null, savedInstanceState);
                container.addView(v, 0);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null){
            String recipeId = appLinkData.getLastPathSegment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.sair:
                sair();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void usuarios(View v){
        Intent i = new Intent(MainActivity.this, UsuariosActivity.class);
        startActivity(i);
    }
    public void cursos(View v){
        Intent i = new Intent(MainActivity.this, CursosActivity.class);
        startActivity(i);
    }
    public void ebooks(View v){
        Intent i = new Intent(MainActivity.this, EbooksActivity.class);
        startActivity(i);
    }
    public void disciplinas(View v){
        Intent i = new Intent(MainActivity.this, DisciplinasActivity.class);
        startActivity(i);
    }
    public void tarefas(View v){
        Intent i = new Intent(MainActivity.this, TarefasActivity.class);
        startActivity(i);
    }
    public void eventos(View v){
        Intent i = new Intent(MainActivity.this, EventosActivity.class);
        startActivity(i);
    }
    public void enquetes(View v){
        Intent i = new Intent(MainActivity.this, EnquetesActivity.class);
        startActivity(i);
    }
    public void chat(View v){
        Intent i = new Intent(MainActivity.this, ChatActivity.class);
        startActivity(i);
    }
    public void graficos(View v){
        Intent i = new Intent(MainActivity.this, GraficosActivity.class);
        startActivity(i);
    }
    public void sair(){
        helper.setBoolean("login", false);
        helper.setString("id", null);
        helper.setString("nome", null);
        helper.setString("email", null);
        helper.setString("privilegio", null);

        LoginManager.getInstance().logOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, gso);
        client.signOut();

        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
