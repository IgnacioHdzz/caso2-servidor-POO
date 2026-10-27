package servidor.principal;

import java.io.IOException;
import comun.constantes.ConstantesComunes;
import servidor.constantes.Constantes;
import servidor.red.ServidorJuego;

// Punto de entrada del servidor. Se corre una sola vez y queda esperando clientes.
public class MainServidor {

    public static void main(String[] args) {
        ServidorJuego servidor = new ServidorJuego(ConstantesComunes.PUERTO);
        try {
            servidor.abrir();
            System.out.println(Constantes.TEXTO_SERVIDOR_INICIADO + ConstantesComunes.PUERTO);
            servidor.atenderConexiones();
        } catch (IOException e) {
            System.out.println(Constantes.TEXTO_ERROR_INICIO + ConstantesComunes.PUERTO + ": " + e.getMessage());
        }
}

}