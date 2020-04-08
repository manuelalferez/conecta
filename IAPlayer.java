package conecta4;

/**
 * Esta clase representa la inteligencia artificial cuyo objetivo es ganar a su adversario humano.
 *
 * IAPlayer realizará aquellos movimiento con menor valor.
 *
 */
public class IAPlayer extends Player {
    private final int SIN_JUGADA = -1;

    /**
     * @param tablero Representación del tablero de juego
     * @param conecta Número de fichas consecutivas para ganar
     * @return Jugador ganador (si lo hay)
     */
    @Override
    public int turnoJugada(Grid tablero, int conecta) {
        int[][] copia_tablero = tablero.toArray();
        int mejorJugada = algoritmoMinMax(copia_tablero);
        return tablero.checkWin(tablero.setButton(mejorJugada, Conecta4.PLAYER2), mejorJugada, conecta);
    }

    /**
     *
     * @param tablero Estado actual del tablero
     * @return La columna con el mejor movimiento
     */
    private int algoritmoMinMax(int tablero[][]) {
        int mejorJugada = SIN_JUGADA;
        mejorJugada = minimizar (tablero);
        return mejorJugada;
    }

    private int minimizar(int tablero[][]){
        int mejorJugada = SIN_JUGADA;
        for (int i = 0; i < tablero.length; i++) {

        }
    }
}
