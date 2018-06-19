package br.com.seocursos.seocursos.ConstClasses;

import java.sql.Date;

/**
 * Created by LUIZ on 05/03/2018.
 */

public class Usuario {
    private Integer numero;
    private String id,nome,email,foto,cpf,cep,endereco,cidade,estado,sexo,tipoUsuario;

    public String getId() {
        return id;
    }

    public Integer getNumero() {
        return numero;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getFoto() {
        return foto;
    }

    public String getCpf() {
        return cpf;
    }

    public String getCep() {
        return cep;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public String getSexo() {
        return sexo;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public Usuario(String id, String nome, String email, String foto, String sexo, String tipoUsuario,
                   String cpf, String cep, String endereco, Integer numero, String cidade, String estado){
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.foto = foto;
        this.sexo = sexo;
        this.tipoUsuario = tipoUsuario;
        this.cpf = cpf;
        this.cep = cep;
        this.endereco = endereco;
        this.numero = numero;
        this.cidade = cidade;
        this.estado = estado;
    }
    public Usuario(Usuario usuario){
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.foto = usuario.getFoto();
        this.sexo = usuario.getSexo();
        this.tipoUsuario = usuario.getTipoUsuario();
        this.cpf = usuario.getCpf();
        this.cep = usuario.getCep();
        this.endereco = usuario.getEndereco();
        this.numero = usuario.getNumero();
        this.cidade = usuario.getCidade();
        this.estado = usuario.getEstado();
    }
}

