package servidor.red;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import comun.protocolo.ConvertidorJson;
import comun.red.Conexion;
import servidor.procesadores.DespachadorMensajes;

// Acepta conexiones y crea una SesionCliente por cada una.
// Todos los hilos de las sesiones los administra el ExecutorService.

public class ServidorJuego {

    private final int puerto;
    private final ExecutorService hilos;
    private final ConvertidorJson convertidor;
    private final DespachadorMensajes despachador;
    private ServerSocket serverSocket;
    private volatile boolean corriendo;

    public ServidorJuego(int pPuerto) {
        this.puerto = pPuerto;
        this.hilos = Executors.newCachedThreadPool();
        this.convertidor = new ConvertidorJson();
        this.despachador = new DespachadorMensajes();
    }

    // Abre el puerto, falla si el puerto ya está en uso (por ejemplo, otro servidor corriendo).
    public void abrir() throws IOException {
        serverSocket = new ServerSocket(puerto);
        corriendo = true;
    }

    // Atiende conexiones hasta que se llame a detener(). 
    public void atenderConexiones() {
        while (corriendo) {
            try {
                Socket socket = serverSocket.accept();
                atenderConexion(socket);
            } catch (IOException e) {
                // accept() falla cuando detener() cierra el ServerSocket: el while termina solo.
            }
        }
    }

    public void detener() {
        corriendo = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            // Ya estaba cerrado, no hay nada más que hacer.
        }
        hilos.shutdownNow();
    }

    private void atenderConexion(Socket pSocket) throws IOException {
        Conexion conexion = new Conexion(pSocket);
        SesionCliente sesion = new SesionCliente(conexion, convertidor, despachador);
        sesion.iniciar(hilos);
    }
}