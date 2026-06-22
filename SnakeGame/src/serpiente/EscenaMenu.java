package serpiente;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.util.Optional;

public class EscenaMenu {

    private final Stage stage;
    private final Scene escena;

    public EscenaMenu(Stage stage) {
        this.stage = stage;

        VBox root = new VBox(13);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #0d0013;");

        Text icono = new Text("🐍");
        icono.setFont(Font.font("Arial", 58));

        Text titulo = new Text("SNAKE GAME");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        titulo.setFill(Color.web("#2d87ff"));

        // linea decorativa
        Region linea = new Region();
        linea.setPrefHeight(2);
        linea.setMaxWidth(240);
        linea.setStyle("-fx-background-color: linear-gradient(to right, #2d87ff, #00f5ff);");

        VBox instrucciones = new VBox(5,
            fila("🎮", "J1: W A S D   |   J2 / Normal: ↑ ↓ ← →"),
            fila("⭐", "Come manzanas para crecer y sumar puntos"),
            fila("💀", "Evita las paredes y tu propio cuerpo"),
            fila("⌨️",  "R Reiniciar  |  P Pausar  |  ESC Menú")
        );
        instrucciones.setAlignment(Pos.CENTER_LEFT);
        instrucciones.setMaxWidth(360);

        Button btnNormal = btn("▶  JUEGO NORMAL", "#2d87ff", "#0055cc");
        Button btnVsIA   = btn("🤖  VS IA",        "#ff6b35", "#cc4400");
        Button btn2J     = btn("👥  2 JUGADORES",  "#00f5ff", "#00b8cc");
        Button btnRank   = btn("🏆  RANKING",       "#c77dff", "#9b4dca");
        Button btnSalir  = btn("✕  SALIR",          "#4a4a5a", "#333342");

        btnNormal.setOnAction(e -> jugar(ModeloJuego.Modo.NORMAL));
        btnVsIA.setOnAction(e   -> jugar(ModeloJuego.Modo.VS_IA));
        btn2J.setOnAction(e     -> jugar(ModeloJuego.Modo.DOS_JUGADORES));
        btnRank.setOnAction(e   -> stage.setScene(new EscenaRanking(stage).getEscena()));
        btnSalir.setOnAction(e  -> stage.close());

        root.getChildren().addAll(icono, titulo, linea, instrucciones,
                btnNormal, btnVsIA, btn2J, btnRank, btnSalir);

        escena = new Scene(root,
                ModeloJuego.COLUMNAS * LienzoJuego.CELDA + 32,
                ModeloJuego.FILAS    * LienzoJuego.CELDA + 120);
        escena.setFill(Color.web("#0d0013"));
    }

    private HBox fila(String icono, String texto) {
        Text ic = new Text(icono + "  ");
        ic.setFont(Font.font("Arial", 13));
        Text tx = new Text(texto);
        tx.setFont(Font.font("Arial", 12));
        tx.setFill(Color.web("#9966cc"));
        HBox h = new HBox(ic, tx);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    private Button btn(String label, String color, String hover) {
        String colorTexto = color.equals("#00f5ff") || color.equals("#c77dff") ? "#0d0013" : "#ffffff";
        String s1 = "-fx-background-color:" + color + ";-fx-text-fill:" + colorTexto + ";"
                + "-fx-font-size:14px;-fx-font-weight:bold;-fx-padding:9 55;-fx-background-radius:4;"
                + "-fx-effect: dropshadow(gaussian, " + color + ", 6, 0.4, 0, 0);";
        String s2 = "-fx-background-color:" + hover + ";-fx-text-fill:" + colorTexto + ";"
                + "-fx-font-size:14px;-fx-font-weight:bold;-fx-padding:9 55;-fx-background-radius:4;";
        Button b = new Button(label);
        b.setMinWidth(220);
        b.setStyle(s1);
        b.setOnMouseEntered(e -> b.setStyle(s2));
        b.setOnMouseExited(e  -> b.setStyle(s1));
        return b;
    }

    private void jugar(ModeloJuego.Modo modo) {
        if (modo == ModeloJuego.Modo.DOS_JUGADORES) {
            TextInputDialog dlg1 = new TextInputDialog();
            dlg1.setTitle("Snake Game - 2 Jugadores");
            dlg1.setHeaderText("Jugador 1");
            dlg1.setContentText("Ingresa tu nombre:");
            Optional<String> res1 = dlg1.showAndWait();
            if (res1.isEmpty()) return;

            TextInputDialog dlg2 = new TextInputDialog();
            dlg2.setTitle("Snake Game - 2 Jugadores");
            dlg2.setHeaderText("Jugador 2");
            dlg2.setContentText("Ingresa tu nombre:");
            Optional<String> res2 = dlg2.showAndWait();
            if (res2.isEmpty()) return;

            String nombre1 = res1.get().trim().isEmpty() ? "Jugador 1" : res1.get().trim();
            String nombre2 = res2.get().trim().isEmpty() ? "Jugador 2" : res2.get().trim();
            stage.setScene(new EscenaJuego(stage, modo, nombre1, nombre2).getEscena());
        } else {
            TextInputDialog dlg = new TextInputDialog();
            dlg.setTitle("Snake Game");
            dlg.setHeaderText("¡Bienvenido!");
            dlg.setContentText("Ingresa tu nombre:");
            Optional<String> res = dlg.showAndWait();
            if (res.isEmpty()) return;

            String nombre = res.get().trim().isEmpty() ? "Jugador" : res.get().trim();
            stage.setScene(new EscenaJuego(stage, modo, nombre).getEscena());
        }
    }

    public Scene getEscena() { return escena; }
}
