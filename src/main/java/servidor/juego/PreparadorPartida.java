package servidor.juego;

import java.util.List;
import java.util.Map;
import java.util.Random;
import comun.modelo.Especie;
import servidor.red.SesionCliente;

// Arma una partida nueva: el mapa, los jugadores, las 7 manadas en 7 zonas
// distintas y las 2 banderas. Así la Sala no necesita conocer esos detalles.
public class PreparadorPartida {

    private final Random random;

    public PreparadorPartida(Random pRandom) {
        this.random = pRandom;
    }

    public Partida preparar(List<SesionCliente> pSesiones, Map<SesionCliente, Especie> pEspecies, long pAhoraMs) {
        servidor.mapa.Mapa mapa = new servidor.mapa.ConstructorMapa(random)
            .agregarZonas().conectarZonas().colocarTazas().construir();
        List<comun.modelo.Zona> zonas = new java.util.ArrayList<>(mapa.getZonas());
        java.util.Collections.shuffle(zonas, random);
        List<Jugador> jugadores = new java.util.ArrayList<>();
        List<servidor.modelo.Manada> manadas = new java.util.ArrayList<>();
        servidor.modelo.FabricaManadas fabrica = new servidor.modelo.FabricaManadas();
        java.util.Set<Especie> usadas = new java.util.HashSet<>();
        int indice = 0;
        for (SesionCliente sesion : pSesiones) {
            Especie especie = pEspecies.get(sesion);
            if (especie == null || !usadas.add(especie)) throw new IllegalArgumentException("Especie repetida o faltante");
            Jugador jugador = new Jugador(sesion, especie);
            comun.modelo.Zona zona = zonas.get(indice++);
            jugador.verZona(zona.getId());
            servidor.modelo.Manada manada = fabrica.crearManadaJugador(especie, jugador, zona, mapa);
            jugadores.add(jugador);
            manadas.add(manada);
            mapa.agregarBandera(new servidor.modelo.Bandera(jugador.getManada().getId(), zona.getCentro(), zona.getId(), manada));
        }
        for (Especie especie : Especie.values()) {
            if (!usadas.contains(especie)) manadas.add(fabrica.crearManadaBot(especie, zonas.get(indice++), mapa));
        }
        return new Partida(mapa, manadas, jugadores, pAhoraMs);
    }
}
