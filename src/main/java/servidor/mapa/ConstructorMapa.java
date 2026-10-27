package servidor.mapa;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import comun.constantes.ConstantesComunes;
import comun.modelo.Posicion;
import servidor.constantes.Constantes;
import comun.modelo.Camino;
import comun.modelo.Zona;
import servidor.modelo.TazaCafe;

/**
 * Patrón Builder: arma el mapa por pasos. Cada paso devuelve el mismo constructor
 * para poder encadenarlos:
 * new ConstructorMapa(random).agregarZonas().conectarZonas().colocarTazas().construir()
 */
public class ConstructorMapa {

    private final Random random;
    private final List<Zona> zonas;
    private final List<Camino> caminos;
    private final List<TazaCafe> tazas;

    public ConstructorMapa(Random pRandom) {
        this.random = pRandom;
        this.zonas = new ArrayList<>();
        this.caminos = new ArrayList<>();
        this.tazas = new ArrayList<>();
    }

    public ConstructorMapa agregarZonas() {
        zonas.clear();
        for (int i = 0; i < ConstantesComunes.CANTIDAD_ZONAS; i++) {
            int columna = i % ConstantesComunes.COLUMNAS_ZONAS;
            int fila = i / ConstantesComunes.COLUMNAS_ZONAS;
            Posicion centro = new Posicion(Constantes.CENTROS_ZONAS_X[i], Constantes.CENTROS_ZONAS_Y[i]);
            zonas.add(new Zona(i, columna * ConstantesComunes.ANCHO_ZONA,
                    fila * ConstantesComunes.ALTO_ZONA,
                    ConstantesComunes.ANCHO_ZONA, ConstantesComunes.ALTO_ZONA, centro));
        }
        return this;
    }

    public ConstructorMapa conectarZonas() {
        caminos.clear();
        for (int[] par : Constantes.CONEXIONES_ZONAS) {
            Posicion origen = zonas.get(par[0]).getCentro();
            Posicion destino = zonas.get(par[1]).getCentro();
            caminos.add(new Camino(par[0], par[1], origen, destino, origen.distanciaA(destino)));
        }
        return this;
    }

    public ConstructorMapa colocarTazas() {
        tazas.clear();
        if (caminos.isEmpty()) {
            throw new IllegalStateException("Conectá las zonas antes de colocar tazas");
        }
        for (int i = 0; i < Constantes.CANTIDAD_TAZAS; i++) {
            Camino camino = caminos.get(random.nextInt(caminos.size()));
            double t = random.nextDouble();
            double x = camino.getInicio().getX() + t * (camino.getFin().getX() - camino.getInicio().getX());
            double y = camino.getInicio().getY() + t * (camino.getFin().getY() - camino.getInicio().getY());
            // Una taza en un camino puede quedar entre dos rectángulos.
            tazas.add(new TazaCafe(i + 1, new Posicion(x, y), camino.getIdZonaA()));
        }
        return this;
    }

    public Mapa construir() {
        Mapa mapa = new Mapa(new ArrayList<>(zonas), new ArrayList<>(caminos));
        for (TazaCafe taza : tazas) {
            mapa.agregarTaza(taza);
        }
        return mapa;
    }
}
