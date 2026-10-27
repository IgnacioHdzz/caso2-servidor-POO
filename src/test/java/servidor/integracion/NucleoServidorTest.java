package servidor.integracion;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;
import comun.constantes.ConstantesComunes;
import comun.modelo.Especie;
import comun.modelo.Zona;
import servidor.constantes.Constantes;
import servidor.mapa.ConstructorMapa;
import servidor.mapa.Mapa;
import servidor.modelo.Animal;
import servidor.modelo.FabricaManadas;
import servidor.modelo.Manada;

class NucleoServidorTest {

    @Test
    void construirMapaGeneraDoceZonasYDiezTazas() {
        Mapa mapa = new ConstructorMapa(new Random(42))
                .agregarZonas().conectarZonas().colocarTazas().construir();

        assertEquals(ConstantesComunes.CANTIDAD_ZONAS, mapa.getZonas().size());
        assertEquals(Constantes.CANTIDAD_TAZAS, mapa.getTazas().size());
        assertEquals(Constantes.CONEXIONES_ZONAS.length, mapa.getCaminos().size());
    }

    @Test
    void mapaTieneZonasConIdentificadoresUnicos() {
        Mapa mapa = new ConstructorMapa(new Random(42))
                .agregarZonas().conectarZonas().colocarTazas().construir();
        Set<Integer> ids = new HashSet<>();
        for (Zona zona : mapa.getZonas()) {
            assertTrue(ids.add(zona.getId()));
            assertNotNull(mapa.getZona(zona.getId()));
        }
        assertEquals(ConstantesComunes.CANTIDAD_ZONAS, ids.size());
    }

    @Test
    void fabricaCreaCuatroBotsConEnergiaYDanioCorrectos() {
        Mapa mapa = new ConstructorMapa(new Random(42))
                .agregarZonas().conectarZonas().colocarTazas().construir();
        FabricaManadas fabrica = new FabricaManadas();
        Manada manada = fabrica.crearManadaBot(Especie.JAGUAR, mapa.getZonas().get(0));

        assertTrue(manada.esBot());
        assertEquals(Constantes.MIEMBROS_MANADA_BOT, manada.getMiembros().size());
        assertEquals(Constantes.DANIO_INICIAL_BOT, manada.getCapacidadDanio());
        for (Animal animal : manada.getMiembros()) {
            assertEquals(Constantes.ENERGIA_INICIAL_BOT, animal.getEnergia());
            assertEquals(manada.getId(), animal.getManada().getId());
        }
    }

    @Test
    void fabricaAsignaIdsDistintosAManadasYAnimales() {
        Mapa mapa = new ConstructorMapa(new Random(42))
                .agregarZonas().conectarZonas().colocarTazas().construir();
        FabricaManadas fabrica = new FabricaManadas();
        Manada primera = fabrica.crearManadaBot(Especie.JAGUAR, mapa.getZonas().get(0));
        Manada segunda = fabrica.crearManadaBot(Especie.TUCAN, mapa.getZonas().get(1));

        assertNotEquals(primera.getId(), segunda.getId());
        Set<Integer> ids = new HashSet<>();
        for (Animal animal : primera.getMiembros()) assertTrue(ids.add(animal.getId()));
        for (Animal animal : segunda.getMiembros()) assertTrue(ids.add(animal.getId()));
        assertEquals(2 * Constantes.MIEMBROS_MANADA_BOT, ids.size());
    }
}
