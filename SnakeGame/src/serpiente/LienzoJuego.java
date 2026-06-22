package serpiente;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

// se conecta con ModeloJuego (lee el estado) y Serpiente (recorre el arreglo dinamico del cuerpo)
// EscenaJuego lo llama en cada tick para redibujar todo desde cero
public class LienzoJuego extends Canvas {

    public static final int CELDA = 28;

    private final int cols;
    private final int rows;

    public LienzoJuego(int cols, int rows) {
        super(cols * CELDA, rows * CELDA);
        this.cols = cols;
        this.rows = rows;
    }

    // funcion principal: dibuja fondo, cuadricula, manzana, serpientes y game over
    public void dibujar(ModeloJuego m) {
        GraphicsContext gc = getGraphicsContext2D();
        double w = cols * CELDA;
        double h = rows * CELDA;

        gc.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#0d0013")),
                new Stop(1, Color.web("#1a0028"))));
        gc.fillRect(0, 0, w, h);

        gc.setLineWidth(0.4);
        gc.setStroke(Color.web("#2d0052"));
        for (int c = 0; c <= cols; c++) gc.strokeLine(c * CELDA, 0, c * CELDA, h);
        for (int r = 0; r <= rows; r++) gc.strokeLine(0, r * CELDA, w, r * CELDA);
        gc.setStroke(Color.web("#3d0066"));
        gc.setLineWidth(0.8);
        for (int r = 0; r <= rows; r += 4) gc.strokeLine(0, r * CELDA, w, r * CELDA);

        dibujarManzana(gc, m.getManzana());

        if (m.getModo() == ModeloJuego.Modo.VS_IA && m.getSerpienteIA() != null)
            dibujarSerpiente(gc, m.getSerpienteIA(), "#ff6b35", "#e85d04", "#ff6b35");

        if (m.getModo() == ModeloJuego.Modo.DOS_JUGADORES && m.getSerpiente2() != null)
            dibujarSerpiente(gc, m.getSerpiente2(), "#00f5ff", "#00b8cc", "#00d4e8");

        dibujarSerpiente(gc, m.getSerpiente(), "#2d87ff", "#0055cc", "#2d87ff");

        if (m.estaTerminado())
            dibujarGameOver(gc, m, w, h);
    }

    // funcion: recorre el arreglo dinamico LinkedList<Punto> de la serpiente de cola a cabeza
    private void dibujarSerpiente(GraphicsContext gc, Serpiente s, String cHead, String c1, String c2) {
        java.util.LinkedList<Punto> body = s.obtenerCuerpo();
        for (int i = body.size() - 1; i >= 0; i--) {
            Punto p = body.get(i);
            double x = p.getColumna() * CELDA;
            double y = p.getFila() * CELDA;
            if (i == 0) {
                gc.setFill(Color.web(cHead).deriveColor(0, 1, 1, 0.25));
                gc.fillRoundRect(x, y, CELDA, CELDA, 10, 10);
                gc.setFill(Color.web(cHead));
                gc.fillRoundRect(x + 2, y + 2, CELDA - 4, CELDA - 4, 8, 8);
                dibujarOjos(gc, p, s.obtenerDireccionActual());
            } else {
                gc.setFill(Color.web(i % 2 == 0 ? c1 : c2).deriveColor(0, 1, 1, 0.85));
                gc.fillRoundRect(x + 3, y + 3, CELDA - 6, CELDA - 6, 6, 6);
            }
        }
    }

    // funcion: posiciona los ojos segun la direccion que mira la cabeza
    private void dibujarOjos(GraphicsContext gc, Punto p, Direccion dir) {
        double cx = p.getColumna() * CELDA + CELDA / 2.0;
        double cy = p.getFila() * CELDA + CELDA / 2.0;
        double r = 3, d = 5;
        double x1, y1, x2, y2;
        switch (dir) {
            case ARRIBA:    x1 = cx-d; y1 = cy-d; x2 = cx+d; y2 = cy-d; break;
            case ABAJO:     x1 = cx-d; y1 = cy+d; x2 = cx+d; y2 = cy+d; break;
            case IZQUIERDA: x1 = cx-d; y1 = cy-d; x2 = cx-d; y2 = cy+d; break;
            default:        x1 = cx+d; y1 = cy-d; x2 = cx+d; y2 = cy+d; break;
        }
        gc.setFill(Color.WHITE);
        gc.fillOval(x1-r, y1-r, r*2, r*2);
        gc.fillOval(x2-r, y2-r, r*2, r*2);
        gc.setFill(Color.web("#0d0013"));
        gc.fillOval(x1-r/2.0, y1-r/2.0, r, r);
        gc.fillOval(x2-r/2.0, y2-r/2.0, r, r);
    }

    // funcion: dibuja la manzana con glow, cuerpo y brillo
    private void dibujarManzana(GraphicsContext gc, Punto p) {
        double x = p.getColumna() * CELDA;
        double y = p.getFila() * CELDA;
        double sz = CELDA - 6;

        gc.setFill(Color.web("#ff1a1a").deriveColor(0, 1, 1, 0.2));
        gc.fillOval(x, y + 2, CELDA, CELDA);
        gc.setFill(Color.web("#e60000"));
        gc.fillOval(x + 3, y + 5, sz, sz);
        gc.setFill(Color.web("#ff9999").deriveColor(0, 1, 1, 0.7));
        gc.fillOval(x + 6, y + 7, 7, 5);

        double tx = x + CELDA / 2.0;
        gc.setStroke(Color.web("#a0522d"));
        gc.setLineWidth(2);
        gc.strokeLine(tx, y + 3, tx + 2, y + 1);
        gc.setFill(Color.web("#00c44f"));
        gc.fillOval(tx, y, 5, 4);
    }

    // funcion: dibuja el panel de game over encima del canvas; los botones los agrega EscenaJuego con StackPane
    private void dibujarGameOver(GraphicsContext gc, ModeloJuego m, double w, double h) {
        gc.setFill(Color.color(0.05, 0, 0.1, 0.82));
        gc.fillRect(0, 0, w, h);

        double pw = 370, ph = 160;
        double px = (w - pw) / 2, py = (h - ph) / 2 - 30;

        gc.setFill(Color.web("#110022"));
        gc.fillRoundRect(px, py, pw, ph, 16, 16);
        gc.setStroke(Color.web("#2d87ff"));
        gc.setLineWidth(2);
        gc.strokeRoundRect(px, py, pw, ph, 16, 16);
        gc.setStroke(Color.web("#2d87ff").deriveColor(0, 1, 1, 0.3));
        gc.setLineWidth(6);
        gc.strokeRoundRect(px - 2, py - 2, pw + 4, ph + 4, 18, 18);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 38));
        gc.setFill(Color.web("#2d87ff"));
        gc.fillText("GAME OVER", w / 2, py + 52);

        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        if (m.getModo() == ModeloJuego.Modo.DOS_JUGADORES) {
            String res;
            if (m.getGanador() == 1)      res = "Jugador 1 gana!   J1: " + m.getPuntuacion() + "  J2: " + m.getPuntuacion2();
            else if (m.getGanador() == 2) res = "Jugador 2 gana!   J1: " + m.getPuntuacion() + "  J2: " + m.getPuntuacion2();
            else                           res = "Empate!   J1: " + m.getPuntuacion() + "  J2: " + m.getPuntuacion2();
            gc.setFill(Color.web("#00f5ff"));
            gc.fillText(res, w / 2, py + 88);
        } else {
            gc.setFill(Color.web("#00f5ff"));
            gc.fillText("Puntuación: " + m.getPuntuacion(), w / 2, py + 88);
        }

        gc.setFill(Color.web("#6633aa"));
        gc.setFont(Font.font("Arial", 12));
        gc.fillText("R para reiniciar   |   ESC para el menú", w / 2, py + 125);
    }
}
