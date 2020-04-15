package conecta4;

import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Esta clase representa la inteligencia artificial cuyo objetivo es ganar a su adversario humano.
 * <p>
 * IAPlayer realizará aquellos movimiento con menor valor.
 */
public class IAPlayer extends Player {
    private final int SIN_JUGADA = -1;
    private int CONECTA_N = 0;
    private final int SIN_GANADOR = 0;
    private final int PEOR_VALORACION_MIN = 1;
    private final int PEOR_VALORACION_MAX = -1;
    private int FILAS;
    private int COLUMNAS;
    private int tablero_copia[][];
    private String VACIAR = "";
    String log = "";

    /**
     * @param tablero Representación del tablero de juego
     * @param conecta Número de fichas consecutivas para ganar
     * @return Jugador ganador (si lo hay)
     */
    @Override
    public int turnoJugada(Grid tablero, int conecta) {
        CONECTA_N = conecta;
        FILAS = tablero.getFilas();
        COLUMNAS = tablero.getColumnas();
        copiarTablero(tablero.toArray());
        imprimirTablero();
        int mejorJugada = algoritmoMinMax();
        System.out.println("Jugada en: " + mejorJugada);
        // escribirLogs();
        //log = VACIAR;
        return tablero.checkWin(tablero.setButton(mejorJugada, Conecta4.PLAYER2), mejorJugada, conecta);
    }

    /**
     * @param tablero Estado actual del tablero
     * @return La columna con el mejor movimiento
     */
    private int algoritmoMinMax() {
        int mejor_jugada = SIN_JUGADA;
        int valoracion = PEOR_VALORACION_MIN;
        int mejor_valoracion = valoracion;
        for (int col = 0; col < COLUMNAS; col++) {
            if (!columnaLlena(col)) {
                valoracion = minimizar(0);
                System.out.println("Columna " + col + " con valoración: " + valoracion);
                if (valoracion < mejor_valoracion) {
                    mejor_valoracion = valoracion;
                    mejor_jugada = col;
                }
            }
        }
        return mejor_jugada;
    }

    private int maximizar(int profundidad) {
        if (esEmpate()) {
            return 0;
        } else {
            int valoracion = PEOR_VALORACION_MAX;
            int mejor_valoracion = valoracion;
            int fila;
            //log += "Profundidad: " + profundidad + "\n";
            //System.out.println("Profundidad: " + profundidad);
            for (int col = 0; col < COLUMNAS; col++) {
                if (!columnaLlena(col)) {
                    //log += "Soy min, Columna: " + col + "\n";
                    //System.out.println("Soy max, Columna: " + col);
                    fila = setFicha(col, Conecta4.PLAYER2);
                    //imprimirTablero();
                    int estado_del_juego = checkWin(fila, col);
                    valoracion = mayorValor(valoracion, minimizar(profundidad++));
                    tablero_copia[fila][col] = Conecta4.VACIO;
                    if (valoracion >= mejor_valoracion) {
                        mejor_valoracion = valoracion;
                    }
                }
            }
            return mejor_valoracion;
        }
    }

    private int minimizar(int profundidad) {
        if (esEmpate()) {
            return 0;
        } else {
            int valoracion = PEOR_VALORACION_MIN;
            int mejor_valoracion = valoracion;
            int fila;
            //log += "Profundidad: " + profundidad + "\n";
            //System.out.println("Profundidad: " + profundidad);
            for (int col = 0; col < COLUMNAS; col++) {
                if (!columnaLlena(col)) {
                    //log += "Soy min, Columna: " + col + "\n";
                    // System.out.println("Soy min, Columna: " + col);
                    fila = setFicha(col, Conecta4.PLAYER2);
                    //imprimirTablero();
                    int estado_del_juego = checkWin(fila, col);
                    valoracion = menorValor(valoracion, maximizar(profundidad++));
                    tablero_copia[fila][col] = Conecta4.VACIO;
                    if (valoracion <= mejor_valoracion) {
                        mejor_valoracion = valoracion;
                    }
                }
            }
            return mejor_valoracion;
        }
    }

    private boolean columnaLlena(int col) {
        return tablero_copia[0][col] != Conecta4.VACIO;
    }

    private int setFicha(int col, int jugador) {
        int fila = FILAS - 1;
        while ((fila >= 0) && (tablero_copia[fila][col] != Conecta4.VACIO)) {
            fila--;
        }
        tablero_copia[fila][col] = jugador;
        //  System.out.println("Pongo ficha");
        return fila;
    }

    public boolean esEmpate() {
        for (int i = 0; i < COLUMNAS; i++) {
            if (tablero_copia[0][i] == Conecta4.VACIO) {
                return false;
            }
        }
        return true;
    }

    private int menorValor(int primer, int segundo) {
        return primer < segundo ? primer : segundo;
    }

    private int mayorValor(int primer, int segundo) {
        return primer > segundo ? primer : segundo;
    }

    // Método para mostrar el estado actual del tablero por la salida estándar
    private void imprimirTablero() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                log += tablero_copia[i][j] + " ";
            }
            log += "\n";
        }
        log += "\n";
    }

    private void copiarTablero(int tablero_origen[][]) {
        tablero_copia = new int[FILAS][COLUMNAS];
        for (int i = 0; i < FILAS; i++)
            for (int j = 0; j < COLUMNAS; j++)
                tablero_copia[i][j] = tablero_origen[i][j];
    }

    private void escribirLogs() {
        FileWriter fichero = null;
        PrintWriter pw;
        try {
            fichero = new FileWriter("log.txt", true);
            pw = new PrintWriter(fichero);
            pw.println(log);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Para asegurarnos que se cierra el fichero
                if (null != fichero)
                    fichero.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public int checkWin(int x, int y) {
        /*
         *	x fila
         *	y columna
         */
        //Comprobar vertical
        int ganar1 = 0;
        int ganar2 = 0;
        int ganador = 0;
        boolean salir = false;
        for (int i = 0; (i < FILAS) && !salir; i++) {
            if (tablero_copia[i][y] != Conecta4.VACIO) {
                if (tablero_copia[i][y] == Conecta4.PLAYER1) {
                    ganar1++;
                } else {
                    ganar1 = 0;
                }
                // Gana el jugador 1
                if (ganar1 == CONECTA_N) {
                    ganador = Conecta4.PLAYER1;
                    salir = true;
                }
                if (!salir) {
                    if (tablero_copia[i][y] == Conecta4.PLAYER2) {
                        ganar2++;
                    } else {
                        ganar2 = 0;
                    }
                    // Gana el jugador 2
                    if (ganar2 == CONECTA_N) {
                        ganador = Conecta4.PLAYER2;
                        salir = true;
                    }
                }
            } else {
                ganar1 = 0;
                ganar2 = 0;
            }
        }
        // Comprobar horizontal
        ganar1 = 0;
        ganar2 = 0;
        for (int j = 0; (j < COLUMNAS) && !salir; j++) {
            if (tablero_copia[x][j] != Conecta4.VACIO) {
                if (tablero_copia[x][j] == Conecta4.PLAYER1) {
                    ganar1++;
                } else {
                    ganar1 = 0;
                }
                // Gana el jugador 1
                if (ganar1 == CONECTA_N) {
                    ganador = Conecta4.PLAYER1;
                    salir = true;
                }
                if (ganador != Conecta4.PLAYER1) {
                    if (tablero_copia[x][j] == Conecta4.PLAYER2) {
                        ganar2++;
                    } else {
                        ganar2 = 0;
                    }
                    // Gana el jugador 2
                    if (ganar2 == CONECTA_N) {
                        ganador = Conecta4.PLAYER2;
                        salir = true;
                    }
                }
            } else {
                ganar1 = 0;
                ganar2 = 0;
            }
        }
        // Comprobar oblicuo. De izquierda a derecha
        ganar1 = 0;
        ganar2 = 0;
        int a = x;
        int b = y;
        while (b > 0 && a > 0) {
            a--;
            b--;
        }
        while (b < COLUMNAS && a < FILAS && !salir) {
            if (tablero_copia[a][b] != Conecta4.VACIO) {
                if (tablero_copia[a][b] == Conecta4.PLAYER1) {
                    ganar1++;
                } else {
                    ganar1 = 0;
                }
                // Gana el jugador 1
                if (ganar1 == CONECTA_N) {
                    ganador = Conecta4.PLAYER1;
                    salir = true;
                }
                if (ganador != Conecta4.PLAYER1) {
                    if (tablero_copia[a][b] == Conecta4.PLAYER2) {
                        ganar2++;
                    } else {
                        ganar2 = 0;
                    }
                    // Gana el jugador 2
                    if (ganar2 == CONECTA_N) {
                        ganador = Conecta4.PLAYER2;
                        salir = true;
                    }
                }
            } else {
                ganar1 = 0;
                ganar2 = 0;
            }
            a++;
            b++;
        }
        // Comprobar oblicuo de derecha a izquierda
        ganar1 = 0;
        ganar2 = 0;
        a = x;
        b = y;
        //buscar posición de la esquina
        while (b < COLUMNAS - 1 && a > 0) {
            a--;
            b++;
        }
        while (b > -1 && a < FILAS && !salir) {
            if (tablero_copia[a][b] != Conecta4.VACIO) {
                if (tablero_copia[a][b] == Conecta4.PLAYER1) {
                    ganar1++;
                } else {
                    ganar1 = 0;
                }
                // Gana el jugador 1
                if (ganar1 == CONECTA_N) {
                    ganador = Conecta4.PLAYER1;
                    salir = true;
                }
                if (ganador != Conecta4.PLAYER1) {
                    if (tablero_copia[a][b] == Conecta4.PLAYER2) {
                        ganar2++;
                    } else {
                        ganar2 = 0;
                    }
                    // Gana el jugador 2
                    if (ganar2 == CONECTA_N) {
                        ganador = Conecta4.PLAYER2;
                        salir = true;
                    }
                }
            } else {
                ganar1 = 0;
                ganar2 = 0;
            }
            a++;
            b--;
        }

        return ganador;
    } // checkWin
}
