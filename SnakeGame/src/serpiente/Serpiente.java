package serpiente;

import java.util.LinkedList;

public class Serpiente {

    // arreglo dinamico que representa los segmentos del cuerpo; cabeza en indice 0
    private final LinkedList<Punto> cuerpo = new LinkedList<>();
    private Direccion direccionActual;
    private Direccion direccionSiguiente; // se aplica en el proximo mover() para evitar giros instantaneos

    public Serpiente(int columna, int fila, Direccion direccion) {
        cuerpo.add(new Punto(columna, fila));

        Direccion atras = direccion.opuesta();
        Punto actual = cuerpo.getFirst();
        for (int i = 0; i < 2; i++) {
            actual = actual.trasladar(atras);
            cuerpo.add(actual);
        }

        direccionActual = direccion;
        direccionSiguiente = direccion;
    }

    public void mover(boolean crecer) {
        direccionActual = direccionSiguiente;
        cuerpo.addFirst(cuerpo.getFirst().trasladar(direccionActual));
        if (!crecer) cuerpo.removeLast();
    }

    // rechaza la direccion opuesta para que no pueda girar 180 grados
    public void establecerDireccion(Direccion direccion) {
        if (!direccionActual.esOpuesta(direccion)) {
            direccionSiguiente = direccion;
        }
    }

    public boolean chocaConsigoMisma() {
        Punto cabeza = obtenerCabeza();
        for (int i = 1; i < cuerpo.size(); i++) {
            if (cabeza.equals(cuerpo.get(i))) return true;
        }
        return false;
    }

    public Punto obtenerCabeza()                 { return cuerpo.getFirst(); }
    public int obtenerLongitud()                 { return cuerpo.size(); }
    public LinkedList<Punto> obtenerCuerpo()     { return cuerpo; }
    public Direccion obtenerDireccionActual()    { return direccionActual; }
    public Direccion obtenerDireccionSiguiente() { return direccionSiguiente; }
}
