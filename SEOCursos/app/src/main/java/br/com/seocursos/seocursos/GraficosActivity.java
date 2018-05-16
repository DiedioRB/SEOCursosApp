package br.com.seocursos.seocursos;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.RadioGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;
//TODO: Finalizar gráficos
public class GraficosActivity extends AppCompatActivity {
    private static final String RELATORIO_URL = "https://www.seocursos.com.br/Administrador/relatorio.php";
    private static final String GRAFICO_URL = "https://www.seocursos.com.br/PHP/Android/graficos.php";
    private static final int MENSALIDADES = 1;
    private static final int INADIMPLENTES = 2;

    RadioGroup grafico;
    WebView webview;

    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficos);

        pd = new ProgressDialogHelper(GraficosActivity.this);

        grafico = findViewById(R.id.selectGrafico);
        webview = findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);

        loadWebviewGraphic(MENSALIDADES);

        grafico.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.mensalidades:
                        loadWebviewGraphic(MENSALIDADES);
                        break;
                    case R.id.inadimplentes:
                        loadWebviewGraphic(INADIMPLENTES);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.relatorio_menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case R.id.mensalidades:
                downloadRelatorio(MENSALIDADES);
                break;
            case R.id.inadimplentes:
                downloadRelatorio(INADIMPLENTES);
                break;
            default:
                break;
        }

        return true;
    }

    public void downloadRelatorio(int relatorio){
        String url = RELATORIO_URL;
        switch(relatorio) {
            case MENSALIDADES:
                url += "?mensalidades&androidDownload";
                break;
            case INADIMPLENTES:
                url += "?inadimplentes&androidDownload";
                break;
        }
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }

    public void loadWebviewGraphic(int grafico){
        Map<String,String> params = new HashMap<>();
        String url = GRAFICO_URL;
        switch(grafico){
            case MENSALIDADES:
                url += "?mensalidades";
                break;
            case INADIMPLENTES:
                url += "?inadimplentes";
                break;
        }
        webview.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=https://seocursos.com.br/Ebooks/Livros/5a1cb86ebee8d.pdf");
    }
}
