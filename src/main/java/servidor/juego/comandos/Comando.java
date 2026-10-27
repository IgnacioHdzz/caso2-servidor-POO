package servidor.juego.comandos;

import servidor.juego.Partida;
import servidor.red.SesionCliente;

// Patrón Command: una orden de un jugador que queda pendiente en la cola
// de la partida hasta que el hilo de la partida la ejecuta.
public abstract class Comando {

    private final SesionCliente solicitante;

    protected Comando(SesionCliente pSolicitante) {
        this.solicitante = pSolicitante;
    }

    public SesionCliente getSolicitante() {
        return solicitante;
    }

    public abstract void ejecutar(Partida pPartida);
}
