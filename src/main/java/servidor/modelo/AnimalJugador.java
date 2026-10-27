package servidor.modelo;

import comun.modelo.Especie;
import comun.modelo.Posicion;
import servidor.juego.Combate;
import servidor.juego.Partida;

// Animal del jugador: solamente ataca por una orden explicita.
public class AnimalJugador extends Animal {
    private Animal objetivo;

    public AnimalJugador(int pId, Posicion pPosicion, int pIdZona, Especie pEspecie, int pEnergia, Manada pManada) {
        super(pId, pPosicion, pIdZona, pEspecie, pEnergia, pManada);
    }

    @Override
    public void recibirOrdenAtaque(Animal pObjetivo) {
        objetivo = pObjetivo;
    }

    @Override
    protected void actuar(long pAhoraMs, Partida pPartida, Combate pCombate) {
        if (objetivo == null) return;
        if (!objetivo.estaVivo() || !estaEnRango(objetivo)) {
            objetivo = null;
            return;
        }
        pCombate.resolverAtaque(this, objetivo, pAhoraMs);
    }
}
