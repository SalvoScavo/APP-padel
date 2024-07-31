package com.example.padel.Obj;

public class Campo {

    private String nome;
    private String tipologia;

    private String urlFoto;

    private Boolean lezioni;

    private String id;


    public Campo(String nome, String tipologia, String urlFoto,Boolean lezioni) {
        this.nome = nome;
        this.tipologia = tipologia;
        this.urlFoto = urlFoto;
        this.id = "";
        this.lezioni = lezioni;
    }

    public Boolean getLezioni() {
        return lezioni;
    }

    public void setLezioni(Boolean lezioni) {
        this.lezioni = lezioni;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public Campo(){

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipologia() {
        return tipologia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }
}
