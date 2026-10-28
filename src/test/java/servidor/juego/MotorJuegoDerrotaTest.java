package servidor.juego;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import comun.modelo.Especie;
import comun.modelo.Zona;
import servidor.mapa.ConstructorMapa;
import servidor.mapa.Mapa;
import servidor.modelo.Animal;
import servidor.modelo.FabricaManadas;
import servidor.modelo.Manada;

class MotorJuegoDerrotaTest {

    @Test
    void terminaYDeclaraAlRivalGanadorSiNoQuedanAnimalesVivos() {
        Mapa mapa = crearMapa();
        Jugador jugador = new Jugador(null, Especie.JAGUAR);
        Jugador rival = new Jugador(null, Especie.TUCAN);
        FabricaManadas fabrica = new FabricaManadas();
        Manada propios = fabrica.crearManadaJugador(Especie.JAGUAR, jugador, mapa.getZonas().get(0));
        Manada enemigos = fabrica.crearManadaJugador(Especie.TUCAN, rival, mapa.getZonas().get(11));

        for (Animal animal : propios.getMiembros()) animal.recibirDanio(999);
        Partida partida = new Partida(mapa, List.of(propios, enemigos), List.of(jugador, rival), 0);

        new MotorJuego(partida, null, Clock.systemUTC()).tick(0, 1000);

        assertTrue(partida.estaTerminada());
        assertSame(rival, partida.getGanador());
        assertEquals(10, propios.contarEliminados());
    }

    @Test
    void terminaInmediatamenteSiUnBotMataAlUltimoAnimalDuranteElTick() {
        Mapa mapa = crearMapa();
        Jugador jugador = new Jugador(null, Especie.JAGUAR);
        Jugador rival = new Jugador(null, Especie.RANA_CALZONUDA);
        FabricaManadas fabrica = new FabricaManadas();
        Zona zonaJugador = mapa.getZonas().get(0);
        Manada propios = fabrica.crearManadaJugador(Especie.JAGUAR, jugador, zonaJugador);
        Manada enemigos = fabrica.crearManadaJugador(Especie.RANA_CALZONUDA, rival, mapa.getZonas().get(11));
        Manada bots = fabrica.crearManadaBot(Especie.TUCAN, zonaJugador);

        // Nueve animales muertos; el ultimo queda con un punto de energia.
        for (int i = 1; i < propios.getMiembros().size(); i++) {
            propios.getMiembros().get(i).recibirDanio(999);
        }
        propios.getMiembros().get(0).recibirDanio(19);
        Partida partida = new Partida(mapa, List.of(propios, enemigos, bots), List.of(jugador, rival), 0);

        new MotorJuego(partida, null, Clock.systemUTC()).tick(0, 1000);

        assertTrue(partida.estaTerminada());
        assertSame(rival, partida.getGanador());
        assertEquals(0, propios.contarVivos());
    }

    private Mapa crearMapa() {
        return new ConstructorMapa(new Random(7))
                .agregarZonas().conectarZonas().colocarTazas().construir();
    }
}
