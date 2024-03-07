# Blackjack JavaFX Application

## Descripción
Esta es una aplicación JavaFX para jugar al popular juego de cartas Blackjack.
El desarrollo de esta aplicación y su api se realizaron entre las fechas: 26/02/24 y 08/03/24. La implementación de la aplicación Blackjack en JavaFX ha seguido un proceso iterativo, enfocado en la modularidad y la escalabilidad del código. Se han utilizado patrones de diseño adecuados para gestionar la lógica del juego, la presentación de la interfaz de usuario y la comunicación con servicios externos.

## Dependencias
- **Jackson Databind Library**: Utilizamos la biblioteca `jackson-databind` para el procesamiento de JSON.

  ```xml
  <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
      <version>2.13.0</version>
  </dependency>
  ```

## Reglas del Juego
El Blackjack es un juego de cartas donde el objetivo es vencer al crupier sin pasarse de 21 puntos.

### Valores de las Cartas
- Las cartas numéricas tienen el valor de su número.
- Las figuras (J, Q, K) valen 10 puntos.
- Los Ases pueden valer 1 u 11 puntos.

### Juego
1. Se reparte una carta al jugador y 2 al crupier.
2. El jugador decide si pedir más cartas o plantarse.
3. Si el total de las cartas del jugador supera 21, pierde.
4. El crupier juega después del jugador.
5. El crupier debe plantarse si supera al jugador y no pasa de 21.
6. El que tenga un total más alto sin sobrepasar 21, gana.

### Puntuaciones
- Esta aplicacion ofrece un servicio de puntuaciones.
- El jugador conseguirá una racha ganadora jugando.
- En cualquier momento el jugador puede guardar su puntuacion en la base de datos.

## Uso
1. Clona este repositorio.
2. Clona el repositorio: https://github.com/Borja-Cisneros/BlackJack-Borja-API
3. Ejecuta la api
4. Ejecuta esta aplicación.
5. Disfruta del juego del Blackjack.