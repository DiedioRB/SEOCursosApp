package br.com.seocursos.seocursos.ConstClasses;

/**
 * Created by Aluno on 11/06/2018.
 */

public class CursoTecnico extends Curso {
    private String modalidade, duracao;

    public String getModalidade() {
        return modalidade;
    }

    public String getDuracao() {
        return duracao;
    }

    public CursoTecnico(String id, String nome, Double preco, String area, String cargaHoraria,
                        String preRequisito, String descricao, String tipo, String modalidade, String duracao){
        super(id, nome, preco, area, cargaHoraria, preRequisito, descricao, tipo);
        this.modalidade = modalidade;
        this.duracao = duracao;
    }
    public CursoTecnico(Curso curso, String modalidade, String duracao){
        super(curso);
        this.modalidade = modalidade;
        this.duracao = duracao;
    }
}