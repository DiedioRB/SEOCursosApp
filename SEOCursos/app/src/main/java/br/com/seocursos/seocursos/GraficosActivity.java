package br.com.seocursos.seocursos;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.RadioGroup;
import android.widget.Toast;

import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;
public class GraficosActivity extends AppCompatActivity {
    private static final String RELATORIO_URL = "https://www.seocursos.com.br/Administrador/relatorio.php";
    private static final String GRAFICO_URL = "https://www.seocursos.com.br/PHP/Android/graficos.php";
    private static final int MENSALIDADES = 1;
    private static final int INADIMPLENTES = 2;
    private int lastRequire;

    private static final int ASK_READ_PERMISSION = 4;

    RadioGroup grafico;
    WebView webview;

    ProgressDialogHelper pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficos);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
                lastRequire = MENSALIDADES;
                if(checkPermissions()){
                    downloadRelatorio(MENSALIDADES);
                }
                break;
            case R.id.inadimplentes:
                lastRequire = INADIMPLENTES;
                if(checkPermissions()) {
                    downloadRelatorio(INADIMPLENTES);
                }
                break;
            default:
                break;
        }

        return true;
    }

    public boolean checkPermissions(){
        int readPermission = ContextCompat.checkSelfPermission(GraficosActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(readPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else{
            ActivityCompat.requestPermissions(GraficosActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ASK_READ_PERMISSION);
        }
        return false;
    }

    public void downloadRelatorio(int relatorio){
        String url = RELATORIO_URL;
        String nomeRelatorio = "";
        switch(relatorio) {
            case MENSALIDADES:
                url += "?mensalidades=&androidDownload=";
                nomeRelatorio = getResources().getString(R.string.relatorioDeMensalidades);
                break;
            case INADIMPLENTES:
                url += "?inadimplentes=&androidDownload=";
                nomeRelatorio = getResources().getString(R.string.relatorioDeInadimplentes);
                break;
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(nomeRelatorio);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, nomeRelatorio);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            manager.enqueue(request);
            Toast.makeText(this, getResources().getString(R.string.fazendoDownloadDe)+" "+nomeRelatorio, Toast.LENGTH_SHORT).show();
        }

    }

    public void loadWebviewGraphic(int grafico){
        String url = GRAFICO_URL;
        switch(grafico){
            case MENSALIDADES:
                url += "?mensalidades";
                break;
            case INADIMPLENTES:
                url += "?inadimplentes";
                break;
        }
        webview.loadUrl(url);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch(requestCode){
            case ASK_READ_PERMISSION:
                if(!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                    Toast.makeText(this, getResources().getString(R.string.eNecessarioGarantirPermissao), Toast.LENGTH_SHORT).show();
                }else{
                    downloadRelatorio(lastRequire);
                }
                break;
        }
    }
}
