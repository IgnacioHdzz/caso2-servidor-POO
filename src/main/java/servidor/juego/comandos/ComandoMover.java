package servidor.juego.comandos;

import java.util.List;
import comun.modelo.Camino;
import comun.modelo.Posicion;
import comun.modelo.Zona;
import servidor.constantes.Constantes;
import servidor.juego.Partida;
import servidor.mapa.Mapa;
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
        int indice = 0;
        for (Integer id : idsAnimales) {
            if (id == null) continue;
            servidor.modelo.Animal animal = pPartida.buscarAnimal(id);
            if (animal == null || !animal.estaVivo() || animal.getManada() != jugador.getManada()) continue;
            Posicion destinoAnimal = escalonar(pPartida.getMapa(), destinoEnCamino, indice);
            indice++;
            servidor.mapa.Ruta ruta = pPartida.getBuscadorRutas().calcularRuta(
                pPartida.getMapa(), animal.getPosicion(), destinoAnimal);
            if (!ruta.haTerminado()) animal.asignarRuta(ruta);
        }
    }

    // Para que no se amontonen: el animal número pIndice queda pIndice lugares detrás del destino,
    // en fila sobre el mismo camino (hacia el centro de la zona). Si no cabe, se queda en el centro.
    private Posicion escalonar(Mapa pMapa, Posicion pDestino, int pIndice) {
        if (pIndice == 0) {
            return pDestino;
        }
        Zona zona = pMapa.zonaDe(pDestino);
        if (zona == null) {
            return pDestino;
        }
        Posicion centro = zona.getCentro();
        Posicion hacia = centro;
        // Si el destino es el propio centro, la fila se forma sobre el primer camino de la zona.
        if (pDestino.distanciaA(centro) < 1) {
            List<Camino> caminos = pMapa.getCaminosDe(zona.getId());
            if (caminos.isEmpty()) {
                return pDestino;
            }
            Camino camino = caminos.get(0);
            hacia = camino.getInicio().distanciaA(centro) < 1 ? camino.getFin() : camino.getInicio();
        }
        double distancia = pDestino.distanciaA(hacia);
        double avance = Math.min(pIndice * Constantes.SEPARACION_EN_FILA, distancia);
        double x = pDestino.getX() + (hacia.getX() - pDestino.getX()) / distancia * avance;
        double y = pDestino.getY() + (hacia.getY() - pDestino.getY()) / distancia * avance;
        Posicion punto = new Posicion(x, y);
        if (zona.contiene(punto)) {
            return punto;
        }
        return centro;
    }
}