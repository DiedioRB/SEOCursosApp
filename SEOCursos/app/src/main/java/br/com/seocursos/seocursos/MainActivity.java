package br.com.seocursos.seocursos;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.Locale;

import br.com.seocursos.seocursos.Fragments.AdministradorMenu;
import br.com.seocursos.seocursos.Fragments.AlunoMenu;
import br.com.seocursos.seocursos.Fragments.TutorMenu;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class MainActivity extends AppCompatActivity {
    private String nome;

    TextView userName;
    LinearLayout container;
    SharedPreferencesHelper helper;

    Snackbar snackbar;

    ProgressDialogHelper pd;

    ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new SharedPreferencesHelper(MainActivity.this);
        if(helper.getString("linguagem") != null) {
            changeLanguage(helper.getString("linguagem"));
        }else{
            helper.setString("linguagem", "pt");
            changeLanguage(helper.getString("linguagem"));
        }

        setContentView(R.layout.activity_main);
        new FlurryAgent.Builder().withLogEnabled(false).build(this, "62X37KYC3XJ5XPQF2RP2");

        userName = findViewById(R.id.userName);
        container = findViewById(R.id.container);

        pd = new ProgressDialogHelper(MainActivity.this);
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        snackbar = Snackbar.make(container, getResources().getString(R.string.semConexaoComAInternet), Snackbar.LENGTH_SHORT);
        if(!checkInternet()) {
            snackbar.show();
        }

        if (!helper.getBoolean("login")) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        nome = helper.getString("nome");
        userName.setText(nome);

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
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.erroAoCarregarMenu), Toast.LENGTH_SHORT).show();
                        sair();
                        break;
                }
                LayoutInflater inflater = getLayoutInflater();
                View v = menu.onCreateView(inflater, null, savedInstanceState);
                container.addView(v, 1);
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
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Verifica o item selecionado
        switch (item.getItemId()) {
            case R.id.minhaConta:
                Intent i = new Intent(MainActivity.this, MinhaContaActivity.class);
                startActivity(i);
                return true;
            case R.id.portugues:
                helper.setString("linguagem", "pt");
                changeLanguage(helper.getString("linguagem"));
                finish();
                startActivity(getIntent());
                return true;
            case R.id.ingles:
                helper.setString("linguagem", "en");
                changeLanguage(helper.getString("linguagem"));
                finish();
                startActivity(getIntent());
                return true;
            case R.id.espanhol:
                helper.setString("linguagem", "es");
                changeLanguage(helper.getString("linguagem"));
                finish();
                startActivity(getIntent());
                return true;
            case R.id.sair:
                sair();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean checkInternet(){
        pd.open();
        boolean is3G = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        boolean retorno;

        if(!(is3G || isWifi)){
            retorno = false;
        }else{
            retorno = true;
        }
        pd.close();
        return retorno;
    }

    public void usuarios(View v){
        goTo(UsuariosActivity.class);
    }
    public void cursos(View v){
        goTo(CursosActivity.class);
    }
    public void ebooks(View v){
        goTo(EbooksActivity.class);
    }
    public void disciplinas(View v){
        goTo(DisciplinasActivity.class);
    }
    public void tarefas(View v){
        goTo(TarefasActivity.class);
    }
    public void eventos(View v){
        goTo(EventosActivity.class);
    }
    public void enquetes(View v){
        goTo(EnquetesActivity.class);
    }
    public void chat(View v){
        goTo(ChatActivity.class);
    }
    public void graficos(View v){
        goTo(GraficosActivity.class);
    }
    public void meusCursos(View v){
        goTo(MeusCursosActivity.class);
    }

    public void goTo(Class classe){
        if(checkInternet()) {
            Intent i = new Intent(MainActivity.this, classe);
            startActivity(i);
        }else{
            if(!snackbar.isShown()) {
                snackbar.show();
            }
        }
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
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void changeLanguage(String codigo){
        Resources res = getResources(); // os recursos
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration(); // configuração dos recursos
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { // verifica a versao do android com o sdk
            conf.setLocale(new Locale(codigo.toLowerCase()));
        }else {
            conf.locale = new Locale(codigo.toLowerCase());
        }
        res.updateConfiguration(conf, dm);
    }
}
