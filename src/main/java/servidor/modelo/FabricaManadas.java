package servidor.modelo;

import comun.modelo.Especie;
import comun.modelo.Posicion;
import comun.modelo.Zona;
import servidor.juego.Jugador;

// Patrón Factory: concentra en un solo lugar la creación de manadas
// (de jugador y bot) y de sus animales. Los ids son únicos dentro de la partida.
public class FabricaManadas {

    private int siguienteIdManada;
    private int siguienteIdAnimal;

    public FabricaManadas() {
        this.siguienteIdManada = 1;
        this.siguienteIdAnimal = 1;
    }

    public Manada crearManadaJugador(Especie pEspecie, Jugador pDuenio, Zona pZona) {
        Manada manada = new Manada(siguienteIdManada++, pEspecie, false,
            servidor.constantes.Constantes.DANIO_INICIAL_JUGADOR, pDuenio);
        for (int i = 0; i < servidor.constantes.Constantes.MIEMBROS_MANADA_JUGADOR; i++) {
            manada.agregarMiembro(new AnimalJugador(siguienteIdAnimal++, posicionInicial(pZona, i),
                pZona.getId(), pEspecie, servidor.constantes.Constantes.ENERGIA_INICIAL_JUGADOR, manada));
        }
        pDuenio.asignarManada(manada);
        return manada;
    }

    public Manada crearManadaBot(Especie pEspecie, Zona pZona) {
        Manada manada = new Manada(siguienteIdManada++, pEspecie, true,
            servidor.constantes.Constantes.DANIO_INICIAL_BOT, null);
        for (int i = 0; i < servidor.constantes.Constantes.MIEMBROS_MANADA_BOT; i++) {
            manada.agregarMiembro(new AnimalBot(siguienteIdAnimal++, posicionInicial(pZona, i),
                pZona.getId(), pEspecie, servidor.constantes.Constantes.ENERGIA_INICIAL_BOT, manada));
        }
        return manada;
    }

    private Posicion posicionInicial(Zona pZona, int pIndice) {
        double separacion = servidor.constantes.Constantes.SEPARACION_INICIAL;
        int columna = pIndice % 5;
        int fila = pIndice / 5;
        double x = pZona.getCentro().getX() + (columna - 2) * separacion;
        double y = pZona.getCentro().getY() + (fila - 1) * separacion;
        return new Posicion(x, y);
    }
}
