package servidor.juego.comandos;

import java.util.List;
import comun.modelo.Posicion;
import servidor.juego.Partida;
import servidor.red.SesionCliente;

// Mover animales hacia un punto.
public class ComandoMover extends Comando {

    private final List<Integer> idsAnimales;
    private final Posicion destino;

    public ComandoMover(SesionCliente pSolicitante, List<Integer> pIdsAnimales, Posicion pDestino) {
        super(pSolicitante);
        this.idsAnimales = pIdsAnimales;
        this.destino = pDestino;
    }

    @Override
    public void ejecutar(Partida pPartida) {
        servidor.juego.Jugador jugador = pPartida.getJugadorDe(getSolicitante());
        if (jugador == null || jugador.getManada() == null || destino == null) return;
        Posicion destinoEnCamino = pPartida.getBuscadorRutas().puntoMasCercanoEnCaminos(pPartida.getMapa(), destino);
        for (Integer id : idsAnimales) {
            if (id == null) continue;
            servidor.modelo.Animal animal = pPartida.buscarAnimal(id);
            if (animal == null || !animal.estaVivo() || animal.getManada() != jugador.getManada()) continue;
            servidor.mapa.Ruta ruta = pPartida.getBuscadorRutas().calcularRuta(
                pPartida.getMapa(), animal.getPosicion(), destinoEnCamino);
            if (!ruta.haTerminado()) animal.asignarRuta(ruta);
        }
    }
}
