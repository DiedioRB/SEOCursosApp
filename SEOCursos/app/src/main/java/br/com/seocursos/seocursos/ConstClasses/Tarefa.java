package br.com.seocursos.seocursos.ConstClasses;

import java.sql.Date;

/**
 * Created by LUIZ on 06/03/2018.
 */

public class Tarefa {
    private Integer idDisciplina,idTutor;
    private String id,descricao,disciplina,tutor;
    private String dataEnvio;

    public String getId() {
        return id;
    }

    public Integer getIdDisciplina() {
        return idDisciplina;
    }

    public Integer getIdTutor() {
        return idTutor;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public String getTutor() {
        return tutor;
    }

    public String getDataEnvio() {
        return dataEnvio;
    }

    public Tarefa(String id, String descricao, String dataEnvio, Integer idDisciplina, Integer idTutor, String disciplina, String tutor){
        this.id = id;
        this.descricao = descricao;
        this.dataEnvio = dataEnvio;
        this.idDisciplina = idDisciplina;
        this.idTutor = idTutor;
        this.disciplina = disciplina;
        this.tutor = tutor;
    }
}
