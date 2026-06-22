package serpiente;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// se conecta con EscenaJuego (envia puntaje) y EscenaRanking (obtiene lista)
// se comunica con el servidor en Render via HTTP
public class ClienteRanking {

    private static final String URL_SERVIDOR = "https://snake-gamez.onrender.com";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();

    // funcion: hace GET al servidor y devuelve el arreglo dinamico con el top 10
    public List<EntradaRanking> obtenerRanking() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(URL_SERVIDOR + "/api/rankings"))
                .timeout(Duration.ofSeconds(8))
                .GET()
                .build();
        return parsear(http.send(req, HttpResponse.BodyHandlers.ofString()).body());
    }

    // funcion: hace POST al servidor con nombre y puntuacion del jugador
    public List<EntradaRanking> enviarPuntuacion(String nombre, int puntuacion)
            throws IOException, InterruptedException {
        if (nombre == null || nombre.isBlank()) nombre = "Anónimo";
        String json = "{\"name\":\"" + escapar(nombre) + "\",\"score\":" + puntuacion + "}";

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(URL_SERVIDOR + "/api/rankings"))
                .timeout(Duration.ofSeconds(8))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return parsear(http.send(req, HttpResponse.BodyHandlers.ofString()).body());
    }

    // patron de busqueda para extraer name y score del JSON que devuelve el servidor
    private static final Pattern PATRON =
            Pattern.compile("\"name\"\\s*:\\s*\"(.*?)\"\\s*,\\s*\"score\"\\s*:\\s*(-?\\d+)");

    // funcion: convierte el JSON del servidor en un arreglo dinamico de EntradaRanking
    private List<EntradaRanking> parsear(String json) {
        // arreglo dinamico ArrayList que acumula cada entrada del ranking
        List<EntradaRanking> lista = new ArrayList<>();
        if (json == null) return lista;
        Matcher m = PATRON.matcher(json);
        while (m.find()) {
            lista.add(new EntradaRanking(desescapar(m.group(1)), Integer.parseInt(m.group(2))));
        }
        return lista;
    }

    private String escapar(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String desescapar(String s) {
        return s.replace("\\\\", "\\").replace("\\\"", "\"");
    }
}
