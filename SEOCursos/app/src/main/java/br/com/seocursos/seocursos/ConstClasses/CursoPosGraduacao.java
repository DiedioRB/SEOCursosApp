package br.com.seocursos.seocursos.ConstClasses;

/**
 * Created by Aluno on 11/06/2018.
 */

public class CursoPosGraduacao extends Curso {
    private String modalidade, status, duracao, notaMec;

    public String getModalidade() {
        return modalidade;
    }

    public String getStatus() {
        return status;
    }

    public String getDuracao() {
        return duracao;
    }

    public String getNotaMec() {
        return notaMec;
    }

    public CursoPosGraduacao(String id, String nome, Double preco, String area, String cargaHoraria,
                          String preRequisito, String descricao, String tipo, String modalidade, String status,
                          String duracao, String notaMec){
        super(id, nome, preco, area, cargaHoraria, preRequisito, descricao, tipo);
        this.modalidade = modalidade;
        this.status = status;
        this.duracao = duracao;
        this.notaMec = notaMec;
    }
    public CursoPosGraduacao(Curso curso, String modalidade, String status, String duracao, String notaMec){
        super(curso);
        this.modalidade = modalidade;
        this.status = status;
        this.duracao = duracao;
        this.notaMec = notaMec;
    }
}
