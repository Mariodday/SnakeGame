package serpiente;

import java.util.Objects;

public class Punto {

    private final int columna;
    private final int fila;

    public Punto(int columna, int fila) {
        this.columna = columna;
        this.fila = fila;
    }

    // devuelve un nuevo Punto desplazado una celda en la direccion indicada
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

    // equals y hashCode son necesarios para que LinkedList.contains() compare por valor
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
