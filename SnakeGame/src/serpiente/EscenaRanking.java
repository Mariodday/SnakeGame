package serpiente;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.List;

// se conecta con ClienteRanking (pide la lista) y EscenaMenu (boton volver)
public class EscenaRanking {

    private final Stage stage;
    private final Scene escena;
    // se conecta con ClienteRanking para obtener y mostrar el ranking del servidor
    private final ClienteRanking clienteRanking = new ClienteRanking();

    private VBox listaRanking;
    private Text estado;

    public EscenaRanking(Stage stage) {
        this.stage = stage;

        VBox root = new VBox(16);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30, 40, 30, 40));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Text titulo = new Text("🏆 RANKING GLOBAL");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 26));
        titulo.setFill(Color.web("#4ecca3"));

        estado = new Text("Cargando...");
        estado.setFont(Font.font("Arial", 13));
        estado.setFill(Color.web("#7f8c8d"));

        listaRanking = new VBox(4);
        listaRanking.setAlignment(Pos.TOP_LEFT);
        listaRanking.setMaxWidth(360);

        ScrollPane scroll = new ScrollPane(listaRanking);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(300);
        scroll.setStyle("-fx-background: #16213e; -fx-background-color: #16213e;");

        Button btnActualizar = crearBtn("↺  Actualizar", "#0f3460", "#16213e");
        Button btnVolver     = crearBtn("←  Volver",     "#e74c3c", "#c0392b");
        btnActualizar.setOnAction(e -> cargarRanking());
        btnVolver.setOnAction(e     -> stage.setScene(new EscenaMenu(stage).getEscena()));

        HBox bots = new HBox(12, btnActualizar, btnVolver);
        bots.setAlignment(Pos.CENTER);

        root.getChildren().addAll(titulo, estado, scroll, bots);

        escena = new Scene(root,
                ModeloJuego.COLUMNAS * LienzoJuego.CELDA + 32,
                ModeloJuego.FILAS    * LienzoJuego.CELDA + 90);
        escena.setFill(Color.web("#1a1a2e"));

        cargarRanking();
    }

    // funcion: llama al servidor en un hilo separado para no congelar la pantalla
    private void cargarRanking() {
        estado.setText("Cargando...");
        listaRanking.getChildren().clear();

        Thread t = new Thread(() -> {
            try {
                // arreglo dinamico List<EntradaRanking> devuelto por ClienteRanking
                List<EntradaRanking> datos = clienteRanking.obtenerRanking();
                Platform.runLater(() -> mostrar(datos));
            } catch (Exception ex) {
                Platform.runLater(() -> estado.setText("⚠ Sin conexión al servidor."));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    // funcion: recorre el arreglo dinamico y construye una fila visual por cada EntradaRanking
    private void mostrar(List<EntradaRanking> datos) {
        listaRanking.getChildren().clear();
        if (datos.isEmpty()) {
            estado.setText("Aún no hay registros.");
            return;
        }
        estado.setText("Top " + datos.size() + " jugadores:");

        for (int i = 0; i < datos.size(); i++) {
            EntradaRanking e = datos.get(i);
            String medalla = i == 0 ? "🥇" : i == 1 ? "🥈" : i == 2 ? "🥉" : "#" + (i + 1);

            Text pos = mk(medalla, 14, "#f1c40f", false);
            Text nom = mk(e.getNombre(), 14, "#ecf0f1", true);
            Text pts = mk(String.valueOf(e.getPuntuacion()), 14, "#4ecca3", true);

            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            HBox fila = new HBox(10, pos, nom, sp, pts);
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.setPadding(new Insets(4, 8, 4, 8));
            fila.setStyle("-fx-background-color:" + (i % 2 == 0 ? "#16213e" : "#1a1a2e") + ";");
            listaRanking.getChildren().add(fila);
        }
    }

    private Text mk(String txt, int size, String color, boolean bold) {
        Text t = new Text(txt);
        t.setFont(bold ? Font.font("Arial", FontWeight.BOLD, size) : Font.font("Arial", size));
        t.setFill(Color.web(color));
        return t;
    }

    private Button crearBtn(String label, String bg, String hov) {
        String s1 = "-fx-background-color:" + bg + ";-fx-text-fill:#ecf0f1;-fx-font-size:13px;-fx-font-weight:bold;-fx-padding:8 20;-fx-background-radius:8;";
        String s2 = "-fx-background-color:" + hov + ";-fx-text-fill:#ecf0f1;-fx-font-size:13px;-fx-font-weight:bold;-fx-padding:8 20;-fx-background-radius:8;";
        Button b = new Button(label);
        b.setStyle(s1);
        b.setOnMouseEntered(e -> b.setStyle(s2));
        b.setOnMouseExited(e  -> b.setStyle(s1));
        return b;
    }

    public Scene getEscena() { return escena; }
}
