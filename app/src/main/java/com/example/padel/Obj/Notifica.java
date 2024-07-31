package com.example.padel.Obj;

public class Notifica {

    private String idDestinatario,messaggio;

    public Notifica(){}

    public Notifica(String idDestinatario, String messaggio) {
        this.idDestinatario = idDestinatario;
        this.messaggio = messaggio;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }
}
