package pe.bigprime;

import utils.Fecha;

public class BigSignerHashRequest {
    private String nombre;
    private String path;
    private String visI;
    private int visP;
    private String texto;
    private float visX;
    private float visY;
    private float visH;
    private float visW;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVisI() {
        return visI;
    }

    public void setVisI(String visI) {
        this.visI = visI;
    }

    public int getVisP() {
        return visP;
    }

    public void setVisP(int visP) {
        this.visP = visP;
    }

    public String getTexto() {
        return texto = texto != null ? texto.replace("<DATE>", Fecha.FechaDB() + " "+ Fecha.darHora())
                .replace("<br>",System.lineSeparator()) : "";
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public float getVisX() {
        return visX;
    }

    public void setVisX(float visX) {
        this.visX = visX;
    }

    public float getVisY() {
        return visY;
    }

    public void setVisY(float visY) {
        this.visY = visY;
    }

    public float getVisH() {
        return visH;
    }

    public void setVisH(float visH) {
        this.visH = visH;
    }

    public float getVisW() {
        return visW;
    }

    public void setVisW(float visW) {
        this.visW = visW;
    }
}