package br.com.seocursos.seocursos.ConstClasses;

/**
 * Created by Aluno on 18/05/2018.
 */

public class Chat {
    private String id,nome,mensagem;

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getMensagem() {
        return mensagem;
    }


    public Chat(String id, String nome, String mensagem){
        this.id = id;
        this.nome = nome;
        this.mensagem = mensagem;
    }
}
