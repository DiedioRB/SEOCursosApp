package br.com.seocursos.seocursos.ConstClasses;

import java.io.Serializable;

/**
 * Created by Aluno on 30/05/2018.
 */

public class VideoAula implements Serializable{
    private String id,titulo,link,tipoVideo,idDisciplina,disciplina;

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getLink() {
        return link;
    }

    public String getTipoVideo() {
        return tipoVideo;
    }

    public String getIdDisciplina() {
        return idDisciplina;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public VideoAula(String id, String titulo, String link, String tipoVideo, String idDisciplina, String disciplina){
        this.id = id;
        this.titulo = titulo;
        this.link = link;
        this.tipoVideo = tipoVideo;
        this.idDisciplina = idDisciplina;
        this.disciplina = disciplina;
    }
}
