
package servidor.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import comun.modelo.Especie;
import comun.modelo.Posicion;
import servidor.constantes.Constantes;

class ManadaTest {

    private Manada crearManada() {
        return new Manada(1, Especie.JAGUAR, false, Constantes.DANIO_INICIAL_JUGADOR, null);
    }

    private AnimalJugador agregarAnimal(Manada pManada, int pId, int pEnergia) {
        AnimalJugador animal = new AnimalJugador(
                pId, new Posicion(0, 0), 0, Especie.JAGUAR, pEnergia, pManada);
        pManada.agregarMiembro(animal);
        return animal;
    }

    @Test
    void aumentarDanioAfectaTodaLaManada() {
        Manada manada = crearManada();
        manada.aumentarDanio(1);
        assertEquals(3, manada.getCapacidadDanio());
    }

    @Test
    void contarVivosYEliminados() {
        Manada manada = crearManada();
        agregarAnimal(manada, 1, 20);
        agregarAnimal(manada, 2, 0);
        assertEquals(1, manada.contarVivos());
        assertEquals(1, manada.contarEliminados());
    }

    @Test
    void primerBotNoDaBono() {
        Manada manada = crearManada();
        AnimalJugador animal = agregarAnimal(manada, 1, 15);
        manada.registrarBotEliminado();
        assertEquals(15, animal.getEnergia());
        assertEquals(1, manada.getBotsEliminados());
    }

    @Test
    void cadaDosBotsDanBono() {
        Manada manada = crearManada();
        AnimalJugador animal = agregarAnimal(manada, 1, 15);
        manada.registrarBotEliminado();
        manada.registrarBotEliminado();
        assertEquals(16, animal.getEnergia());
        assertEquals(2, manada.getBotsEliminados());
    }

    @Test
    void bonoNoSuperaEnergiaMaximaNiReviveAnimales() {
        Manada manada = crearManada();
        AnimalJugador lleno = agregarAnimal(manada, 1, 20);
        AnimalJugador muerto = agregarAnimal(manada, 2, 0);
        manada.registrarBotEliminado();
        manada.registrarBotEliminado();
        assertEquals(20, lleno.getEnergia());
        assertEquals(0, muerto.getEnergia());
    }
}
