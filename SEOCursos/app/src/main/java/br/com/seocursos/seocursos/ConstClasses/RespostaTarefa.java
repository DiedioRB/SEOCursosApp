package br.com.seocursos.seocursos.ConstClasses;

/**
 * Created by Aluno on 16/05/2018.
 */

public class RespostaTarefa {
    private String id, resposta, idUsuario, aluno;

    public String getId() {
        return id;
    }

    public String getResposta() {
        return resposta;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getAluno() {
        return aluno;
    }

    public RespostaTarefa(String id, String resposta, String idUsuario, String aluno){
        this.id = id;
        this.resposta = resposta;
        this.idUsuario = idUsuario;
        this.aluno = aluno;
    }
}
