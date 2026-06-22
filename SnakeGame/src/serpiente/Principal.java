package serpiente;

import javafx.application.Application;
import javafx.stage.Stage;

public class Principal extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Juego de la Serpiente");
        stage.setScene(new EscenaMenu(stage).getEscena());
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
