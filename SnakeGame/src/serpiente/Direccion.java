package serpiente;

public enum Direccion {
    ARRIBA, ABAJO, IZQUIERDA, DERECHA;

    public boolean esOpuesta(Direccion otra) {
        return (this == ARRIBA && otra == ABAJO)
            || (this == ABAJO && otra == ARRIBA)
            || (this == IZQUIERDA && otra == DERECHA)
            || (this == DERECHA && otra == IZQUIERDA);
    }

    public Direccion opuesta() {
        switch (this) {
            case ARRIBA:    return ABAJO;
            case ABAJO:     return ARRIBA;
            case IZQUIERDA: return DERECHA;
            case DERECHA:   return IZQUIERDA;
            default:        return this;
        }
    }
}
