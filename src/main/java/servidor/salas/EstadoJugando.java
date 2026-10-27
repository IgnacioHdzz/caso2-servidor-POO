package servidor.salas;

import servidor.constantes.Constantes;
import servidor.juego.comandos.Comando;
import servidor.juego.comandos.ComandoAbandonar;
import servidor.red.SesionCliente;

// El juego corriendo.
public class EstadoJugando extends EstadoSala {

    @Override
    public void unirJugador(Sala pSala, SesionCliente pSesion) {
        enviarError(pSesion, Constantes.TEXTO_SALA_LLENA);
    }

    @Override
    public void recibirComando(Sala pSala, Comando pComando) {
        // No se ejecuta aquí, se deja en la cola y lo ejecuta el hilo del motor.
        pSala.getPartida().agregarComando(pComando);
    }

    @Override
    public void alDesconectarse(Sala pSala, SesionCliente pSesion) {
        // También es un comando, el hilo del motor le da la victoria al rival y termina la partida.
        pSala.getPartida().agregarComando(new ComandoAbandonar(pSesion));
    }
}