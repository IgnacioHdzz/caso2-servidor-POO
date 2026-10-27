package servidor.juego.comandos;

import servidor.juego.Partida;
import servidor.red.SesionCliente;

// Un jugador se desconectó durante el juego: gana el rival.
public class ComandoAbandonar extends Comando {

    public ComandoAbandonar(SesionCliente pSolicitante) {
        super(pSolicitante);
    }

    @Override
    public void ejecutar(Partida pPartida) {
        servidor.juego.Jugador jugador = pPartida.getJugadorDe(getSolicitante());
        if (jugador != null) pPartida.declararGanador(pPartida.getRival(jugador));
    }
}
