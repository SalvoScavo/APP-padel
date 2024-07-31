package com.example.padel.Obj;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class User
{
    private String userID;
    private String nome;

    private String cognome;

    private String pic;
    private String bio;
    private double reputazione;
    private int ranking;

    private String stato;

    private boolean admin;

    private int nValutazioni;


    public User(){
        //default costruttore
    }
    public User(String userID, String nome, String cognome){
        this.userID = userID;
        this.nome = nome;
        this.cognome = cognome;
        this.pic = "";
        this.bio = "";
        this.reputazione =0;
        this.ranking = 0;
        this.admin = false;
        this.stato = "attivo";
        this.nValutazioni = 0;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public int getnValutazioni() {
        return nValutazioni;
    }

    public void setnValutazioni(int nValutazioni) {
        this.nValutazioni = nValutazioni;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }


    public boolean isAdmin() {
        return admin;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String immagineProfilo) {
        this.pic = immagineProfilo;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public double getReputazione() {
        return reputazione;
    }

    public void setReputazione(double reputazione) {
        this.reputazione = reputazione;
    }

    public int getRanking() {
        return ranking;
    }

    public void calcolaReputazione(double val)
    {
        this.reputazione = ((this.reputazione*this.nValutazioni) + val)/(this.nValutazioni+1);
        BigDecimal bd = new BigDecimal(Double.toString(this.reputazione));
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        this.reputazione = bd.doubleValue();

        this.nValutazioni++;
    }
    public String rankToString()
    {
        if(ranking <=200) return "LOW";
        if(ranking <=500) return "MEDIUM";
        return "HIGH";
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }
}
