package servidor.juego.comandos;

import java.util.List;
import servidor.juego.Partida;
import servidor.red.SesionCliente;

// Ordenar a unos animales que ataquen a un objetivo.
public class ComandoAtacar extends Comando {

    private final List<Integer> idsAnimales;
    private final int idObjetivo;

    public ComandoAtacar(SesionCliente pSolicitante, List<Integer> pIdsAnimales, int pIdObjetivo) {
        super(pSolicitante);
        this.idsAnimales = pIdsAnimales;
        this.idObjetivo = pIdObjetivo;
    }

    @Override
    public void ejecutar(Partida pPartida) {
        servidor.juego.Jugador jugador = pPartida.getJugadorDe(getSolicitante());
        servidor.modelo.Animal objetivo = pPartida.buscarAnimal(idObjetivo);
        if (jugador == null || jugador.getManada() == null || objetivo == null || !objetivo.estaVivo()
                || objetivo.getManada() == jugador.getManada()) return;
        for (Integer id : idsAnimales) {
            if (id == null) continue;
            servidor.modelo.Animal animal = pPartida.buscarAnimal(id);
            if (animal != null && animal.estaVivo() && animal.getManada() == jugador.getManada())
                animal.recibirOrdenAtaque(objetivo);
        }
    }
}
