package servidor.constantes;

/** Valores fijos que solo usa el servidor (las reglas del juego). */
public final class Constantes {

    private Constantes() {
    }

    // Manadas de jugador
    public static final int MIEMBROS_MANADA_JUGADOR = 10;
    public static final int ENERGIA_INICIAL_JUGADOR = 20;
    public static final int DANIO_INICIAL_JUGADOR = 2;
    public static final int ENERGIA_MAXIMA_JUGADOR = 20;

    // Manadas bot
    public static final int MIEMBROS_MANADA_BOT = 4;
    public static final int ENERGIA_INICIAL_BOT = 10;
    public static final int DANIO_INICIAL_BOT = 1;

    // Combate
    public static final double RADIO_ENCUENTRO = 60;
    public static final long ENFRIAMIENTO_ATAQUE_MS = 1000;
    public static final int BOTS_PARA_BONO = 2;
    public static final int BONO_ENERGIA = 1;

    // Movimiento (unidades del mapa por segundo)
    public static final double VELOCIDAD_ANIMAL = 80;
    public static final double SEPARACION_INICIAL = 25;

    // Tazas y banderas
    public static final int CANTIDAD_TAZAS = 10;
    public static final int AUMENTO_DANIO_TAZA = 1;
    public static final double RADIO_TAZA = 20;
    public static final double RADIO_BANDERA = 30;
    public static final int CANTIDAD_MANADAS = 7;

    // Forma del mapa: centro de cada zona (zona i = columna i % 4, fila i / 4)
    // y qué zonas se conectan con un camino. Los centros no están alineados a
    // propósito, para que los caminos midan distinto (y Dijkstra tenga sentido).
    public static final double[] CENTROS_ZONAS_X = {
        260, 940, 1450, 2150,
        330, 860, 1550, 2060,
        280, 930, 1470, 2120
    };
    public static final double[] CENTROS_ZONAS_Y = {
        170, 230, 160, 220,
        640, 560, 630, 580,
        1030, 970, 1040, 960
    };
    public static final int[][] CONEXIONES_ZONAS = {
        {0, 1}, {1, 2}, {2, 3},
        {4, 5}, {5, 6}, {6, 7},
        {8, 9}, {9, 10}, {10, 11},
        {0, 4}, {4, 8}, {1, 5}, {2, 6}, {3, 7}, {7, 11}, {6, 10},
        {5, 10}
    };

    // Códigos de partida
    public static final String CARACTERES_CODIGO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    // Textos que se envían al cliente
    public static final String TEXTO_CODIGO_INEXISTENTE = "No existe una partida con ese código.";
    public static final String TEXTO_SALA_LLENA = "Esa partida ya tiene dos jugadores.";
    public static final String TEXTO_ESPECIE_TOMADA = "Esa especie ya fue elegida.";
    public static final String TEXTO_ACCION_NO_PERMITIDA = "Esa acción no se puede hacer en este momento.";
    public static final String TEXTO_SIN_SALA = "Primero tenés que crear una partida o unirte a una.";
    public static final String TEXTO_RIVAL_ABANDONO = "Tu rival abandonó la partida.";
    public static final String TEXTO_TIPO_DESCONOCIDO = "Mensaje no reconocido.";
}
