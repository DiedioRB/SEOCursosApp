package br.com.seocursos.seocursos.ConstClasses;

/**
 * Created by LUIZ on 06/03/2018.
 */

public class Disciplina {
    private String id,nome,nivel,cargaHoraria,area,duracao,curso,modalidade,idCurso, idTutor;

    public String getId() {
        return id;
    }

    public String getDuracao() {
        return duracao;
    }

    public String getModalidade() {
        return modalidade;
    }

    public String getIdCurso() {
        return idCurso;
    }

    public String getNome() {
        return nome;
    }

    public String getNivel() {
        return nivel;
    }

    public String getCargaHoraria() {
        return cargaHoraria;
    }

    public String getArea() {
        return area;
    }

    public String getCurso() {
        return curso;
    }

    public String getIdTutor(){
        return idTutor;
    }

    public Disciplina(String id, String nome, String nivel, String cargaHoraria, String area, String duracao, String modalidade, String idCurso, String curso, String idTutor){
        this.id = id;
        this.nome= nome;
        this.nivel = nivel;
        this.cargaHoraria = cargaHoraria;
        this.area = area;
        this.duracao = duracao;
        this.modalidade = modalidade;
        this.idCurso = idCurso;
        this.curso = curso;
        this.idTutor = idTutor;
    }
}
