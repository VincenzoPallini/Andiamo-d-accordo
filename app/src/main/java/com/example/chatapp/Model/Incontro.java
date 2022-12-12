package com.example.chatapp.Model;

import com.google.android.gms.maps.model.LatLng;

public class Incontro {

    private String Incontroid;
    private String cittaorg;
    private String addressorganizz;
    private String date;
    private String orario;
    private int partecipanti;
    private String summary;
    private LatLng latlng;



    public Incontro(String Incontroid, String cittaorg, String addressorganizz, int partecipanti, String date,String orario, String summary, LatLng LatLng) {
        this.Incontroid = Incontroid;
        this.cittaorg = cittaorg;
        this.addressorganizz = addressorganizz;
        this.date = date;
        this.orario = orario;
        this.partecipanti = partecipanti;
        this.summary = summary;
        this.latlng = latlng;



    }

    public Incontro() {}

    public String getIncontroidId() {
        return Incontroid;
    }

    public void setIncontroidId(String Incontroid) {
        this.Incontroid = Incontroid;
    }

    public String getCittaorg() {
        return cittaorg;
    }

    public void setCittaorg(String cittaorg) {
        this.cittaorg = cittaorg;
    }

    public String getAddressorganizz() {
        return addressorganizz;
    }

    public void setAddressorganizz(String addressorganizz) {this.addressorganizz = addressorganizz;}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrario() {
        return orario;
    }

    public void setOrario(String orario) {
        this.orario = orario;
    }

    public int getPartecipanti() {
        return partecipanti;
    }

    public void setPartecipanti(int partecipanti) {
        this.partecipanti = partecipanti;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LatLng getLatlng () {return latlng;}

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }




}
