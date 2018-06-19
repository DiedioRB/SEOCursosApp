package br.com.seocursos.seocursos.ConstClasses;

/**
 * Created by Aluno on 11/06/2018.
 */

public class CursoGraduacao extends Curso {
    private String modalidade, titulacao, duracao, notaMec;

    public String getModalidade() {
        return modalidade;
    }

    public String getTitulacao() {
        return titulacao;
    }

    public String getDuracao() {
        return duracao;
    }

    public String getNotaMec() {
        return notaMec;
    }

    public CursoGraduacao(String id, String nome, Double preco, String area, String cargaHoraria,
                          String preRequisito, String descricao, String tipo, String modalidade, String titulacao,
                          String duracao, String notaMec){
        super(id, nome, preco, area, cargaHoraria, preRequisito, descricao, tipo);
        this.modalidade = modalidade;
        this.titulacao = titulacao;
        this.duracao = duracao;
        this.notaMec = notaMec;
    }
    public CursoGraduacao(Curso curso, String modalidade, String titulacao, String duracao, String notaMec){
        super(curso);
        this.modalidade = modalidade;
        this.titulacao = titulacao;
        this.duracao = duracao;
        this.notaMec = notaMec;
    }
}
