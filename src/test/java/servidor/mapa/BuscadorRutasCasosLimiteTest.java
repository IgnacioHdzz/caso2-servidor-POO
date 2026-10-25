package servidor.mapa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import comun.modelo.Camino;
import comun.modelo.MapaBase;
import comun.modelo.Posicion;
import comun.modelo.Zona;

// Pruebas de regresion adicionales para Dijkstra y los limites del mapa.
class BuscadorRutasCasosLimiteTest {

    private final BuscadorRutas buscador = new BuscadorRutas();

    private Zona zona(int pId, double pX, double pY) {
        return new Zona(pId, pX, pY, 100, 100, new Posicion(pX + 50, pY + 50));
    }

    private Camino camino(Zona pA, Zona pB, double pLongitud) {
        return new Camino(pA.getId(), pB.getId(), pA.getCentro(), pB.getCentro(), pLongitud);
    }

    @Test
    void prefiereRutaMasCortaAunquePasePorMasZonas() {
        Zona a = zona(0, 0, 0);
        Zona b = zona(1, 100, 0);
        Zona c = zona(2, 200, 0);
        Zona d = zona(3, 300, 0);
        MapaBase mapa = new MapaBase(List.of(a, b, c, d), List.of(
                camino(a, d, 200), camino(a, b, 10),
                camino(b, c, 10), camino(c, d, 10)));

        Posicion destino = new Posicion(320, 25);
        Ruta ruta = buscador.calcularRuta(mapa, new Posicion(10, 10), destino);

        assertEquals(5, ruta.getPuntos().size());
        assertEquals(a.getCentro().getX(), ruta.getPuntos().get(0).getX());
        assertEquals(b.getCentro().getX(), ruta.getPuntos().get(1).getX());
        assertEquals(c.getCentro().getX(), ruta.getPuntos().get(2).getX());
        assertEquals(d.getCentro().getX(), ruta.getPuntos().get(3).getX());
        assertEquals(destino.getX(), ruta.getPuntos().get(4).getX());
    }

    @Test
    void puedeRecorrerUnCaminoEnSentidoInverso() {
        Zona a = zona(0, 0, 0);
        Zona b = zona(1, 100, 0);
        MapaBase mapa = new MapaBase(List.of(a, b), List.of(camino(a, b, 30)));

        Posicion destino = new Posicion(20, 30);
        Ruta ruta = buscador.calcularRuta(mapa, new Posicion(120, 20), destino);

        assertEquals(3, ruta.getPuntos().size());
        assertEquals(b.getCentro().getX(), ruta.getPuntos().get(0).getX());
        assertEquals(a.getCentro().getX(), ruta.getPuntos().get(1).getX());
        assertEquals(destino.getX(), ruta.getPuntos().get(2).getX());
    }

    @Test
    void devuelveRutaVaciaSiOrigenEstaFueraDelMapa() {
        Zona a = zona(0, 0, 0);
        MapaBase mapa = new MapaBase(List.of(a), List.of());

        Ruta ruta = buscador.calcularRuta(mapa, new Posicion(-1, 30), new Posicion(20, 30));

        assertTrue(ruta.haTerminado());
        assertTrue(ruta.getPuntos().isEmpty());
    }

    @Test
    void admiteUnPuntoEnBordeIzquierdoPeroNoEnBordeDerecho() {
        Zona a = zona(0, 0, 0);
        MapaBase mapa = new MapaBase(List.of(a), List.of());

        Ruta dentro = buscador.calcularRuta(mapa, new Posicion(0, 30), new Posicion(50, 50));
        Ruta fuera = buscador.calcularRuta(mapa, new Posicion(100, 30), new Posicion(50, 50));

        assertEquals(1, dentro.getPuntos().size());
        assertTrue(fuera.haTerminado());
    }

    @Test
    void noConfundeZonaAisladaConZonaConectada() {
        Zona a = zona(0, 0, 0);
        Zona b = zona(1, 100, 0);
        Zona aislada = zona(2, 200, 0);
        MapaBase mapa = new MapaBase(List.of(a, b, aislada), List.of(camino(a, b, 15)));

        Ruta ruta = buscador.calcularRuta(mapa, new Posicion(10, 10), new Posicion(210, 10));

        assertTrue(ruta.haTerminado());
        assertTrue(ruta.getPuntos().isEmpty());
    }
}
