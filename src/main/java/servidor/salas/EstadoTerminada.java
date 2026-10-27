package servidor.salas;

import servidor.juego.comandos.Comando;
import servidor.red.SesionCliente;

/** La partida terminó. */
public class EstadoTerminada extends EstadoSala {

    @Override
    public void recibirComando(Sala pSala, Comando pComando) {
        // Se ignora, pudo llegar justo cuando terminaba la partida.
    }

    @Override
    public void alDesconectarse(Sala pSala, SesionCliente pSesion) {
        // No hay nada que hacer, la sala ya se cerró.
    }
}
