package servidor.juego.comandos;

import servidor.juego.Partida;
import servidor.red.SesionCliente;

// Cambiar la zona que está viendo un jugador.
public class ComandoVerZona extends Comando {

    private final int idZona;

    public ComandoVerZona(SesionCliente pSolicitante, int pIdZona) {
        super(pSolicitante);
        this.idZona = pIdZona;
    }

    @Override
    public void ejecutar(Partida pPartida) {
        servidor.juego.Jugador jugador = pPartida.getJugadorDe(getSolicitante());
        if (jugador == null || jugador.getManada() == null) return;
        for (servidor.modelo.Animal animal : jugador.getManada().getMiembros()) {
            if (animal.estaVivo() && animal.getIdZona() == idZona) { jugador.verZona(idZona); return; }
        }
    }
}
