package serpiente;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class EscenaJuego {

    private static final long PASO_NS = 150_000_000L; // 150 ms por tick

    private final Stage stage;
    private final Scene escena;
    private final ModeloJuego modelo;
    private final LienzoJuego lienzo;
    private final ClienteRanking ranking = new ClienteRanking();

    private Text lblPuntos;
    private Text lblLongitud;
    private Text lblRival;
    private VBox overlayGameOver;
    private AnimationTimer timer;
    private long tUltimo = 0;
    private boolean pausado = false;
    private boolean yaEnvio = false; // evita doble envio si el jugador reinicia rapido
    private String nombreJugador  = "Jugador";
    private String nombreJugador2 = "Jugador 2";

    public EscenaJuego(Stage stage, ModeloJuego.Modo modo, String nombre) {
        this.nombreJugador = nombre;
        this.stage  = stage;
        this.modelo = new ModeloJuego(modo);
        this.lienzo = new LienzoJuego(ModeloJuego.COLUMNAS, ModeloJuego.FILAS);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0d0013;");
        root.setPadding(new Insets(10, 16, 10, 16));
        root.setTop(crearHUD());
        root.setCenter(crearCentro()); // lienzo + overlay de game over

        escena = new Scene(root);
        escena.setFill(Color.web("#0d0013"));

        // addEventFilter en lugar de setOnKeyPressed para que lleguen aunque un boton tenga el foco
        escena.addEventFilter(KeyEvent.KEY_PRESSED, e -> manejarTecla(e));
        iniciarBucle();
    }

    public EscenaJuego(Stage stage, ModeloJuego.Modo modo, String nombre1, String nombre2) {
        this(stage, modo, nombre1);
        this.nombreJugador2 = nombre2;
    }

    private HBox crearHUD() {
        Button btnMenu = new Button("☰ Menú");
        btnMenu.setFocusTraversable(false);
        aplicarEstiloBtn(btnMenu, "#3d0066", "#2d87ff");
        btnMenu.setOnAction(e -> volverAlMenu());

        HBox hud;
        if (modelo.getModo() == ModeloJuego.Modo.VS_IA) {
            lblPuntos   = textoHUD("Puntuación: 0", "#ecf0f1");
            lblLongitud = textoHUD("Longitud: 3", "#ecf0f1");
            lblRival    = textoHUD("IA: 3", "#ff6b35");
            Region sp1 = new Region(), sp2 = new Region();
            HBox.setHgrow(sp1, Priority.ALWAYS);
            HBox.setHgrow(sp2, Priority.ALWAYS);
            hud = new HBox(20, lblPuntos, lblLongitud, sp1, lblRival, sp2, btnMenu);

        } else if (modelo.getModo() == ModeloJuego.Modo.DOS_JUGADORES) {
            lblPuntos   = textoHUD(nombreJugador + ": 0", "#2d87ff");
            lblLongitud = textoHUD("Long: 3 | 3", "#ecf0f1");
            lblRival    = textoHUD(nombreJugador2 + ": 0", "#00f5ff");
            Region sp1 = new Region(), sp2 = new Region();
            HBox.setHgrow(sp1, Priority.ALWAYS);
            HBox.setHgrow(sp2, Priority.ALWAYS);
            hud = new HBox(20, lblPuntos, sp1, lblLongitud, sp2, lblRival, new Region(), btnMenu);

        } else {
            lblPuntos   = textoHUD("Puntuación: 0", "#ecf0f1");
            lblLongitud = textoHUD("Longitud: 3", "#ecf0f1");
            Region sp1 = new Region(), sp2 = new Region();
            HBox.setHgrow(sp1, Priority.ALWAYS);
            HBox.setHgrow(sp2, Priority.ALWAYS);
            hud = new HBox(20, lblPuntos, sp1, lblLongitud, sp2, btnMenu);
        }

        hud.setAlignment(Pos.CENTER_LEFT);
        hud.setPadding(new Insets(0, 0, 8, 0));
        return hud;
    }

    // el overlay de botones se oculta hasta que el juego termine
    private StackPane crearCentro() {
        overlayGameOver = new VBox(12);
        overlayGameOver.setAlignment(Pos.CENTER);
        overlayGameOver.setTranslateY(80); // lo baja para quedar sobre el panel dibujado en canvas
        overlayGameOver.setVisible(false);
        overlayGameOver.setFocusTraversable(false);

        Button btnR = new Button("↺  Reiniciar");
        Button btnM = new Button("←  Menú");
        btnR.setFocusTraversable(false);
        btnM.setFocusTraversable(false);
        aplicarEstiloBtnGrande(btnR, "#2d87ff", "#0055cc");
        aplicarEstiloBtnGrande(btnM, "#3d0066", "#6600cc");
        btnR.setOnAction(e -> reiniciar());
        btnM.setOnAction(e -> volverAlMenu());

        HBox bots = new HBox(16, btnR, btnM);
        bots.setAlignment(Pos.CENTER);
        overlayGameOver.getChildren().add(bots);

        StackPane centro = new StackPane(lienzo, overlayGameOver);
        StackPane.setAlignment(overlayGameOver, Pos.CENTER);
        return centro;
    }

    private HBox crearPiePagina() {
        String controles = modelo.getModo() == ModeloJuego.Modo.DOS_JUGADORES
                ? "J1: W A S D   |   J2: ↑ ↓ ← →   |   P Pausar   |   ESC Menú"
                : "↑ ↓ ← →  Mover   |   P  Pausar   |   ESC  Menú";
        Text t = new Text(controles);
        t.setFont(Font.font("Arial", 12));
        t.setFill(Color.web("#6633aa"));
        HBox pie = new HBox(t);
        pie.setAlignment(Pos.CENTER);
        pie.setPadding(new Insets(8, 0, 0, 0));
        return pie;
    }

    private void manejarTecla(KeyEvent e) {
        KeyCode k = e.getCode();

        if (modelo.getModo() == ModeloJuego.Modo.DOS_JUGADORES) {
            // J1 usa WASD, J2 usa flechas
            switch (k) {
                case W: modelo.establecerDireccion(Direccion.ARRIBA);     e.consume(); break;
                case S: modelo.establecerDireccion(Direccion.ABAJO);      e.consume(); break;
                case A: modelo.establecerDireccion(Direccion.IZQUIERDA);  e.consume(); break;
                case D: modelo.establecerDireccion(Direccion.DERECHA);    e.consume(); break;
                case UP:    modelo.establecerDireccion2(Direccion.ARRIBA);    e.consume(); break;
                case DOWN:  modelo.establecerDireccion2(Direccion.ABAJO);     e.consume(); break;
                case LEFT:  modelo.establecerDireccion2(Direccion.IZQUIERDA); e.consume(); break;
                case RIGHT: modelo.establecerDireccion2(Direccion.DERECHA);   e.consume(); break;
                default: break;
            }
        } else {
            switch (k) {
                case UP:    modelo.establecerDireccion(Direccion.ARRIBA);    e.consume(); break;
                case DOWN:  modelo.establecerDireccion(Direccion.ABAJO);     e.consume(); break;
                case LEFT:  modelo.establecerDireccion(Direccion.IZQUIERDA); e.consume(); break;
                case RIGHT: modelo.establecerDireccion(Direccion.DERECHA);   e.consume(); break;
                default: break;
            }
        }

        switch (k) {
            case R:      reiniciar();    e.consume(); break;
            case P:      pausar();       e.consume(); break;
            case ESCAPE: volverAlMenu(); e.consume(); break;
            default: break;
        }
    }

    private void iniciarBucle() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (pausado || now - tUltimo < PASO_NS) return;
                boolean antes = modelo.estaTerminado();
                modelo.avanzar();
                actualizarHUD();
                lienzo.dibujar(modelo);
                tUltimo = now;
                if (!antes && modelo.estaTerminado()) onGameOver();
            }
        };
        timer.start();
    }

    private void actualizarHUD() {
        if (modelo.getModo() == ModeloJuego.Modo.DOS_JUGADORES) {
            lblPuntos.setText(nombreJugador + ": " + modelo.getPuntuacion());
            int l2 = modelo.getSerpiente2() != null ? modelo.getSerpiente2().obtenerLongitud() : 3;
            lblLongitud.setText("Long: " + modelo.getSerpiente().obtenerLongitud() + " | " + l2);
            if (lblRival != null) lblRival.setText(nombreJugador2 + ": " + modelo.getPuntuacion2());
        } else {
            lblPuntos.setText("Puntuación: " + modelo.getPuntuacion());
            lblLongitud.setText("Longitud: " + modelo.getSerpiente().obtenerLongitud());
            if (lblRival != null && modelo.getSerpienteIA() != null)
                lblRival.setText("IA: " + modelo.getSerpienteIA().obtenerLongitud());
        }
    }

    private void reiniciar() {
        modelo.reiniciar();
        pausado = false;
        yaEnvio = false;
        tUltimo = 0;
        overlayGameOver.setVisible(false);
        actualizarHUD();
        lienzo.dibujar(modelo);
        timer.start();
    }

    private void pausar() {
        if (modelo.estaTerminado()) return;
        pausado = !pausado;
        if (!pausado) lienzo.dibujar(modelo);
    }

    private void onGameOver() {
        timer.stop();
        overlayGameOver.setVisible(true);
        if (modelo.getModo() != ModeloJuego.Modo.DOS_JUGADORES)
            pedirNombre();
    }

    private void pedirNombre() {
        if (yaEnvio || modelo.getModo() == ModeloJuego.Modo.DOS_JUGADORES) return;
        yaEnvio = true;
        int pts = modelo.getPuntuacion();
        String nombre = nombreJugador;

        // hilo daemon para no bloquear el hilo de JavaFX mientras espera la respuesta HTTP
        Thread t = new Thread(() -> {
            try {
                ranking.enviarPuntuacion(nombre, pts);
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    Alert a = new Alert(Alert.AlertType.WARNING, "No se pudo guardar el puntaje en línea.");
                    a.setHeaderText("Sin conexión");
                    a.showAndWait();
                });
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void volverAlMenu() {
        timer.stop();
        stage.setScene(new EscenaMenu(stage).getEscena());
    }

    private Text textoHUD(String txt, String color) {
        Text t = new Text(txt);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        t.setFill(Color.web(color));
        return t;
    }

    private void aplicarEstiloBtn(Button b, String bg, String hover) {
        String s1 = "-fx-background-color:" + bg + ";-fx-text-fill:#ffffff;-fx-font-size:13px;-fx-padding:4 12;-fx-background-radius:4;";
        String s2 = "-fx-background-color:" + hover + ";-fx-text-fill:#ffffff;-fx-font-size:13px;-fx-padding:4 12;-fx-background-radius:4;";
        b.setStyle(s1);
        b.setOnMouseEntered(e -> b.setStyle(s2));
        b.setOnMouseExited(e  -> b.setStyle(s1));
    }

    private void aplicarEstiloBtnGrande(Button b, String bg, String hover) {
        String s1 = "-fx-background-color:" + bg + ";-fx-text-fill:#ffffff;-fx-font-size:14px;-fx-font-weight:bold;-fx-padding:8 24;-fx-background-radius:6;";
        String s2 = "-fx-background-color:" + hover + ";-fx-text-fill:#ffffff;-fx-font-size:14px;-fx-font-weight:bold;-fx-padding:8 24;-fx-background-radius:6;";
        b.setStyle(s1);
        b.setOnMouseEntered(e -> b.setStyle(s2));
        b.setOnMouseExited(e  -> b.setStyle(s1));
    }

    public Scene getEscena() { return escena; }
}
