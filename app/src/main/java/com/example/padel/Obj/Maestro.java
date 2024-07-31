package com.example.padel.Obj;

import java.util.ArrayList;
import java.util.Map;

public class Maestro {

    private  String nome,cognome,id;
    private Map<String,Boolean> giorniLavoro;

    public Maestro(){}



    public Maestro(String nome, String cognome, Map<String, Boolean> giorniLavoro) {
        this.nome = nome;
        this.cognome = cognome;
        this.giorniLavoro = giorniLavoro;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public Map<String, Boolean> getGiorniLavoro() {
        return giorniLavoro;
    }

    public void setGiorniLavoro(Map<String, Boolean> giorniLavoro) {
        this.giorniLavoro = giorniLavoro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String idMaestro()
    {
        return this.id;
    }

    public void addId(String id)
    {
        this.id = id;
    }

    public ArrayList<String> giorniLavorativi()
    {
        ArrayList<String> giorni = new ArrayList<>();
        for (String giorno : giorniLavoro.keySet())
        {
            if(giorniLavoro.get(giorno) == true)
                giorni.add(giorno);
        }

        return giorni;
    }
}
