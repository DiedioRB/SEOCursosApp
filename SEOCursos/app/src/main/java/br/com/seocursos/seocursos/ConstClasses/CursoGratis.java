package br.com.seocursos.seocursos.ConstClasses;

/**
 * Created by Aluno on 11/06/2018.
 */

public class CursoGratis extends Curso {
    private String disponivelAte, nivel;

    public String getDisponivelAte() {
        return disponivelAte;
    }

    public String getNivel() {
        return nivel;
    }

    public CursoGratis(String id, String nome, Double preco, String area, String cargaHoraria,
                       String preRequisito, String descricao, String tipo, String disponivelAte, String nivel){
        super(id, nome, preco, area, cargaHoraria, preRequisito, descricao, tipo);
        this.disponivelAte = disponivelAte;
        this.nivel = nivel;
    }
    public CursoGratis(Curso curso, String disponivelAte, String nivel){
        super(curso);
        this.disponivelAte = disponivelAte;
        this.nivel = nivel;
    }

}
