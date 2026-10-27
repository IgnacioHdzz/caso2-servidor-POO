package servidor.modelo;

import comun.modelo.Especie;
import comun.modelo.Posicion;
import servidor.juego.Combate;
import servidor.juego.Partida;

// Bot: elige automaticamente un enemigo de jugador vivo y cercano.
public class AnimalBot extends Animal {
    public AnimalBot(int pId, Posicion pPosicion, int pIdZona, Especie pEspecie, int pEnergia, Manada pManada) {
        super(pId, pPosicion, pIdZona, pEspecie, pEnergia, pManada);
    }

    @Override
    protected void actuar(long pAhoraMs, Partida pPartida, Combate pCombate) {
        for (Animal enemigo : pPartida.getEnemigosDe(this)) {
            if (!enemigo.getManada().esBot() && estaEnRango(enemigo)) {
                pCombate.resolverAtaque(this, enemigo, pAhoraMs);
                break;
            }
        }
    }
}
