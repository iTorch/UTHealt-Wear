package com.jp.wear.phone.mywear.Body;

public class VitalSignsBody {
    private float oxigeno;
    private float temperatura;
    private float calorias_quemadas;
    private int pasos_diario;
    private float distancia_recorrida;
    private float ritmo_cardiaco;
    private int id_persona;

    public float getRitmo_cardiaco() {
        return ritmo_cardiaco;
    }

    public void setRitmo_cardiaco(float ritmo_cardiaco) {
        this.ritmo_cardiaco = ritmo_cardiaco;
    }



    public float getOxigeno() {
        return oxigeno;
    }

    public void setOxigeno(float oxigeno) {
        this.oxigeno = oxigeno;
    }

    public float getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(float temperatura) {
        this.temperatura = temperatura;
    }

    public float getCalorias_quemadas() {
        return calorias_quemadas;
    }

    public void setCalorias_quemadas(float calorias_quemadas) {
        this.calorias_quemadas = calorias_quemadas;
    }

    public float getPasos_diario() {
        return pasos_diario;
    }

    public void setPasos_diario(int peso_diario) {
        this.pasos_diario = peso_diario;
    }

    public float getDistancia_recorrida() {
        return distancia_recorrida;
    }

    public void setDistancia_recorrida(float distancia_recorrida) {
        this.distancia_recorrida = distancia_recorrida;
    }

    public int getId_persona() {
        return id_persona;
    }

    public void setId_persona(int id_persona) {
        this.id_persona = id_persona;
    }
}
