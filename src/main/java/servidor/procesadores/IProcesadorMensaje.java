package servidor.procesadores;

import comun.protocolo.Mensaje;
import servidor.red.SesionCliente;

/** Sabe procesar un tipo de mensaje que llega de un cliente. */
public interface IProcesadorMensaje {

    void procesar(Mensaje pMensaje, SesionCliente pSesion);
}
