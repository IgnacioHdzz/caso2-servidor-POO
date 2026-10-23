package servidor.mapa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import comun.modelo.Camino;
import comun.modelo.MapaBase;
import comun.modelo.Posicion;
import comun.modelo.Zona;

// Calcula una ruta entre posiciones usando Dijkstra sobre los caminos del mapa.
public class BuscadorRutas {

    public Ruta calcularRuta(MapaBase pMapa, Posicion pOrigen, Posicion pDestino) {
        Zona origen = pMapa.zonaDe(pOrigen);
        Zona destino = pMapa.zonaDe(pDestino);
        if (origen == null || destino == null) {
            return new Ruta(new ArrayList<>());
        }
        if (origen.getId() == destino.getId()) {
            return new Ruta(new ArrayList<>(List.of(pDestino)));
        }

        List<Zona> zonasRuta = dijkstra(pMapa, origen, destino);
        if (zonasRuta.isEmpty()) {
            return new Ruta(new ArrayList<>());
        }

        List<Posicion> puntos = new ArrayList<>();
        puntos.add(origen.getCentro());
        for (int i = 1; i < zonasRuta.size(); i++) {
            puntos.add(zonasRuta.get(i).getCentro());
        }
        puntos.add(pDestino);
        return new Ruta(puntos);
    }

    // Busca la ruta de menor distancia acumulada entre dos zonas.
    private List<Zona> dijkstra(MapaBase pMapa, Zona pOrigen, Zona pDestino) {
        Map<Integer, Double> distancias = new HashMap<>();
        Map<Integer, Integer> anteriores = new HashMap<>();
        Set<Integer> visitadas = new HashSet<>();

        for (Zona zona : pMapa.getZonas()) {
            distancias.put(zona.getId(), Double.POSITIVE_INFINITY);
        }
        distancias.put(pOrigen.getId(), 0.0);

        while (true) {
            Zona actual = null;
            double menorDistancia = Double.POSITIVE_INFINITY;
            for (Zona zona : pMapa.getZonas()) {
                double distancia = distancias.get(zona.getId());
                if (!visitadas.contains(zona.getId()) && distancia < menorDistancia) {
                    menorDistancia = distancia;
                    actual = zona;
                }
            }
            if (actual == null) {
                break;
            }
            int idActual = actual.getId();
            if (idActual == pDestino.getId()) {
                break;
            }
            visitadas.add(idActual);

            for (Camino camino : pMapa.getCaminosDe(idActual)) {
                int vecino = camino.otroExtremo(idActual);
                if (visitadas.contains(vecino) || pMapa.getZona(vecino) == null) {
                    continue;
                }
                double nuevaDistancia = distancias.get(idActual) + camino.getLongitud();
                if (nuevaDistancia < distancias.get(vecino)) {
                    distancias.put(vecino, nuevaDistancia);
                    anteriores.put(vecino, idActual);
                }
            }
        }

        if (Double.isInfinite(distancias.get(pDestino.getId()))) {
            return new ArrayList<>();
        }
        List<Zona> resultado = new ArrayList<>();
        int id = pDestino.getId();
        while (id != pOrigen.getId()) {
            resultado.add(pMapa.getZona(id));
            Integer anterior = anteriores.get(id);
            if (anterior == null) {
                return new ArrayList<>();
            }
            id = anterior;
        }
        resultado.add(pOrigen);
        Collections.reverse(resultado);
        return resultado;
    }
}
