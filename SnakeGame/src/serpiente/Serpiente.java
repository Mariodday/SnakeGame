package serpiente;

import java.util.LinkedList;

// representa una serpiente; usada por ModeloJuego (jugador1, jugador2, ia)
public class Serpiente {

    // arreglo dinamico LinkedList: permite agregar al frente y quitar al final en O(1)
    // cada elemento es un Punto que representa una celda del cuerpo; indice 0 = cabeza
    private final LinkedList<Punto> cuerpo = new LinkedList<>();

    private Direccion direccionActual;
    private Direccion direccionSiguiente; // se aplica en el proximo mover() para evitar giros instantaneos

    public Serpiente(int columna, int fila, Direccion direccion) {
        cuerpo.add(new Punto(columna, fila));

        Direccion atras = direccion.opuesta();
        Punto actual = cuerpo.getFirst();
        // funcion: rellena el cuerpo inicial con 3 segmentos en linea
        for (int i = 0; i < 2; i++) {
            actual = actual.trasladar(atras);
            cuerpo.add(actual);
        }

        direccionActual = direccion;
        direccionSiguiente = direccion;
    }

    // funcion: avanza la serpiente un paso; si crecer=true no quita la cola
    public void mover(boolean crecer) {
        direccionActual = direccionSiguiente;
        cuerpo.addFirst(cuerpo.getFirst().trasladar(direccionActual));
        if (!crecer) cuerpo.removeLast();
    }

    // funcion: cambia la direccion siguiente; rechaza giros de 180 grados
    public void establecerDireccion(Direccion direccion) {
        if (!direccionActual.esOpuesta(direccion)) {
            direccionSiguiente = direccion;
        }
    }

    // funcion: recorre el arreglo dinamico buscando si la cabeza coincide con algun segmento del cuerpo
    public boolean chocaConsigoMisma() {
        Punto cabeza = obtenerCabeza();
        for (int i = 1; i < cuerpo.size(); i++) {
            if (cabeza.equals(cuerpo.get(i))) return true;
        }
        return false;
    }

    public Punto obtenerCabeza()                 { return cuerpo.getFirst(); }
    public int obtenerLongitud()                 { return cuerpo.size(); }
    // devuelve el arreglo dinamico completo; lo usa LienzoJuego para dibujar cada segmento
    public LinkedList<Punto> obtenerCuerpo()     { return cuerpo; }
    public Direccion obtenerDireccionActual()    { return direccionActual; }
    public Direccion obtenerDireccionSiguiente() { return direccionSiguiente; }
}
