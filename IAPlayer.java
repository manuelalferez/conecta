package conecta4;

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

    // Tablero usado para construir el árbol
    private int tablero_copia[][];
    private boolean ES_EMPATE = false;

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
        int valoracion = Integer.MAX_VALUE;
        int mejor_valoracion = valoracion;
        for (int col = 0; col < COLUMNAS; col++) {
            if (!columnaLlena(col)) {
                int fila = setFicha(col, Conecta4.PLAYER2);
                int estado_del_juego = checkWin(fila, col);
                valoracion = maximizar(estado_del_juego, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
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
     * @param estado_del_juego Estado del juego
     * @param profundidad      Profundidad del nodo
     * @param alfa             La mayor heurística que PLAYER1 escogerá
     * @param beta             La menor heurística que PLAYER2 escogerá
     * @return Puesto que esta función será llamada desde algoritmoMinMax y minimizar, devolverá la mejor evaluación
     * para el estado en el que se encuentra el tablero
     */
    private int maximizar(int estado_del_juego, int profundidad, int alfa, int beta) {
        if (estado_del_juego != 0 || esEmpate() || profundidad > MAX_PROFUNDIDAD) {
            return getEstadoJuego(estado_del_juego);
        } else {
            for (int col = 0; col < COLUMNAS; col++) {
                if (!columnaLlena(col)) {
                    int fila = setFicha(col, Conecta4.PLAYER1);
                    estado_del_juego = checkWin(fila, col);
                    alfa = Math.max(alfa, minimizar(estado_del_juego, profundidad++, alfa, beta));
                    tablero_copia[fila][col] = Conecta4.VACIO;
                    if (alfa >= beta) {
                        return alfa;
                    }
                }
            }
            return alfa;
        }
    }

    /**
     * Elige el mejor movimiento para min (jugador IA)
     *
     * @param estado_del_juego Estado del juego
     * @param profundidad      Profundidad del nodo
     * @param alfa             La mayor heurística que PLAYER1 escogerá
     * @param beta             La menor heurística que PLAYER2 escogerá
     * @return Puesto que esta función será llamada desde maximizar, devolverá la mejor evaluación
     * para el estado en el que se encuentra el tablero
     */
    private int minimizar(int estado_del_juego, int profundidad, int alfa, int beta) {
        if (estado_del_juego != 0 || esEmpate() || profundidad > MAX_PROFUNDIDAD) {
            return getEstadoJuego(estado_del_juego);
        } else {
            for (int col = 0; col < COLUMNAS; col++) {
                if (!columnaLlena(col)) {
                    int fila = setFicha(col, Conecta4.PLAYER2);
                    estado_del_juego = checkWin(fila, col);
                    beta = Math.min(beta, maximizar(estado_del_juego, profundidad++, alfa, beta));
                    tablero_copia[fila][col] = Conecta4.VACIO;
                    if (beta <= alfa) {
                        return beta;
                    }
                }
            }
            return beta;
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

    /**
     * Devuelve una heurística para el tablero_copia. Hay 4 casos:
     * - Si el tablero se encuentra en un nodo hoja (un estado final), devuelve una heurística asociada
     * dependiendo si ha ganador PLAYER1 (10000), PLAYER2 (-10000) o se ha empatado (0)
     * - El último caso se da cuando la partida no tiene un claro ganador. En este caso se devuelve una
     * heurística en función del número de fichas consecutivas con posibles CONECTA N (si se puede llegar
     * a una victoria por alguna de las partes). Esa heurística se calcula en función:
     * 10^(número de fichas consecutivas, o no, pero con posibilidad de unirse en un CONECTA N)
     *
     * @param estado_del_juego Estado actual del juego
     * @return La heurística del juego, asociada a la posición actual del tablero_copia
     */
    private int getEstadoJuego(int estado_del_juego) {
        if (estado_del_juego == Conecta4.PLAYER1) {
            return (int) Math.pow(10, CONECTA_N);
        } else if (estado_del_juego == Conecta4.PLAYER2) {
            return -(int) Math.pow(10, CONECTA_N);
        } else if (ES_EMPATE) {
            return 0;
        } else {
            int heuristica_juego = 0;
            heuristica_juego += getHeuristica(Conecta4.PLAYER1);
            heuristica_juego -= getHeuristica(Conecta4.PLAYER2);
            return heuristica_juego;
        }
    }

    /**
     * Suma las heurísticas, calculadas por separado, de cada una de las variantes
     * - Horizontal
     * - Vertical
     * - Diagonal positiva, que va desde la esquina inferior izquierda hasta la superior derecha
     * - Diagonal negativa, que va desde la esquina inferior derecha hasta la superior izquierda
     *
     * @param jugador A quién se le está hallando la heurística
     * @return La heurística asociada al tablero_copia, para el jugador correspondiente
     */
    private int getHeuristica(int jugador) {
        int heuristica = 0;
        heuristica += getHeuristicaHorizontal(jugador);
        heuristica += getHeuristicaVertical(jugador);
        heuristica += getHeuristicaDiagonalPositiva(jugador);
        heuristica += getHeuristicaDiagonalNegativa(jugador);
        return heuristica;
    }

    private int getHeuristicaHorizontal(int jugador) {
        int heuristica = 0;
        for (int fila = FILAS - 1; fila >= 0; fila--) {
            int conecta = 0;
            int max_fichas = 0;
            int fichas = 0;
            int caducidad = 0;
            for (int col = 0; col < COLUMNAS; col++) {
                if (tablero_copia[fila][col] == jugador) {
                    if (fichas == 0) {
                        caducidad = CONECTA_N - 1;
                    }
                    fichas++;
                    conecta++;
                } else if (tablero_copia[fila][col] == Conecta4.VACIO) {
                    conecta++;
                } else {
                    conecta = 0;
                    fichas = 0;
                }
                if (conecta >= CONECTA_N && fichas > max_fichas) {
                    max_fichas = fichas;
                }
                if (caducidad == 0 && fichas != 0) {
                    fichas--;
                } else if (fichas >= 1) {
                    caducidad--;
                }
            }
            if (max_fichas != 0) {
                heuristica += (int) Math.pow(10, max_fichas);
            }
        }
        return heuristica;
    }

    private int getHeuristicaVertical(int jugador) {
        int heuristica = 0;
        for (int col = 0; col < COLUMNAS; col++) {
            int conecta = 0;
            int fichas = 0;
            for (int fila = FILAS - 1; fila >= 0; fila--) {
                if (tablero_copia[fila][col] == jugador) {
                    fichas++;
                    conecta++;
                } else if (tablero_copia[fila][col] == Conecta4.VACIO) {
                    conecta++;
                } else {
                    conecta = 0;
                    fichas = 0;
                }

                if (conecta == CONECTA_N && fichas > 0) {
                    heuristica += (int) Math.pow(10, fichas);
                    break;
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
            int max_fichas = 0;
            int fichas = 0;
            int caducidad = 0;
            do {
                if (tablero_copia[a][b] == jugador) {
                    if (fichas == 0) {
                        caducidad = CONECTA_N - 1;
                    }
                    fichas++;
                    conecta++;
                } else if (tablero_copia[a][b] == Conecta4.VACIO) {
                    conecta++;
                } else {
                    conecta = 0;
                    fichas = 0;
                }
                if (conecta >= CONECTA_N && fichas > max_fichas) {
                    max_fichas = fichas;
                }
                if (caducidad == 0 && fichas != 0) {
                    fichas--;
                } else if (fichas >= 1) {
                    caducidad--;
                }
                a--;
                b++;
            } while (a >= 0 && b < COLUMNAS);

            if (max_fichas != 0) {
                heuristica += (int) Math.pow(10, max_fichas);
            }

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
            int max_fichas = 0;
            int fichas = 0;
            int caducidad = 0;
            do {
                if (tablero_copia[a][b] == jugador) {
                    if (fichas == 0) {
                        caducidad = CONECTA_N - 1;
                    }
                    fichas++;
                    conecta++;
                } else if (tablero_copia[a][b] == Conecta4.VACIO) {
                    conecta++;
                } else {
                    conecta = 0;
                    fichas = 0;
                }
                if (conecta >= CONECTA_N && fichas > max_fichas) {
                    max_fichas = fichas;
                }
                if (caducidad == 0 && fichas != 0) {
                    fichas--;
                } else if (fichas >= 1) {
                    caducidad--;
                }

                a--;
                b--;
            } while (a >= 0 && b >= 0);

            if (max_fichas != 0) {
                heuristica += (int) Math.pow(10, max_fichas);
            }

            if (col < COLUMNAS - 1)
                col++;
            else
                fila--;
        } while (fila >= lim_fil);
        return heuristica;
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
