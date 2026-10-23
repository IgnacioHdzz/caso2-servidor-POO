package servidor.mapa;

import java.util.List;
import comun.modelo.Posicion;

// Representa los puntos que debe recorrer un animal y el punto actual.
public class Ruta {

    private final List<Posicion> puntos;
    private int indiceActual;

    public Ruta(List<Posicion> pPuntos) {
        this.puntos = pPuntos;
        this.indiceActual = 0;
    }

    // Devuelve el siguiente punto, o null si ya no quedan puntos.
    public Posicion siguientePunto() {
        if (haTerminado()) {
            return null;
        }
        return puntos.get(indiceActual);
    }

    // Pasa al siguiente punto sin exceder el tamaño de la lista.
    public void avanzarAlSiguiente() {
        if (!haTerminado()) {
            indiceActual++;
        }
    }

    // Indica si se han recorrido todos los puntos.
    public boolean haTerminado() {
        return indiceActual >= puntos.size();
    }

    public List<Posicion> getPuntos() {
        return puntos;
    }
}
