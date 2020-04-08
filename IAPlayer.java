package conecta4;

public class IAPlayer extends Player {

    /**
     *
     * @param tablero Representación del tablero de juego
     * @param conecta Número de fichas consecutivas para ganar
     * @return Jugador ganador (si lo hay)
     */
    @Override
    public int turnoJugada(Grid tablero, int conecta) {

        // ...
        // Calcular la mejor columna posible donde hacer nuestra turnoJugada
        //Pintar Ficha (sustituir 'columna' por el valor adecuado)
        //Pintar Ficha
        int columna = getRandomColumn(tablero);

        return tablero.checkWin(tablero.setButton(columna, Conecta4.PLAYER2), columna, conecta);

    }

}
