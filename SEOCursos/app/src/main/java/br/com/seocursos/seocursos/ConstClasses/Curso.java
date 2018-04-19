package br.com.seocursos.seocursos.ConstClasses;

/**
 * Created by LUIZ on 05/03/2018.
 */

public class Curso{
    private Integer cargaHoraria;
    private String id, nome, area, preRequisito, descricao, tipo;
    private Double preco;

    public String getId() {
        return id;
    }

    public Integer getCargaHoraria() {
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

    public Curso(String id, String nome, Double preco, String area, Integer cargaHoraria, String preRequisito, String descricao, String tipo){
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.area = area;
        this.cargaHoraria = cargaHoraria;
        this.preRequisito = preRequisito;
        this.descricao = descricao;
        this.tipo = tipo;
    }
}
