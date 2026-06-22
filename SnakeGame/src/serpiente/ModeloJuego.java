package serpiente;

import java.util.Random;

// logica del juego; se conecta con Serpiente, Punto y Direccion
// EscenaJuego llama avanzar() cada 150ms y lee el estado con los getters
public class ModeloJuego {

    public enum Modo { NORMAL, VS_IA, DOS_JUGADORES }

    public static final int COLUMNAS = 25;
    public static final int FILAS = 20;

    private final Modo modo;
    private final Random rnd = new Random();

    // cada Serpiente contiene internamente un arreglo dinamico LinkedList<Punto>
    private Serpiente jugador1;
    private Serpiente jugador2;
    private Serpiente ia;

    private Punto manzana; // posicion actual de la manzana en el tablero
    private int puntosJ1;
    private int puntosJ2;
    private boolean terminado;
    private boolean comioManzana;
    private int ganador; // 0=empate, 1=J1, 2=J2

    public ModeloJuego(Modo modo) {
        this.modo = modo;
        reiniciar();
    }

    public ModeloJuego() {
        this(Modo.NORMAL);
    }

    // funcion: reinicia todas las variables al estado inicial
    public void reiniciar() {
        jugador1 = new Serpiente(6, FILAS / 2, Direccion.DERECHA);
        jugador2 = modo == Modo.DOS_JUGADORES ? new Serpiente(COLUMNAS - 7, FILAS / 2, Direccion.IZQUIERDA) : null;
        ia       = modo == Modo.VS_IA         ? new Serpiente(COLUMNAS - 7, FILAS / 2, Direccion.IZQUIERDA) : null;
        puntosJ1 = 0;
        puntosJ2 = 0;
        ganador = 0;
        terminado = false;
        comioManzana = false;
        generarManzana();
    }

    // funcion: tick principal llamado cada 150ms desde EscenaJuego
    public void avanzar() {
        if (terminado) return;

        if (modo == Modo.VS_IA) moverIA_decision();

        Punto sig1 = jugador1.obtenerCabeza().trasladar(jugador1.obtenerDireccionSiguiente());
        comioManzana = sig1.equals(manzana);
        jugador1.mover(comioManzana);
        if (comioManzana) {
            puntosJ1++;
            generarManzana();
        }

        if (modo == Modo.DOS_JUGADORES && jugador2 != null) {
            Punto sig2 = jugador2.obtenerCabeza().trasladar(jugador2.obtenerDireccionSiguiente());
            boolean j2Comio = sig2.equals(manzana);
            jugador2.mover(j2Comio);
            if (j2Comio) {
                puntosJ2++;
                generarManzana();
            }
        } else if (modo == Modo.VS_IA) {
            moverIA();
        }

        Punto cab1 = jugador1.obtenerCabeza();
        boolean j1Murio = fueraDelTablero(cab1) || jugador1.chocaConsigoMisma()
                || (modo == Modo.VS_IA && ia != null && ia.obtenerCuerpo().contains(cab1))
                || (modo == Modo.DOS_JUGADORES && jugador2 != null && jugador2.obtenerCuerpo().contains(cab1));

        if (modo == Modo.DOS_JUGADORES && jugador2 != null) {
            Punto cab2 = jugador2.obtenerCabeza();
            boolean j2Murio = fueraDelTablero(cab2) || jugador2.chocaConsigoMisma()
                    || jugador1.obtenerCuerpo().contains(cab2);

            if (j1Murio || j2Murio) {
                terminado = true;
                if (j1Murio && j2Murio) ganador = 0;
                else if (j1Murio)        ganador = 2;
                else                     ganador = 1;
            }
        } else {
            if (j1Murio) terminado = true;
        }
    }

    // funcion: genera una posicion aleatoria para la manzana que no este ocupada
    private void generarManzana() {
        Punto p;
        do {
            p = new Punto(rnd.nextInt(COLUMNAS), rnd.nextInt(FILAS));
        } while (celdaOcupada(p));
        manzana = p;
    }

    // funcion: revisa si un Punto esta dentro del arreglo dinamico de alguna serpiente
    private boolean celdaOcupada(Punto p) {
        if (jugador1.obtenerCuerpo().contains(p)) return true;
        if (jugador2 != null && jugador2.obtenerCuerpo().contains(p)) return true;
        if (ia != null && ia.obtenerCuerpo().contains(p)) return true;
        return false;
    }

    // funcion: decide la direccion de la IA; 1 de cada 3 movimientos es aleatorio
    private void moverIA_decision() {
        if (rnd.nextInt(3) == 0) {
            // arreglo estatico de enum con las 4 direcciones posibles
            Direccion[] todas = Direccion.values();
            ia.establecerDireccion(todas[rnd.nextInt(todas.length)]);
            return;
        }

        Punto cab = ia.obtenerCabeza();
        int dx = manzana.getColumna() - cab.getColumna();
        int dy = manzana.getFila() - cab.getFila();

        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0) ia.establecerDireccion(Direccion.DERECHA);
            else ia.establecerDireccion(Direccion.IZQUIERDA);
        } else {
            if (dy > 0) ia.establecerDireccion(Direccion.ABAJO);
            else ia.establecerDireccion(Direccion.ARRIBA);
        }
    }

    // funcion: mueve la IA y la hace reaparecer si choca
    private void moverIA() {
        Punto sig = ia.obtenerCabeza().trasladar(ia.obtenerDireccionSiguiente());
        boolean comio = sig.equals(manzana);
        ia.mover(comio);
        if (comio) generarManzana();

        Punto cab = ia.obtenerCabeza();
        if (fueraDelTablero(cab) || ia.chocaConsigoMisma() || jugador1.obtenerCuerpo().contains(cab))
            reaparecerIA();
    }

    // funcion: crea una nueva Serpiente IA en una posicion libre del tablero
    private void reaparecerIA() {
        Punto p;
        int intentos = 0;
        do {
            p = new Punto(rnd.nextInt(COLUMNAS - 6) + 3, rnd.nextInt(FILAS - 6) + 3);
            intentos++;
        } while (jugador1.obtenerCuerpo().contains(p) && intentos < 50);
        // arreglo estatico de enum para elegir la direccion inicial al reaparecer
        Direccion[] dirs = Direccion.values();
        ia = new Serpiente(p.getColumna(), p.getFila(), dirs[rnd.nextInt(dirs.length)]);
    }

    public boolean fueraDelTablero(Punto p) {
        return p.getColumna() < 0 || p.getColumna() >= COLUMNAS
            || p.getFila() < 0 || p.getFila() >= FILAS;
    }

    public void establecerDireccion(Direccion d)  { jugador1.establecerDireccion(d); }
    public void establecerDireccion2(Direccion d) { if (jugador2 != null) jugador2.establecerDireccion(d); }

    public Modo getModo()              { return modo; }
    public Serpiente getSerpiente()    { return jugador1; }
    public Serpiente getSerpiente2()   { return jugador2; }
    public Serpiente getSerpienteIA()  { return ia; }
    public Punto getManzana()          { return manzana; }
    public int getPuntuacion()         { return puntosJ1; }
    public int getPuntuacion2()        { return puntosJ2; }
    public boolean estaTerminado()     { return terminado; }
    public boolean comioManzana()      { return comioManzana; }
    public int getGanador()            { return ganador; }
    public boolean esModoVsIA()        { return modo == Modo.VS_IA; }
}
