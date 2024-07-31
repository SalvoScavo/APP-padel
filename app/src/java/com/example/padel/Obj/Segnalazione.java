package com.example.padel.Obj;

public class Segnalazione {

    private String idSegnalato,idSegnalatore,motivo,idSegnalazione;

    public Segnalazione()
    {

    }

    public void aggiungiId(String id)
    {
        idSegnalazione=id;
    }

    public String prendiId(){return idSegnalazione;}

    public String getIdSegnalato() {
        return idSegnalato;
    }

    public void setIdSegnalato(String idSegnalato) {
        this.idSegnalato = idSegnalato;
    }

    public String getIdSegnalatore() {
        return idSegnalatore;
    }

    public void setIdSegnalatore(String idSegnalatore) {
        this.idSegnalatore = idSegnalatore;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
