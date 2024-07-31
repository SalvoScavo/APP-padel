package com.example.padel.Obj;

import java.util.ArrayList;
import java.util.Map;

public class Partita {

    private String idPartita;

    private String giorno;
    private String idCampo;
    private String idCreatore;

    private String oraInizio;

    private String id1S1,id1S2,id2S2;

    private int punteggio1,punteggio2;


    public Partita() {

    }

    public Partita(String giorno, String idCampo, String idCreatore, String oraInizio, String id1S1, String id1S2, String id2S2) {
        this.giorno = giorno;
        this.idCampo = idCampo;
        this.idCreatore = idCreatore;
        this.oraInizio = oraInizio;
        this.id1S1 = id1S1;
        this.id1S2 = id1S2;
        this.id2S2 = id2S2;
        this.punteggio1 = 0;
        this.punteggio2 = 0;
    }

    public String IdPartita() {
        return idPartita;
    }

    public void addIdPartita(String idPartita)
    {
        this.idPartita = idPartita;
    }

    public int getPunteggio1() {
        return punteggio1;
    }

    public void setPunteggio1(int punteggio1) {
        this.punteggio1 = punteggio1;
    }

    public int getPunteggio2() {
        return punteggio2;
    }

    public void setPunteggio2(int punteggio2) {
        this.punteggio2 = punteggio2;
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

    public String getIdCreatore() {
        return idCreatore;
    }

    public void setIdCreatore(String idCreatore) {
        this.idCreatore = idCreatore;
    }

    public String getOraInizio() {
        return oraInizio;
    }

    public void setOraInizio(String oraInizio) {
        this.oraInizio = oraInizio;
    }

    public ArrayList<String> prendiInviti() {
        ArrayList<String> inviti = new ArrayList<>();
        if(id1S1 != "VUOTO")
            inviti.add(id1S1);
        if(id1S2 != "VUOTO")
            inviti.add(id1S2);
        if(id2S2 != "VUOTO")
            inviti.add(id2S2);

        return inviti;
    }

    public String getId1S1() {
        return id1S1;
    }

    public void setId1S1(String id1S1) {
        this.id1S1 = id1S1;
    }

    public String getId1S2() {
        return id1S2;
    }

    public void setId1S2(String id1S2) {
        this.id1S2 = id1S2;
    }

    public String getId2S2() {
        return id2S2;
    }

    public void setId2S2(String id2S2) {
        this.id2S2 = id2S2;
    }

    @Override
    public String toString() {
        String str = "Partita{" +
                "giorno='" + giorno + '\'' +
                ", idCampo='" + idCampo + '\'' +
                ", idCreatore='" + idCreatore + '\'' +
                ", oraInizio='" + oraInizio + '\'' + ", inviti=";


        for (String id : prendiInviti()) {
            str += id + " - ";
        }

        str += '}';
        return str;
    }
}
