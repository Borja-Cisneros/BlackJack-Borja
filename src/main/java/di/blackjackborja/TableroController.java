package di.blackjackborja;

import di.carta.Carta;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Shadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TableroController {
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
    private Label labelPuntosIA;
    @FXML
    private Button init;
    @FXML
    private VBox botonesMenu;

    @FXML
    private AnchorPane hudJuego;

    @FXML
    private Label labelPuntosJugador;
    List<Carta> baraja;
    private int puntosJugador;
    private int puntosIA;
    private int racha = 0;
    @FXML
    void guardarPuntuacion(ActionEvent event) {

    }
    @FXML
    void pedirCartas(ActionEvent event) {
        darCartaJugador();
    }

    @FXML
    void plantarse(ActionEvent event) {
        jugarMaquina(false);
    }
    @FXML
    private void reiniciar() {
        hudJuego.setVisible(true);
        botonesMenu.setVisible(false);
        init.setText("Volver a jugar");
        botonera.setDisable(false);
        cartasJugador.getChildren().clear();
        cartasIA.getChildren().clear();
        repartirCartasIniciales();
    }

    private void acabarPartida(Estado estado) {
        botonesMenu.setVisible(true);
        botonera.setDisable(true);
        switch (estado) {
            case VICTORIA -> {
                // Mostar Victoria
                racha++;
                labelRacha.setText(String.valueOf(racha));
            }
            case EMPATE -> {
                // Mostrar Empate
                racha = 0;
                labelRacha.setText(String.valueOf(racha));
            }
            case DERROTA -> {
                // Mostrar Derrota
                racha = 0;
                labelRacha.setText(String.valueOf(racha));
            }
        }

    }

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
                System.out.println("aaaa");
                acabarPartida(Estado.VICTORIA);
            } else if (puntosIA == puntosJugador) {
                acabarPartida(Estado.EMPATE);
            } else if (puntosIA > puntosJugador){
                System.out.println("bbb");
                acabarPartida(Estado.DERROTA);
            }
        }
    }

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

    public void initialize() {
        hudJuego.setVisible(false);

        crearBaraja();
    }

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
                System.out.println(carta);
            }
        }
        System.out.println(baraja);
    }

    public void repartirCartasIniciales() {
        hBoxBaraja.getChildren().add(new Carta());
        darCartaOrdenador();
        darCartaOrdenador();
        darCartaJugador();
        mostrarCartaIA(0);
        Carta carta = (Carta) cartasIA.getChildren().get(0);
        labelPuntosIA.setText(String.valueOf(carta.getValor()));
    }

    public void mostrarCartaIA(int indice) {
        Carta carta = (Carta) cartasIA.getChildren().get(indice);
        carta.mostrarCarta();
    }

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

    public void darCartaJugador() {
        this.cartasJugador.getChildren().add(this.sacarCarta());
        sumarPuntosJugador();
    }

    public void darCartaOrdenador() {
        this.cartasIA.getChildren().add(this.sacarCarta());
    }
}