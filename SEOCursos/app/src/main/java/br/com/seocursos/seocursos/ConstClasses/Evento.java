package br.com.seocursos.seocursos.ConstClasses;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Aluno on 28/03/2018.
 */

public class Evento {
    private String id,nome,lugar,telefone,valor,formaPagamento;
    private String dia;

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getLugar() {
        return lugar;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getValor() {
        return valor;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public String getDia() {
        return dia;
    }

    public Evento(String id, String nome, String lugar, String dia, String telefone, String valor, String formaPagamento){
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date data = formato.parse(dia,pos);
        formato = new SimpleDateFormat("dd/MM/yyyy");
        String date = formato.format(data);

        this.id = id;
        this.nome = nome;
        this.lugar = lugar;
        this.dia = date;
        this.telefone = telefone;
        this.valor = valor;
        this.formaPagamento = formaPagamento;

    }
}