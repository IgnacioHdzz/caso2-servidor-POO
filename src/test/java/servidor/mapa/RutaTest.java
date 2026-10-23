package servidor.mapa;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import comun.modelo.Posicion;

class RutaTest {

    @Test
    void rutaVaciaTerminaInmediatamente() {
        Ruta ruta = new Ruta(List.of());
        assertTrue(ruta.haTerminado());
        assertNull(ruta.siguientePunto());
    }

    @Test
    void rutaConUnPuntoEmpiezaEnEsePunto() {
        Posicion punto = new Posicion(2, 5);
        Ruta ruta = new Ruta(List.of(punto));
        assertFalse(ruta.haTerminado());
        assertSame(punto, ruta.siguientePunto());
    }

    @Test
    void avanzarCambiaAlPuntoSiguiente() {
        Posicion primero = new Posicion(1, 2);
        Posicion segundo = new Posicion(3, 4);
        Ruta ruta = new Ruta(List.of(primero, segundo));
        ruta.avanzarAlSiguiente();
        assertSame(segundo, ruta.siguientePunto());
        assertFalse(ruta.haTerminado());
    }

    @Test
    void avanzarHastaElFinalMarcaRutaTerminada() {
        Ruta ruta = new Ruta(List.of(new Posicion(1, 1), new Posicion(2, 2)));
        ruta.avanzarAlSiguiente();
        ruta.avanzarAlSiguiente();
        assertTrue(ruta.haTerminado());
        assertNull(ruta.siguientePunto());
    }

    @Test
    void avanzarDespuesDeTerminarNoProduceError() {
        Ruta ruta = new Ruta(List.of(new Posicion(1, 1)));
        ruta.avanzarAlSiguiente();
        ruta.avanzarAlSiguiente();
        assertTrue(ruta.haTerminado());
        assertNull(ruta.siguientePunto());
    }

    @Test
    void getPuntosDevuelveLosPuntosRecibidos() {
        List<Posicion> puntos = List.of(new Posicion(0, 0), new Posicion(2, 2));
        Ruta ruta = new Ruta(puntos);
        assertSame(puntos, ruta.getPuntos());
    }
}
