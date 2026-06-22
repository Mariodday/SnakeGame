package serpiente;

import java.util.Objects;

// representa una celda del tablero con columna y fila
// usado por Serpiente (cuerpo) y ModeloJuego (manzana)
public class Punto {

    private final int columna;
    private final int fila;

    public Punto(int columna, int fila) {
        this.columna = columna;
        this.fila = fila;
    }

    // funcion: devuelve un nuevo Punto desplazado una celda en la direccion indicada
    public Punto trasladar(Direccion direccion) {
        switch (direccion) {
            case ARRIBA:    return new Punto(columna,     fila - 1);
            case ABAJO:     return new Punto(columna,     fila + 1);
            case IZQUIERDA: return new Punto(columna - 1, fila);
            case DERECHA:   return new Punto(columna + 1, fila);
            default:        return new Punto(columna,     fila);
        }
    }

    public int getColumna() { return columna; }
    public int getFila()    { return fila; }

    // equals y hashCode permiten usar LinkedList.contains() comparando por valor y no por referencia
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Punto)) return false;
        Punto p = (Punto) o;
        return columna == p.columna && fila == p.fila;
    }

    @Override
    public int hashCode() {
        return Objects.hash(columna, fila);
    }

    @Override
    public String toString() {
        return "(" + columna + ", " + fila + ")";
    }
}
