
package servidor.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import comun.modelo.Camino;
import comun.modelo.Especie;
import comun.modelo.Posicion;
import comun.modelo.Zona;
import servidor.constantes.Constantes;
import servidor.juego.Combate;
import servidor.juego.Partida;
import servidor.mapa.Mapa;
import servidor.mapa.Ruta;

class AnimalTest {

    // Subclase mínima para probar la lógica común sin depender de la IA de bots.
    private static class AnimalPrueba extends Animal {
        private int actuaciones;

        AnimalPrueba(int id, Posicion posicion, int energia, Manada manada) {
            super(id, posicion, 0, Especie.JAGUAR, energia, manada);
        }

        @Override
        protected void actuar(long ahoraMs, Partida partida, Combate combate) {
            actuaciones++;
        }
    }

    private Manada crearManada() {
        return new Manada(1, Especie.JAGUAR, false, 2, null);
    }

    private AnimalPrueba crearAnimal(int energia) {
        return new AnimalPrueba(1, new Posicion(10, 10), energia, crearManada());
    }

    @Test
    void danioNoBajaDeCeroYCurarNoRevive() {
        AnimalPrueba animal = crearAnimal(5);
        animal.recibirDanio(50);
        assertEquals(0, animal.getEnergia());
        animal.curar(10, 20);
        assertEquals(0, animal.getEnergia());
    }

    @Test
    void curacionRespetaMaximo() {
        AnimalPrueba animal = crearAnimal(19);
        animal.curar(10, 20);
        assertEquals(20, animal.getEnergia());
    }

    @Test
    void enfriamientoEntreAtaques() {
        AnimalPrueba animal = crearAnimal(20);
        assertTrue(animal.puedeAtacar(0));
        animal.registrarAtaque(100);
        assertFalse(animal.puedeAtacar(1099));
        assertTrue(animal.puedeAtacar(1100));
    }

    @Test
    void radioDeEncuentro() {
        AnimalPrueba uno = crearAnimal(20);
        AnimalPrueba dos = new AnimalPrueba(
                2,
                new Posicion(10 + Constantes.RADIO_ENCUENTRO, 10),
                20,
                crearManada());
        assertTrue(uno.estaEnRango(dos));

        dos = new AnimalPrueba(
                3,
                new Posicion(11 + Constantes.RADIO_ENCUENTRO, 10),
                20,
                crearManada());
        assertFalse(uno.estaEnRango(dos));
    }

    @Test
    void avanzaYUsaTiempoSobranteEntrePuntos() {
        AnimalPrueba animal = crearAnimal(20);
        animal.asignarRuta(
                new Ruta(List.of(
                        new Posicion(50, 10),
                        new Posicion(130, 10))));

        Mapa mapa = new Mapa(
                List.of(new Zona(
                        0, 0, 0, 200, 200,
                        new Posicion(100, 100))),
                List.<Camino>of());

        Partida partida = new Partida(
                mapa, List.of(crearManada()), List.of(), 0);

        animal.actualizar(1.0, 0, partida, new Combate());

        assertEquals(90, animal.getPosicion().getX(), 0.00001);
        assertEquals(1, animal.actuaciones);
        assertEquals(0, animal.getIdZona());

        animal.actualizar(0.5, 1000, partida, new Combate());

        assertEquals(130, animal.getPosicion().getX(), 0.00001);
        assertTrue(animal.getRuta().haTerminado());
    }

    @Test
    void muertoNoSeMueveNiActua() {
        AnimalPrueba animal = crearAnimal(0);

        animal.asignarRuta(
                new Ruta(List.of(new Posicion(100, 10))));

        Mapa mapa = new Mapa(
                List.of(new Zona(
                        0, 0, 0, 200, 200,
                        new Posicion(100, 100))),
                List.<Camino>of());

        Partida partida = new Partida(
                mapa, List.of(crearManada()), List.of(), 0);

        animal.actualizar(2.0, 2000, partida, new Combate());

        assertEquals(10, animal.getPosicion().getX());
        assertEquals(0, animal.actuaciones);
    }
}

