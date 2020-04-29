package conecta4;

import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Esta clase representa la inteligencia artificial cuyo objetivo es ganar a su adversario humano.
 * IAPlayer realizará aquellos movimiento con menor valor.
 */
public class IAPlayer extends Player {
    // Constantes
    private final int MAX_PROFUNDIDAD = 2;
    private final int SIN_JUGADA = -1;
    private int CONECTA_N = 0;
    private int FILAS;
    private int COLUMNAS;
    private boolean ES_EMPATE = false;
    private final int FICHA_PROVISIONAL = 2;
    private final static String VACIAR = "";

    private final int PEOR_VALORACION_MIN = Integer.MAX_VALUE;
    private final int PEOR_VALORACION_MAX = Integer.MIN_VALUE;

    // Tablero usado para construir el árbol
    private int tablero_copia[][];
    private int tablero_heuristico[][];
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
        tablero_copia = copiarTablero(tablero.toArray());
        System.out.println("============================");
        System.out.println("\nTablero actual:\n");
        imprimirTablero();
        System.out.println("============================");
        int mejorJugada = algoritmoMinMax();
        System.out.println("============================");
        System.out.println("Jugada en: " + mejorJugada);
        System.out.println("============================\n");
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
                int fila = setFicha(col, Conecta4.PLAYER2);
                int estado_del_juego = checkWin(fila, col);
                valoracion = maximizar(0, estado_del_juego);
                tablero_copia[fila][col] = Conecta4.VACIO;
                System.out.println("Columna " + col + ", con valoración: " + valoracion);
                if (valoracion < mejor_valoracion) {
                    mejor_valoracion = valoracion;
                    mejor_jugada = col;
                }
            }
        }
        return mejor_jugada;
    }

    /**
     * Elige el mejor movimiento para max (jugador humano)
     *
     * @param profundidad      Profundidad del nodo
     * @param estado_del_juego Estado del juego
     * @return Puesto que esta función será llamada desde algoritmoMinMax y minimizar, devolverá la mejor evaluación
     * para el estado en el que se encuentra el tablero
     */
    private int maximizar(int profundidad, int estado_del_juego) {
        if (estado_del_juego != 0 || esEmpate() || profundidad > MAX_PROFUNDIDAD) {
            return getEstadoJuego(estado_del_juego);
        } else {
            int valoracion = PEOR_VALORACION_MAX;
            int mejor_valoracion = valoracion;
            int fila;
            //System.out.println("Profundidad: " + profundidad + "\n");
            for (int col = 0; col < COLUMNAS; col++) {
                if (!columnaLlena(col)) {
                    //System.out.println("Soy max, Columna: " + col + "\n");
                    fila = setFicha(col, Conecta4.PLAYER1);
                    //imprimirTablero();
                    estado_del_juego = checkWin(fila, col);
                    valoracion = Math.max(valoracion, minimizar(profundidad++, estado_del_juego));
                    tablero_copia[fila][col] = Conecta4.VACIO;
                    if (valoracion >= mejor_valoracion) {
                        mejor_valoracion = valoracion;
                    }
                }
            }
            return mejor_valoracion;
        }
    }

    /**
     * Elige el mejor movimiento para min (jugador IA)
     *
     * @param profundidad      Profundidad del nodo
     * @param estado_del_juego Estado del juego
     * @return Puesto que esta función será llamada desde maximizar, devolverá la mejor evaluación
     * para el estado en el que se encuentra el tablero
     */
    private int minimizar(int profundidad, int estado_del_juego) {
        if (estado_del_juego != 0 || esEmpate() || profundidad > MAX_PROFUNDIDAD) {
            return getEstadoJuego(estado_del_juego);
        } else {
            int valoracion = PEOR_VALORACION_MIN;
            int mejor_valoracion = valoracion;
            int fila;
            //System.out.println("Profundidad: " + profundidad + "\n");
            for (int col = 0; col < COLUMNAS; col++) {
                if (!columnaLlena(col)) {
                    //System.out.println("Soy min, Columna: " + col + "\n");
                    fila = setFicha(col, Conecta4.PLAYER2);
                    //imprimirTablero();
                    estado_del_juego = checkWin(fila, col);
                    valoracion = Math.min(valoracion, maximizar(profundidad++, estado_del_juego));
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
        return fila;
    }

    public boolean esEmpate() {
        for (int i = 0; i < COLUMNAS; i++) {
            if (tablero_copia[0][i] == Conecta4.VACIO) {
                ES_EMPATE = false;
                return false;
            }
        }
        ES_EMPATE = true;
        return true;
    }

    private void imprimirTablero() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                System.out.print(tablero_copia[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private int[][] copiarTablero(int tablero_origen[][]) {
        int[][] tablero_destino = new int[FILAS][COLUMNAS];
        for (int i = 0; i < FILAS; i++)
            for (int j = 0; j < COLUMNAS; j++)
                tablero_destino[i][j] = tablero_origen[i][j];
        return tablero_destino;
    }

    private int getEstadoJuego(int estado_del_juego) {
        if (estado_del_juego == Conecta4.PLAYER1)
            return 200+((int) Math.pow(10, 3));
        else if (estado_del_juego == Conecta4.PLAYER2)
            return 200+((int) Math.pow(-10, 3));
        else if (ES_EMPATE) {
            return 0;
        } else {
            int heuristica_juego = 0;
            heuristica_juego += getHeuristica(Conecta4.PLAYER1);
            heuristica_juego += getHeuristica(Conecta4.PLAYER2);
            return heuristica_juego;
        }
    }

    private int getHeuristica(int jugador) {
        int heuristica = 0;
        tablero_heuristico = copiarTablero(tablero_copia);
        rellenarTableroHeuristico();
        heuristica += getHeuristicaHorizontal(jugador);
        heuristica += getHeuristicaVertical(jugador);
        heuristica += getHeuristicaDiagonalPositiva(jugador);
        heuristica += getHeuristicaDiagonalNegativa(jugador);
        return heuristica;
    }

    private void rellenarTableroHeuristico() {
        for (int i = 0; i < FILAS; i++)
            for (int j = 0; j < COLUMNAS; j++)
                if (tablero_heuristico[i][j] == Conecta4.VACIO)
                    tablero_heuristico[i][j] = FICHA_PROVISIONAL;
    }

    private int getHeuristicaHorizontal(int jugador) {
        int heuristica = 0;
        for (int fila = 0; fila < FILAS; fila++) {
            int conecta = 0;
            int fichas = 0;
            for (int col = 0; col < COLUMNAS; col++) {
                if (conecta == CONECTA_N) {
                    heuristica += (int) Math.pow(10, fichas);
                    break;
                } else {
                    if (tablero_heuristico[fila][col] == jugador) {
                        fichas++;
                        conecta++;
                    } else if (tablero_heuristico[fila][col] == FICHA_PROVISIONAL) {
                        conecta++;
                    } else {
                        conecta = 0;
                        fichas = 0;
                    }
                }
            }
        }
        return heuristica;
    }

    private int getHeuristicaVertical(int jugador) {
        int heuristica = 0;
        for (int col = 0; col < COLUMNAS; col++) {
            int conecta = 0;
            int fichas = 0;
            for (int fila = 0; fila < FILAS; fila++) {
                if (conecta == CONECTA_N) {
                    heuristica += (int) Math.pow(10, fichas);
                    break;
                } else {
                    if (tablero_heuristico[fila][col] == jugador) {
                        fichas++;
                        conecta++;
                    } else if (tablero_heuristico[fila][col] == FICHA_PROVISIONAL) {
                        conecta++;
                    } else {
                        conecta = 0;
                        fichas = 0;
                    }
                }
            }
        }
        return heuristica;
    }

    private int getHeuristicaDiagonalPositiva(int jugador) {
        int heuristica = 0;
        int fila = CONECTA_N - 1; // N - 1
        int lim_col = (COLUMNAS - 1) - (CONECTA_N - 1); // COLUMNAS - (N-1)
        int col = 0;
        do {
            int a = fila;
            int b = col;
            int conecta = 0;
            int fichas = 0;
            do {
                if (tablero_heuristico[a][b] == jugador) {
                    fichas++;
                    conecta++;
                } else if (tablero_heuristico[a][b] == FICHA_PROVISIONAL) {
                    conecta++;
                } else {
                    conecta = 0;
                    fichas = 0;
                }
                a--;
                b++;
                if (conecta == CONECTA_N) {
                    heuristica += (int) Math.pow(10, fichas);
                    break;
                }
            } while (a >= 0 && b < COLUMNAS);

            if (fila < FILAS - 1)
                fila++;
            else
                col++;
        } while (col <= lim_col);
        return heuristica;
    }

    private int getHeuristicaDiagonalNegativa(int jugador) {
        int heuristica = 0;
        int fila = FILAS - 1;
        int col = CONECTA_N - 1; // N - 1
        int lim_fil = CONECTA_N - 1; // N - 1
        do {
            int a = fila;
            int b = col;
            int conecta = 0;
            int fichas = 0;
            do {
                if (tablero_heuristico[a][b] == jugador) {
                    fichas++;
                    conecta++;
                } else if (tablero_heuristico[a][b] == FICHA_PROVISIONAL) {
                    conecta++;
                } else {
                    conecta = 0;
                    fichas = 0;
                }

                a--;
                b--;
                if (conecta == CONECTA_N) {
                    heuristica += (int) Math.pow(10, fichas);
                    break;
                }
            } while (a >= 0 && b >= 0);

            if (col < COLUMNAS - 1)
                col++;
            else
                fila--;
        } while (fila >= lim_fil);
        return heuristica;
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
    }
}
