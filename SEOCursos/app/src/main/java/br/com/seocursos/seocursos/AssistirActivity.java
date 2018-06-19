package br.com.seocursos.seocursos;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import br.com.seocursos.seocursos.ConstClasses.VideoAula;
import br.com.seocursos.seocursos.Outros.CRUD;
import br.com.seocursos.seocursos.Outros.ProgressDialogHelper;

public class AssistirActivity extends AppCompatActivity {
    private static final String JSON_URL = "https://www.seocursos.com.br/PHP/Android/videoAulas.php";
    private VideoAula videoAula;

    WebView webView;
    VideoView videoView;
    MediaController controller;

    ProgressDialogHelper pd;
    SharedPreferencesHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistir);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        pd = new ProgressDialogHelper(AssistirActivity.this);
        helper = new SharedPreferencesHelper(AssistirActivity.this);

        try{
            Intent i = getIntent();
            videoAula = (VideoAula)i.getSerializableExtra("video");
        }catch(NullPointerException e){
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.videoAulaNaoEncontrada), Toast.LENGTH_SHORT).show();
            finish();
        }
        this.setTitle(videoAula.getTitulo());

        webView = findViewById(R.id.webView);
        videoView = findViewById(R.id.videoView);
        controller = new MediaController(AssistirActivity.this);

        controller.setMediaPlayer(videoView);
        videoView.setMediaController(controller);

        carregar();
    }

    public void carregar(){
        String tipo = videoAula.getTipoVideo();
        String link = videoAula.getLink();

        switch(tipo){
            case "A":
                videoView.setVideoPath("https://www.seocursos.com.br/Videos/VideoAulas/"+link);
                videoView.start();
                break;
            case "Y":
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/embed/"+link));
                startActivity(i);
                finish();
                break;
            case "V":
                i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vimeo.com/"+link));
                startActivity(i);
                finish();
                break;
        }
        Toast.makeText(this, videoAula.getTitulo(), Toast.LENGTH_SHORT).show();
    }
}
