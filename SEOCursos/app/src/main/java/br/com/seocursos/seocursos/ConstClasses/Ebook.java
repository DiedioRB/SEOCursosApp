package br.com.seocursos.seocursos.ConstClasses;

/**
 * Created by Aluno on 14/05/2018.
 */

public class Ebook {
    String id, titulo, autor, anoEdicao, editora, area, link;

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getAnoEdicao() {
        return anoEdicao;
    }

    public String getEditora() {
        return editora;
    }

    public String getArea() {
        return area;
    }

    public String getLink() {
        return link;
    }

    public Ebook(String id, String titulo, String autor, String anoEdicao, String editora, String area, String link){
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.anoEdicao = anoEdicao;
        this.editora = editora;
        this.area = area;
        this.link = link;
    }
}
