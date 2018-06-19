package br.com.seocursos.seocursos.ConstClasses;

/**
 * Created by Aluno on 11/06/2018.
 */

public class Tutor extends Usuario {
    String nascimento, telResidencial, telCelular, nomeCurso, nomeInstituicao, anoConclusao;

    public String getNascimento() {
        return nascimento;
    }

    public String getTelResidencial() {
        return telResidencial;
    }

    public String getTelCelular() {
        return telCelular;
    }

    public String getNomeCurso() {
        return nomeCurso;
    }

    public String getNomeInstituicao() {
        return nomeInstituicao;
    }

    public String getAnoConclusao() {
        return anoConclusao;
    }

    public Tutor(String id, String nome, String email, String foto, String sexo, String tipoUsuario,
                 String cpf, String cep, String endereco, Integer numero, String cidade, String estado,
                 String nascimento, String telResidencial, String telCelular, String nomeCurso, String nomeInstituicao,
                 String anoConclusao){
        super(id, nome, email, foto, sexo, tipoUsuario, cpf, cep, endereco, numero, cidade, estado);
        this.nascimento = nascimento;
        this.telResidencial = telResidencial;
        this.telCelular = telCelular;
        this.nomeCurso = nomeCurso;
        this.nomeInstituicao = nomeInstituicao;
        this.anoConclusao = anoConclusao;
    }
    public Tutor(Usuario usuario, String nascimento, String telResidencial, String telCelular,
                 String nomeCurso, String nomeInstituicao, String anoConclusao){
        super(usuario);
        this.nascimento = nascimento;
        this.telResidencial = telResidencial;
        this.telCelular = telCelular;
        this.nomeCurso = nomeCurso;
        this.nomeInstituicao = nomeInstituicao;
        this.anoConclusao = anoConclusao;
    }
}
