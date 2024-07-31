package com.example.padel.Obj;

public class Lezione
{

    private String giorno,idCampo,idMaestro,oraInizio,utente;

    public Lezione(){
    }


    public Lezione(String giorno, String idCampo, String idMaestro, String oraInizio, String utente) {
        this.giorno = giorno;
        this.idCampo = idCampo;
        this.idMaestro = idMaestro;
        this.oraInizio = oraInizio;
        this.utente = utente;
    }

    public String getGiorno() {
        return giorno;
    }

    public void setGiorno(String giorno) {
        this.giorno = giorno;
    }

    public String getIdCampo() {
        return idCampo;
    }

    public void setIdCampo(String idCampo) {
        this.idCampo = idCampo;
    }

    public String getIdMaestro() {
        return idMaestro;
    }

    public void setIdMaestro(String idMaestro) {
        this.idMaestro = idMaestro;
    }

    public String getOraInizio() {
        return oraInizio;
    }

    public void setOraInizio(String oraInizio) {
        this.oraInizio = oraInizio;
    }

    public String getUtente() {
        return utente;
    }

    public void setUtente(String utente) {
        this.utente = utente;
    }
}
