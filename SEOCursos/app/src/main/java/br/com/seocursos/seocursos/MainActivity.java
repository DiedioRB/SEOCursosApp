package br.com.seocursos.seocursos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    SharedPreferencesSingleton helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        helper = SharedPreferencesSingleton.getInstance(MainActivity.this);
        if(!helper.getBoolean("login")){
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
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
    public void sair(){
        helper.setBoolean("login", false);
        helper.setString("id", null);
        helper.setString("nome", null);
        helper.setString("email", null);
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }
}
