package br.com.seocursos.seocursos.ConstClasses;

/**
 * Created by Aluno on 28/03/2018.
 */

public class Enquete {
    private String id,pergunta,valorA,valorB,valorC,valorD,valorE;

    public String getId() {
        return id;
    }

    public String getPergunta() {
        return pergunta;
    }

    public String getValorA() {
        return valorA;
    }

    public String getValorB() {
        return valorB;
    }

    public String getValorC() {
        return valorC;
    }

    public String getValorD() {
        return valorD;
    }

    public String getValorE() {
        return valorE;
    }

    public Enquete(String id, String pergunta, String valorA, String valorB, String valorC, String valorD, String valorE){
        this.id = id;
        this.pergunta = pergunta;
        this.valorA = valorA;
        this.valorB = valorB;
        this.valorC = valorC;
        this.valorD = valorD;
        this.valorE = valorE;

    }
}
