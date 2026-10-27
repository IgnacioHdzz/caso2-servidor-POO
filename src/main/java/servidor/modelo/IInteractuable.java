package servidor.modelo;

import comun.modelo.Posicion;
import servidor.juego.Partida;

// Algo del mapa que reacciona cuando un animal lo toca (tazas y banderas).
public interface IInteractuable {

    boolean estaAlAlcance(Posicion pPosicion);

    void alSerTocado(Animal pAnimal, Partida pPartida);
}
