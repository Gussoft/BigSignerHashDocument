package utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Fecha {
    public static Calendar calendar = Calendar.getInstance();
    private static String fecha;

    public static String darHora() {
        String hora = null;
        try {
            Date hoy = new Date();
            SimpleDateFormat df = new SimpleDateFormat("H:mm:ss");
            hora = df.format(hoy);
            System.out.println("La hora es " + hora);
        } catch (Exception e) {
            System.out.println("No se pudo mostrar la hora");
        }
        return hora;
    }

    public static String FechaDB() {
        SimpleDateFormat fec = new SimpleDateFormat("yyyy-MM-dd");
        fecha = fec.format(calendar.getTime());
        return fecha;
    }

    public static String DateandHour(){
        return FechaDB() + " " + darHora();
    }
}
