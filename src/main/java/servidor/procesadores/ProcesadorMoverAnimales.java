package servidor.procesadores;

import comun.protocolo.Mensaje;
import comun.modelo.Posicion;
import comun.protocolo.datos.DatosMoverAnimales;
import servidor.juego.comandos.ComandoMover;
import servidor.red.SesionCliente;
import servidor.salas.Sala;

// MOVE_ANIMALS convierte el mensaje en un ComandoMover. No mueve nada.
public class ProcesadorMoverAnimales extends ProcesadorEnSala {

    @Override
    protected void procesarEnSala(Mensaje pMensaje, SesionCliente pSesion, Sala pSala) {
        DatosMoverAnimales datos = pMensaje.getDatos(DatosMoverAnimales.class);
        Posicion destino = new Posicion(datos.getDestinoX(), datos.getDestinoY());
        pSala.recibirComando(new ComandoMover(pSesion, datos.getIdsAnimales(), destino));
    }
}