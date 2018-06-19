package br.com.seocursos.seocursos.ConstClasses;

/**
 * Created by LUIZ on 05/03/2018.
 */

public class Curso{
    private String id, nome, area, preRequisito, descricao, tipo, cargaHoraria;
    private Double preco;

    public String getId() {
        return id;
    }

    public String getCargaHoraria() {
        return cargaHoraria;
    }

    public String getNome() {
        return nome;
    }

    public String getArea() {
        return area;
    }

    public String getPreRequisito() {
        return preRequisito;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public Double getPreco() {
        return preco;
    }

    public Curso(String id, String nome, Double preco, String area, String cargaHoraria, String preRequisito, String descricao, String tipo){
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.area = area;
        this.cargaHoraria = cargaHoraria;
        this.preRequisito = preRequisito;
        this.descricao = descricao;
        this.tipo = tipo;
    }
    public Curso(Curso curso){
        this.id = curso.getId();
        this.nome = curso.getNome();
        this.preco = curso.getPreco();
        this.area = curso.getArea();
        this.cargaHoraria = curso.getCargaHoraria();
        this.preRequisito = curso.getPreRequisito();
        this.descricao = curso.getDescricao();
        this.tipo = curso.getTipo();
    }
}
