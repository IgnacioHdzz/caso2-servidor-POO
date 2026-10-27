package servidor;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import comun.constantes.ConstantesComunes;
import comun.modelo.Especie;
import comun.protocolo.ConvertidorJson;
import comun.protocolo.Mensaje;
import comun.protocolo.TipoMensaje;
import comun.protocolo.datos.DatosCrearPartida;
import comun.protocolo.datos.DatosMoverAnimales;
import comun.protocolo.datos.DatosSeleccionarEspecie;
import comun.protocolo.datos.DatosUnirsePartida;
import comun.red.Conexion;
import comun.red.IReceptorMensajes;
import comun.red.LectorMensajes;

/**
 * Herramienta de prueba (no es parte del juego): un cliente de texto para probar el
 * servidor sin ventana. Lo que se escribe se envía como mensaje, y lo que llega se imprime.
 */
public class ClienteConsola implements IReceptorMensajes {

    private final ConvertidorJson convertidor = new ConvertidorJson();

    public static void main(String[] args) throws IOException {
        new ClienteConsola().correr();
    }

    private void correr() throws IOException {
        Conexion conexion = new Conexion(new Socket(ConstantesComunes.HOST, ConstantesComunes.PUERTO));
        new Thread(new LectorMensajes(conexion, convertidor, this)).start();
        System.out.println("Comandos: crear <nick> | unir <nick> <codigo> | especie <ESPECIE> | mover <id> <x> <y> | salir");

        Scanner teclado = new Scanner(System.in);
        String linea = teclado.nextLine().trim();
        while (!linea.equals("salir")) {
            try {
                conexion.escribirLinea(convertidor.aJson(traducir(linea.split(" "))));
            } catch (RuntimeException e) {
                System.out.println("Comando inválido");
            }
            linea = teclado.nextLine().trim();
        }
        conexion.cerrar();
    }

    private Mensaje traducir(String[] pPartes) {
        if (pPartes[0].equals("crear")) {
            return new Mensaje(TipoMensaje.CREATE_MATCH, new DatosCrearPartida(pPartes[1]));
        }
        if (pPartes[0].equals("unir")) {
            return new Mensaje(TipoMensaje.JOIN_MATCH, new DatosUnirsePartida(pPartes[1], pPartes[2]));
        }
        if (pPartes[0].equals("especie")) {
            return new Mensaje(TipoMensaje.SELECT_ANIMAL, new DatosSeleccionarEspecie(Especie.valueOf(pPartes[1])));
        }
        if (pPartes[0].equals("mover")) {
            return new Mensaje(TipoMensaje.MOVE_ANIMALS, new DatosMoverAnimales(
                    List.of(Integer.parseInt(pPartes[1])), Double.parseDouble(pPartes[2]), Double.parseDouble(pPartes[3])));
        }
        throw new IllegalArgumentException(pPartes[0]);
    }

    @Override
    public void recibir(Mensaje pMensaje) {
        System.out.println("<< " + convertidor.aJson(pMensaje));
    }

    @Override
    public void alDesconectarse() {
        System.out.println("<< (conexión cerrada)");
    }
}