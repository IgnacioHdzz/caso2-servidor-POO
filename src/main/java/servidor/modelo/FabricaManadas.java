package servidor.modelo;

import java.util.ArrayList;
import java.util.List;
import comun.modelo.Camino;
import comun.modelo.Especie;
import comun.modelo.Posicion;
import comun.modelo.Zona;
import servidor.juego.Jugador;
import servidor.mapa.Mapa;

// Crea las manadas y distribuye sus animales a lo largo de los caminos de su zona.
public class FabricaManadas {

    private int siguienteIdManada;
    private int siguienteIdAnimal;

    public FabricaManadas() {
        this.siguienteIdManada = 1;
        this.siguienteIdAnimal = 1;
    }

    // Compatibilidad con las pruebas que crean manadas sin un mapa completo.
    public Manada crearManadaJugador(Especie pEspecie, Jugador pDuenio, Zona pZona) {
        return crearManadaJugador(pEspecie, pDuenio, pZona,
            new Mapa(java.util.Collections.singletonList(pZona), java.util.Collections.emptyList()));
    }

    public Manada crearManadaBot(Especie pEspecie, Zona pZona) {
        return crearManadaBot(pEspecie, pZona,
            new Mapa(java.util.Collections.singletonList(pZona), java.util.Collections.emptyList()));
    }

    public Manada crearManadaJugador(Especie pEspecie, Jugador pDuenio, Zona pZona, Mapa pMapa) {
        Manada manada = new Manada(siguienteIdManada++, pEspecie, false,
            servidor.constantes.Constantes.DANIO_INICIAL_JUGADOR, pDuenio);
        List<Posicion> posiciones = posicionesIniciales(pZona, pMapa,
            servidor.constantes.Constantes.MIEMBROS_MANADA_JUGADOR);
        for (Posicion posicion : posiciones) {
            manada.agregarMiembro(new AnimalJugador(siguienteIdAnimal++, posicion,
                pZona.getId(), pEspecie, servidor.constantes.Constantes.ENERGIA_INICIAL_JUGADOR, manada));
        }
        pDuenio.asignarManada(manada);
        return manada;
    }

    public Manada crearManadaBot(Especie pEspecie, Zona pZona, Mapa pMapa) {
        Manada manada = new Manada(siguienteIdManada++, pEspecie, true,
            servidor.constantes.Constantes.DANIO_INICIAL_BOT, null);
        List<Posicion> posiciones = posicionesIniciales(pZona, pMapa,
            servidor.constantes.Constantes.MIEMBROS_MANADA_BOT);
        for (Posicion posicion : posiciones) {
            manada.agregarMiembro(new AnimalBot(siguienteIdAnimal++, posicion,
                pZona.getId(), pEspecie, servidor.constantes.Constantes.ENERGIA_INICIAL_BOT, manada));
        }
        return manada;
    }

    private List<Posicion> posicionesIniciales(Zona pZona, Mapa pMapa, int pCantidad) {
        List<Camino> caminos = pMapa.getCaminosDe(pZona.getId());
        List<Posicion> resultado = new ArrayList<>();
        if (caminos.isEmpty()) {
            // Respaldo para mapas de prueba sin conexiones.
            for (int i = 0; i < pCantidad; i++) {
                resultado.add(new Posicion(pZona.getCentro().getX() + (i % 5 - 2) * 30,
                    pZona.getCentro().getY() + (i / 5 - 1) * 30));
            }
            return resultado;
        }

        for (int i = 0; i < pCantidad; i++) {
            int indiceCamino = i % caminos.size();
            Camino camino = caminos.get(indiceCamino);
            Posicion centro = pZona.getCentro();
            Posicion extremo = camino.getIdZonaA() == pZona.getId()
                ? camino.getFin() : camino.getInicio();

            double dx = extremo.getX() - centro.getX();
            double dy = extremo.getY() - centro.getY();
            double longitud = Math.hypot(dx, dy);
            double ux = dx / longitud;
            double uy = dy / longitud;

            // Distancia máxima dentro del rectángulo, dejando margen al borde.
            double margen = 24;
            double hastaX = ux > 0 ? (pZona.getX() + pZona.getAncho() - margen - centro.getX()) / ux
                : ux < 0 ? (pZona.getX() + margen - centro.getX()) / ux : Double.POSITIVE_INFINITY;
            double hastaY = uy > 0 ? (pZona.getY() + pZona.getAlto() - margen - centro.getY()) / uy
                : uy < 0 ? (pZona.getY() + margen - centro.getY()) / uy : Double.POSITIVE_INFINITY;
            double distanciaMaxima = Math.min(longitud, Math.min(hastaX, hastaY));

            // Repartir animales por camino, en posiciones distintas y sin agruparlos en el centro.
            int cantidadEnCamino = (pCantidad - 1 - indiceCamino) / caminos.size() + 1;
            int ordenEnCamino = i / caminos.size();
            double fraccion = 0.16 + 0.72 * (ordenEnCamino + 1) / (cantidadEnCamino + 1);
            double distancia = distanciaMaxima * fraccion;

            // Un pequeño desplazamiento lateral alternado evita que parezcan una fila apretada.
            double lateral = (ordenEnCamino % 2 == 0 ? 1 : -1) * 9;
            double x = centro.getX() + ux * distancia - uy * lateral;
            double y = centro.getY() + uy * distancia + ux * lateral;
            x = Math.max(pZona.getX() + margen, Math.min(pZona.getX() + pZona.getAncho() - margen, x));
            y = Math.max(pZona.getY() + margen, Math.min(pZona.getY() + pZona.getAlto() - margen, y));
            resultado.add(new Posicion(x, y));
        }
        return resultado;
    }
}
