package serpiente;

public class EntradaRanking {

    private final String nombre;
    private final int puntuacion;

    public EntradaRanking(String nombre, int puntuacion) {
        this.nombre = nombre;
        this.puntuacion = puntuacion;
    }

    public String getNombre()  { return nombre; }
    public int getPuntuacion() { return puntuacion; }
}
