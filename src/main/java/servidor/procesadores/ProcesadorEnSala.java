package servidor.procesadores;

import comun.protocolo.Mensaje;
import servidor.constantes.Constantes;
import servidor.red.SesionCliente;
import servidor.salas.Sala;

// Base de los procesadores que solo tienen sentido dentro de una sala.
// procesar() revisa primero que haya sala y después llama a procesarEnSala(), que cada procesador define a su manera.

// Es una clase base para no tener que repetir el mismo código de revisión de sala en cada procesador.

public abstract class ProcesadorEnSala implements IProcesadorMensaje {

    @Override
    public final void procesar(Mensaje pMensaje, SesionCliente pSesion) {
        Sala sala = pSesion.getSala();
        if (sala == null) {
            pSesion.enviarError(Constantes.TEXTO_SIN_SALA);
            return;
        }
        procesarEnSala(pMensaje, pSesion, sala);
    }

    protected abstract void procesarEnSala(Mensaje pMensaje, SesionCliente pSesion, Sala pSala);
}