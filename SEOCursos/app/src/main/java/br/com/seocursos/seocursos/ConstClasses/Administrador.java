package br.com.seocursos.seocursos.ConstClasses;

/**
 * Created by Aluno on 11/06/2018.
 */

public class Administrador extends Usuario {

    public Administrador(String id, String nome, String email, String foto, String sexo, String tipoUsuario,
                         String cpf, String cep, String endereco, Integer numero, String cidade, String estado){
        super(id, nome, email, foto, sexo, tipoUsuario, cpf, cep, endereco, numero, cidade, estado);
    }
    public Administrador(Usuario usuario){
        super(usuario);
    }
}
