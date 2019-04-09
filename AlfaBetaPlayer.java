/*
 * @author Manuel Alférez Ruiz
 * @date 27 de abril de 2018, 9:40
 * @note Inteligencia Artificial. 2º Curso. Grado en Ingeniería Informática
 */
package conectan;

/**
 * Clase AlfaBetaPlayer para representar al jugador CPU que usa la poda Alfa
 * Beta
 */
public class AlfaBetaPlayer extends Player {

    private int filas; //<Número de filas del Tablero
    private int columnas; //<Número de columnas del Tablero 
    private int connects; //<Número de fichas que han de alinearse para ganar

    private int profundidadMaxima; //<Profundidad máxima establecida

    /**
     *
     * @param tablero Representación del tablero de juego
     * @param conecta Número de fichas consecutivas para ganar
     * @return Jugador ganador (si lo hay)
     */
    @Override
    public int jugada(Grid tablero, int conecta) {

        // Inicialización de valores
        filas = tablero.getFilas();
        columnas = tablero.getColumnas();
        connects = conecta;

        profundidadMaxima = conecta;

        // Calcular la mejor columna posible donde hacer nuestra jugada
        int mejorColumna = minimax(tablero.toArray());
        return tablero.checkWin(tablero.setButton(mejorColumna, ConectaN.JUGADOR2), mejorColumna, conecta);
    } // jugada

    /**
     * @brief Algoritmo minimax
     * @param tab El estado actual de tablero
     * @return Devuelve la mejor columna donde poner ficha
     */
    private int minimax(int tab[][]) {
        int mejorColumna = -1; //Mejor columna donde poner ficha

        int max, aux, posFila, util;
        max = Integer.MIN_VALUE;
        
        for (int i = 0; i < columnas; i++) {
            if (!columnaLlena(tab, i)) {
                posFila = ponerFicha(tab, i, ConectaN.JUGADOR2);
                util = valorUtilidad(tab, posFila, i);
                aux = min_valor(tab, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, util);
                tab[posFila][i] = 0;
                if (aux > max) {
                    max = aux;
                    mejorColumna = i;
                }
            }
        }
        return mejorColumna;
    } // minimax

    /**
     * @brief Haya un valor de utilidad maximizando la función de utilidad
     * @param tab Representación del tablero de juego
     * @param max Valor de la mejor alternativa para max a lo largo del camino
     * @param min Valor de la mejor alternativa para min a lo largo del camino
     * @param prof Profundidad en el árbol
     * @param util Valor de utilidad que informa sobre el estado de la partida:
     * 1 si gana el JUGADOR1, -1 si gana el JUGADOR2 y 0 si nadie gana
     * @return Devuelve un valor máximo de utilidad
     */
    public int max_valor(int tab[][], int max, int min, int prof, int util) {
        //Si el valor de utilidad es final 
        if (util != 0 || prof > profundidadMaxima || esEmpate(tab)) {
            return valor(tab, util);
        } else {

            int v = Integer.MIN_VALUE; //Valor escogido por max
            int posFila; // Fila donde se queda la ficha tras insertarla

            for (int i = 0; i < columnas; i++) { //Recorremos todas las columnas
                if (!columnaLlena(tab, i)) { //Si la columna i no esta llena
                    posFila = ponerFicha(tab, i, ConectaN.JUGADOR2);
                    util = valorUtilidad(tab, posFila, i);
                    v = MAX(v, min_valor(tab, max, min, prof + 1, util));                   
                    max = MAX(v, max);                  
                    tab[posFila][i] = 0; //Eliminamos la casilla colocada
                    if (v >= min) {
                        return v;
                    }
                }
            }
            return v;
        }
    } // max_valor

    /**
     * @brief Haya un valor de utilidad minimizando la función de utilidad
     * @param tab Representación del tablero de juego
     * @param max Valor de la mejor alternativa para max a lo largo del camino
     * @param min Valor de la mejor alternativa para min a lo largo del camino
     * @param prof Profundidad en el árbol
     * @param util Valor de utilidad que informa sobre el estado de la partida:
     * 1 si gana el JUGADOR1, -1 si gana el JUGADOR2 y 0 si nadie gana
     * @return Devuelve el valor mínimo de utilidad
     */
    public int min_valor(int tab[][], int max, int min, int prof, int util) {
        //Si el valor de utilidad es final 
        if (util != 0 || prof > profundidadMaxima || esEmpate(tab)) {
            return valor(tab, util);
        } else {

            int v = Integer.MAX_VALUE; //Valor escogido por max
            int posFila; // Fila donde se queda la ficha tras insertarla

            for (int i = 0; i < columnas; i++) { //Recorremos todas las columnas
                if (!columnaLlena(tab, i)) { //Si la columna i no esta llena
                    posFila = ponerFicha(tab, i, ConectaN.JUGADOR1);
                    util = valorUtilidad(tab, posFila, i);
                    v = MIN(v, max_valor(tab, max, min, prof + 1, util));
                    min = MIN(v, min); 
                    tab[posFila][i] = 0; // Eliminamos la ficha colocada
                    if (max >= v) {
                        return v;
                    }
                }
            }
            return v;
        }
    } // min_valor

    /**
     * @brief Devuelve el entero que es mayor
     * @param x Un valor de tipo entero
     * @param y Un valor de tipo entero
     * @return El entero mayor entre x e y
     */
    public int MAX(int x, int y) {
        if (x > y) {
            return x;
        } else {
            return y;
        }
    } // MAX

    /**
     * @brief Devuelve el entero que es menor
     * @param x Un valor de tipo entero
     * @param y Un valor de tipo entero
     * @return El entero menor entre x e y
     */
    public int MIN(int x, int y) {
        if (x > y) {
            return y;
        } else {
            return x;
        }
    } // MIN

    /**
     * @brief Comprueba si una columna esta completa
     * @param tab Tablero de juego representado como matriz de enteros
     * @param col Columna a comprobar
     * @return True si esta completa y false en otro caso
     */
    public boolean columnaLlena(int tab[][], int col) {
        return tab[0][col] != 0;
    } // ColumnaLlena

    /**
     * @brief Coloca una ficha en la columna col
     * @param tab Tablero de juego representado como matriz de enteros
     * @param col Columna donde insertar la ficha
     * @param jugador Representación numérica del jugador que pone la ficha
     * @return Devuelve la fila donde se inserto la ficha
     */
    public int ponerFicha(int tab[][], int col, int jugador) {
        int y = filas - 1;
        //Buscamos la fila donde colocar la pieza
        while ((y >= 0) && (tab[y][col] != 0)) {
            y--;
        }
        tab[y][col] = jugador; //Colocamos la ficha
        return y;
    } // ponerFicha

    /**
     * @brief Determina si se ha llegado a empate
     * @param tab Tablero de juego representado como matriz de enteros
     * @return True si se ha llegado a empate y false en caso contrario
     */
    public boolean esEmpate(int tab[][]) {
        for (int i = 0; i < columnas; i++) {
            if (tab[0][i] == 0) {
                return false;
            }
        }
        return true;
    } // esEmpate

    /**
     * @brief Calcula el valor de la partida
     * @param tab Tablero de juego representado como matriz de enteros
     * @param util Valorde utilidad: 1 si gana el JUGADOR1, -1 si gana el
     * JUGADOR2 y 0 en otro caso
     * @return Valor de utilidad (más preciso que el devuelto por la función
     * valorUtilidad)
     */
    public int valor(int tab[][], int util) {
        switch (util) {
            case ConectaN.JUGADOR1:
                return -2;
            case ConectaN.JUGADOR2:
                return 2;
            default:
                //La partida aún no terminó
                return valorParcial(tab, ConectaN.JUGADOR2) - valorParcial(tab, ConectaN.JUGADOR1);
        }
    } // valor

    /**
     * @brief Calcula un valor de utilidad para los posibles movimientos del
     * jugador
     * @param tab Tablero de juego representado como matriz de enteros
     * @param jugador Representación numérica del jugador que pone la ficha
     * @return Valor de utilidad parcial
     */
    public int valorParcial(int tab[][], int jugador) {
        int x = 0;
        int fila;
        int util;
        for (int i = 0; i < columnas; i++) {
            if (!columnaLlena(tab, i)) {
                fila = ponerFicha(tab, i, jugador);
                util = valorUtilidad(tab, fila, i);
                if (util == jugador) {
                    x += 5;
                }
                tab[fila][i] = 0;
            }
        }
        return x;
    } // valorParcial

    /**
     * @brief Comprobar si el tablero se halla en un estado de fin de partida, a
     * partir de la última jugada realizada
     * @param x Fila
     * @param y Columna
     * @param tab Tablero de juego representado como matriz de enteros
     * @return Devuelve un valor de utilidad: 1 si gana el JUGADOR1, -1 si gana
     * el JUGADOR2 y 0 en otro caso
     */
    public int valorUtilidad(int tab[][], int x, int y) {
        //Comprobar vertical
        int ganar1 = 0;
        int ganar2 = 0;
        int ganador = 0;
        boolean salir = false;
        for (int i = 0; (i < filas) && !salir; i++) {
            if (tab[i][y] != ConectaN.VACIO) {
                if (tab[i][y] == ConectaN.JUGADOR1) {
                    ganar1++;
                } else {
                    ganar1 = 0;
                }
                // Gana el jugador 1
                if (ganar1 == connects) {
                    ganador = ConectaN.JUGADOR1;
                    salir = true;
                }
                if (!salir) {
                    if (tab[i][y] == ConectaN.JUGADOR2) {
                        ganar2++;
                    } else {
                        ganar2 = 0;
                    }
                    // Gana el jugador 2
                    if (ganar2 == connects) {
                        ganador = ConectaN.JUGADOR2;
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
        for (int j = 0; (j < columnas) && !salir; j++) {
            if (tab[x][j] != ConectaN.VACIO) {
                if (tab[x][j] == ConectaN.JUGADOR1) {
                    ganar1++;
                } else {
                    ganar1 = 0;
                }
                // Gana el jugador 1
                if (ganar1 == connects) {
                    ganador = ConectaN.JUGADOR1;
                    salir = true;
                }
                if (ganador != ConectaN.JUGADOR1) {
                    if (tab[x][j] == ConectaN.JUGADOR2) {
                        ganar2++;
                    } else {
                        ganar2 = 0;
                    }
                    // Gana el jugador 2
                    if (ganar2 == connects) {
                        ganador = ConectaN.JUGADOR2;
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
        while (b < columnas && a < filas && !salir) {
            if (tab[a][b] != ConectaN.VACIO) {
                if (tab[a][b] == ConectaN.JUGADOR1) {
                    ganar1++;
                } else {
                    ganar1 = 0;
                }
                // Gana el jugador 1
                if (ganar1 == connects) {
                    ganador = ConectaN.JUGADOR1;
                    salir = true;
                }
                if (ganador != ConectaN.JUGADOR1) {
                    if (tab[a][b] == ConectaN.JUGADOR2) {
                        ganar2++;
                    } else {
                        ganar2 = 0;
                    }
                    // Gana el jugador 2
                    if (ganar2 == connects) {
                        ganador = ConectaN.JUGADOR2;
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
        while (b < columnas - 1 && a > 0) {
            a--;
            b++;
        }
        while (b > -1 && a < filas && !salir) {
            if (tab[a][b] != ConectaN.VACIO) {
                if (tab[a][b] == ConectaN.JUGADOR1) {
                    ganar1++;
                } else {
                    ganar1 = 0;
                }
                // Gana el jugador 1
                if (ganar1 == connects) {
                    ganador = ConectaN.JUGADOR1;
                    salir = true;
                }
                if (ganador != ConectaN.JUGADOR1) {
                    if (tab[a][b] == ConectaN.JUGADOR2) {
                        ganar2++;
                    } else {
                        ganar2 = 0;
                    }
                    // Gana el jugador 2
                    if (ganar2 == connects) {
                        ganador = ConectaN.JUGADOR2;
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
    } // valorUtilidad

} // AlfaBetaPlayer