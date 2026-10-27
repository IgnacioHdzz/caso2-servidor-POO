package servidor.juego;

import java.util.ArrayList;
import java.util.List;
import comun.protocolo.datos.DatosEstado;

// Arma lo que un jugador puede ver (niebla de guerra): solo la zona que está viendo,
// y solo si tiene al menos un animal vivo ahí.
public class FiltroVisibilidad {

    public DatosEstado construirEstado(Jugador pJugador, Partida pPartida) {
        List<Integer> zonas = zonasConAnimales(pJugador);
        int visible = pJugador.getIdZonaVisible();
        if (!zonas.contains(visible) && !zonas.isEmpty()) { visible = zonas.get(0); pJugador.verZona(visible); }
        List<comun.modelo.AnimalBase> animales = new ArrayList<>();
        List<comun.modelo.TazaBase> tazas = new ArrayList<>();
        List<comun.modelo.BanderaBase> banderas = new ArrayList<>();
        if (zonas.contains(visible)) {
            for (servidor.modelo.Animal animal : pPartida.getAnimalesVivos())
                if (animal.getIdZona() == visible) animales.add(animal.aDatos());
            for (servidor.modelo.TazaCafe taza : pPartida.getMapa().getTazas())
                if (taza.getIdZona() == visible) tazas.add(taza.aDatos());
            for (servidor.modelo.Bandera bandera : pPartida.getMapa().getBanderas())
                if (bandera.getIdZona() == visible) banderas.add(bandera.aDatos());
        }
        servidor.modelo.Manada manada = pJugador.getManada();
        return new DatosEstado(visible, zonas, animales, tazas, banderas,
            manada.getCapacidadDanio(), pJugador.getTazasConsumidas(), manada.getBotsEliminados(), List.of());
    }

    private List<Integer> zonasConAnimales(Jugador pJugador) {
        List<Integer> zonas = new ArrayList<>();
        if (pJugador.getManada() != null) {
            for (servidor.modelo.Animal animal : pJugador.getManada().getMiembros())
                if (animal.estaVivo() && !zonas.contains(animal.getIdZona())) zonas.add(animal.getIdZona());
        }
        return zonas;
    }
}
