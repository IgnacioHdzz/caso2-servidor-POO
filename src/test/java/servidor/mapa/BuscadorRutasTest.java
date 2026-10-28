package servidor.mapa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.Test;
import comun.modelo.Camino;
import comun.modelo.MapaBase;
import comun.modelo.Posicion;
import comun.modelo.Zona;

class BuscadorRutasTest {

    private final BuscadorRutas buscador = new BuscadorRutas();

    private Zona zona(int id, double x, double y) {
        return new Zona(id, x, y, 100, 100, new Posicion(x + 50, y + 50));
    }

    private Camino camino(Zona a, Zona b, double longitud) {
        return new Camino(a.getId(), b.getId(), a.getCentro(), b.getCentro(), longitud);
    }

    @Test
    void mismaZonaPasaPorElCentro() {
        Zona a = zona(0, 0, 0);
        Posicion destino = new Posicion(70, 80);
        Ruta ruta = buscador.calcularRuta(new MapaBase(List.of(a), List.of()), new Posicion(10, 10), destino);
        assertEquals(2, ruta.getPuntos().size());
        assertEquals(a.getCentro().getX(), ruta.getPuntos().get(0).getX());
        assertEquals(destino.getX(), ruta.getPuntos().get(1).getX());
    }

    @Test
    void elDestinoSeAjustaAlCaminoMasCercano() {
        Zona a = zona(0, 0, 0);
        Zona b = zona(1, 100, 0);
        MapaBase mapa = new MapaBase(List.of(a, b), List.of(camino(a, b, 100)));
        Posicion ajustado = buscador.puntoMasCercanoEnCaminos(mapa, new Posicion(80, 90));
        assertEquals(80, ajustado.getX());
        assertEquals(50, ajustado.getY());
    }

    @Test
    void prefiereMenorLongitudEnVezDeMenosSaltos() {
        Zona a = zona(0, 0, 0);
        Zona b = zona(1, 100, 0);
        Zona c = zona(2, 200, 0);
        MapaBase mapa = new MapaBase(List.of(a, b, c), List.of(
                camino(a, c, 1000), camino(a, b, 10), camino(b, c, 10)));
        Posicion destino = new Posicion(230, 30);
        Ruta ruta = buscador.calcularRuta(mapa, new Posicion(10, 10), destino);
        assertEquals(4, ruta.getPuntos().size());
        assertEquals(a.getCentro().getX(), ruta.getPuntos().get(0).getX());
        assertEquals(b.getCentro().getX(), ruta.getPuntos().get(1).getX());
        assertEquals(c.getCentro().getX(), ruta.getPuntos().get(2).getX());
        assertEquals(destino.getX(), ruta.getPuntos().get(3).getX());
    }

    @Test
    void devuelveRutaVaciaSiNoHayConexion() {
        Zona a = zona(0, 0, 0);
        Zona b = zona(1, 100, 0);
        Ruta ruta = buscador.calcularRuta(new MapaBase(List.of(a, b), List.of()),
                new Posicion(10, 10), new Posicion(110, 10));
        assertTrue(ruta.haTerminado());
    }

    @Test
    void devuelveRutaVaciaSiDestinoEstaFueraDelMapa() {
        Zona a = zona(0, 0, 0);
        Ruta ruta = buscador.calcularRuta(new MapaBase(List.of(a), List.of()),
                new Posicion(10, 10), new Posicion(900, 900));
        assertTrue(ruta.haTerminado());
    }

    @Test
    void incluyeCentroYDestinoEnZonasConectadas() {
        Zona a = zona(0, 0, 0);
        Zona b = zona(1, 100, 0);
        Ruta ruta = buscador.calcularRuta(new MapaBase(List.of(a, b), List.of(camino(a, b, 50))),
                new Posicion(10, 10), new Posicion(120, 20));
        assertEquals(3, ruta.getPuntos().size());
        assertEquals(150, ruta.getPuntos().get(1).getX());
        assertEquals(120, ruta.getPuntos().get(2).getX());
    }
}
