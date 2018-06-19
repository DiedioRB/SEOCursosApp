package br.com.seocursos.seocursos.ConstClasses;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Aluno on 11/06/2018.
 */

public class Aluno extends Usuario {
    String telefone, dataInicio;

    public String getTelefone() {
        return telefone;
    }

    public String getDataInicio() {
        return dataInicio;
    }

    public Aluno(String id, String nome, String email, String foto, String sexo, String tipoUsuario,
                 String cpf, String cep, String endereco, Integer numero, String cidade, String estado,
                 String telefone, String dataInicio){
        super(id, nome, email, foto, sexo, tipoUsuario, cpf, cep, endereco, numero, cidade, estado);

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date data = formato.parse(dataInicio,pos);
        formato = new SimpleDateFormat("dd/MM/yyyy");
        String date = formato.format(data);

        this.telefone = telefone;
        this.dataInicio = date;
    }
    public Aluno(Usuario usuario, String telefone, String dataInicio){
        super(usuario);

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date data = formato.parse(dataInicio,pos);
        formato = new SimpleDateFormat("dd/MM/yyyy");
        String date = formato.format(data);

        this.telefone = telefone;
        this.dataInicio = date;
    }
}
