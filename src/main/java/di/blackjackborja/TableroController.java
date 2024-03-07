package di.blackjackborja;

import di.carta.Carta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.Modality;
import javafx.stage.Stage;

// Controlador para la lógica del juego de blackjack
public class TableroController {
    // URL base para las solicitudes HTTP
    private static final String BASE_URL = "http://localhost:8080";

    // Inyección de componentes de la interfaz de usuario
    @FXML
    private TableView<Map<String, Object>> tabla;
    @FXML
    private TableColumn<Map, String> cNombre;
    @FXML
    private TableColumn<Map, String> cRacha;
    @FXML
    private VBox botonera;
    @FXML
    private HBox cartasIA;
    @FXML
    private HBox hBoxBaraja;
    @FXML
    private Label labelRacha;
    @FXML
    private HBox cartasJugador;
    @FXML
    private ImageView derrota;
    @FXML
    private ImageView victoria;
    @FXML
    private Label labelPuntosIA;
    @FXML
    private Button init;
    @FXML
    private VBox botonesMenu;
    @FXML
    private AnchorPane hudJuego;
    @FXML
    private Label labelPuntosJugador;
    @FXML
    private AnchorPane top5;

    // Lista de cartas en la baraja
    List<Carta> baraja;
    // Puntos del jugador y la IA
    private int puntosJugador;
    private int puntosIA;
    // Racha actual del jugador
    private int racha = 0;

    // Método para guardar la puntuación del jugador
    @FXML
    void guardarPuntuacion(ActionEvent event) {
        insertarPuntuacion(Integer.parseInt(labelRacha.getText()), abrirVentanaEmergente());
    }

    // Método para solicitar una carta para el jugador
    @FXML
    void pedirCartas(ActionEvent event) {
        darCartaJugador();
    }

    // Método para que el jugador se plante
    @FXML
    void plantarse(ActionEvent event) {
        jugarMaquina(false);
    }

    // Método para reiniciar el juego
    @FXML
    private void reiniciar() {
        llenarTablaPuntuaciones();
        victoria.setVisible(false);
        derrota.setVisible(false);
        hudJuego.setVisible(true);
        botonesMenu.setVisible(false);
        top5.setVisible(false);
        botonera.setDisable(false);
        init.setText("Volver a jugar");
        cartasJugador.getChildren().clear();
        cartasIA.getChildren().clear();
        repartirCartasIniciales();
    }

    // Método para finalizar la partida y mostrar los resultados
    private void acabarPartida(Estado estado) {
        botonesMenu.setVisible(true);
        top5.setVisible(true);
        botonera.setDisable(true);
        switch (estado) {
            case VICTORIA -> {
                racha++;
                labelRacha.setText(String.valueOf(racha));
                victoria.setVisible(true);
            }
            case EMPATE, DERROTA -> {
                racha = 0;
                labelRacha.setText(String.valueOf(racha));
                derrota.setVisible(true);
            }
        }
    }

    // Método para que la IA juegue
    private void jugarMaquina(boolean acabado) {
        int i = 2;
        mostrarCartaIA(1);
        sumarPuntosIA(acabado);
        if (puntosJugador <= 21) {
            while (puntosIA < puntosJugador) {
                darCartaOrdenador();
                mostrarCartaIA(i);
                sumarPuntosIA(acabado);
                i++;
            }
        }
    }

    // Método para sumar los puntos de la IA
    public void sumarPuntosIA(boolean acabado) {
        puntosIA = 0;
        boolean as = false;
        for (Node c : cartasIA.getChildren()) {
            Carta carta = (Carta) c;
            if (carta.getNombre().matches("AS")) {
                as = true;
            }
            if (carta.isVisible()) {
                puntosIA += carta.getValor();
            }
        }

        if (puntosIA > 21 && as) {
            puntosIA -= 10;
        }

        labelPuntosIA.setText(String.valueOf(puntosIA));

        if(!acabado) {
            if (puntosIA > 21) {
                acabarPartida(Estado.VICTORIA);
            } else if (puntosIA == puntosJugador) {
                acabarPartida(Estado.EMPATE);
            } else if (puntosIA > puntosJugador){
                acabarPartida(Estado.DERROTA);
            }
        }
    }

    // Método para sumar los puntos del jugador
    public void sumarPuntosJugador() {
        puntosJugador = 0;
        int as = 0;
        for (Node c : cartasJugador.getChildren()) {
            Carta carta = (Carta) c;
            if (carta.getNombre().matches("AS")) {
                as++;
            }
            puntosJugador += carta.getValor();
        }

        while (as > 0 && puntosJugador > 21) {
            puntosJugador -= 10;
            as--;
        }
        labelPuntosJugador.setText(String.valueOf(puntosJugador));

        if (puntosJugador > 21) {
            jugarMaquina(true);
            acabarPartida(Estado.DERROTA);
        }
    }

    // Inicialización del controlador
    public void initialize() {
        hudJuego.setVisible(false);
        iniciarTabla();
        llenarTablaPuntuaciones();
        crearBaraja();
    }

    // Método para crear la baraja de cartas
    public void crearBaraja() {
        baraja = new ArrayList<>();
        char[] palos = {'C', 'T', 'P', 'D'};
        String[] nombres = {"AS", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        int valor;

        for (char palo : palos) {
            for (int i = 0; i < 13; i++) {
                if (i >= 10) {
                    valor = 10;
                } else if (i == 0) {
                    valor = 11;
                } else {
                    valor = i + 1;
                }
                Carta carta = new Carta();
                carta.setPalo(palo);
                carta.setNombre(nombres[i]);
                carta.setValor(valor);
                this.baraja.add(carta);
            }
        }
    }

    // Método para repartir las cartas iniciales
    public void repartirCartasIniciales() {
        if (hBoxBaraja.getChildren().isEmpty()) {
            Carta carta = new Carta();
            carta.setDisable(true);
            hBoxBaraja.getChildren().add(carta);
        }
        darCartaOrdenador();
        darCartaOrdenador();
        darCartaJugador();
        mostrarCartaIA(0);
        Carta carta = (Carta) cartasIA.getChildren().get(0);
        labelPuntosIA.setText(String.valueOf(carta.getValor()));
    }

    // Método para mostrar una carta de la IA
    public void mostrarCartaIA(int indice) {
        Carta carta = (Carta) cartasIA.getChildren().get(indice);
        carta.mostrarCarta();
    }

    // Método para sacar una carta de la baraja
    public Carta sacarCarta() {
        Carta carta = null;
        Random aleatorio = new Random(System.currentTimeMillis());
        boolean control = true;
        while (control) {
            carta = this.baraja.get(aleatorio.nextInt(52));
            if (!carta.isRepartida()) {
                carta.setRepartida(true);
                control = false;
            }
        }
        return carta;
    }

    // Método para dar una carta al jugador
    public void darCartaJugador() {
        this.cartasJugador.getChildren().add(this.sacarCarta());
        sumarPuntosJugador();
    }

    // Método para dar una carta a la IA
    public void darCartaOrdenador() {
        this.cartasIA.getChildren().add(this.sacarCarta());
    }

    // Método para iniciar la tabla de puntuaciones
    public void iniciarTabla() {
        cNombre.setCellValueFactory(new MapValueFactory<>("nombre"));
        cRacha.setCellValueFactory(new MapValueFactory<>("puntuacion"));
    }

    // Método para llenar la tabla de puntuaciones
    private void llenarTablaPuntuaciones() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/puntuacion/ordenadas"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::procesarRespuestaPuntuaciones)
                .join();
    }

    // Método para procesar la respuesta de la API de puntuaciones
    private void procesarRespuestaPuntuaciones(String respuesta) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Convertir la respuesta JSON a una lista de mapas
            ObservableList<Map<String, Object>> listaPuntuaciones = FXCollections.observableArrayList(
                    objectMapper.readValue(respuesta, Map[].class)
            );

            // Limitar la lista a los primeros 5 registros
            listaPuntuaciones.remove(5, listaPuntuaciones.size());
            // Llenar la tabla de puntuaciones con los datos
            tabla.setItems(listaPuntuaciones);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final HttpClient httpClient = HttpClient.newHttpClient();

    // Método para insertar la puntuación del jugador en la API
    public void insertarPuntuacion(int puntuacion, String nombre) {
        String requestBody = String.format("{\"puntuacion\": %d, \"nombre\": \"%s\"}", puntuacion, nombre);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BASE_URL + "/api/puntuacion/engadir"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                System.out.println("Puntuación insertada exitosamente.");
            } else {
                System.out.println("Error al insertar la puntuación - Código de respuesta: " + response.statusCode());
            }
        } catch (URISyntaxException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    // Método para abrir una ventana emergente y obtener el nombre del jugador
    private String abrirVentanaEmergente() {
        Stage ventanaEmergente = new Stage();
        ventanaEmergente.initModality(Modality.APPLICATION_MODAL);
        ventanaEmergente.setTitle("Guardar Puntuacion");
        ventanaEmergente.setMinWidth(250);

        Label labelNombre = new Label("Nombre del Jugador:");
        TextField textFieldNombre = new TextField();
        Button btnAceptar = new Button("Aceptar");
        btnAceptar.setOnAction(e -> {
            ventanaEmergente.close();
        });

        VBox layoutVentanaEmergente = new VBox(10);
        layoutVentanaEmergente.getChildren().addAll(labelNombre, textFieldNombre, btnAceptar);
        layoutVentanaEmergente.setPadding(new Insets(10));

        Scene sceneVentanaEmergente = new Scene(layoutVentanaEmergente, 250, 150);
        ventanaEmergente.setScene(sceneVentanaEmergente);
        ventanaEmergente.showAndWait();

        return textFieldNombre.getText();
    }
}