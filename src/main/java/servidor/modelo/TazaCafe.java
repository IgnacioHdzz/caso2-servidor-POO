package servidor.modelo;

import comun.modelo.Posicion;
import servidor.constantes.Constantes;
import comun.modelo.TazaBase;
import servidor.juego.Partida;

/** Una taza en el servidor: da +1 de daño a la manada del jugador que la toma. */
public class TazaCafe extends TazaBase implements IInteractuable {

    public TazaCafe(int pId, Posicion pPosicion, int pIdZona) {
        super(pId, pPosicion, pIdZona);
    }

    @Override
    public boolean estaAlAlcance(Posicion pPosicion) {
        return pPosicion != null && getPosicion().distanciaA(pPosicion) <= Constantes.RADIO_TAZA;
    }

    @Override
    public void alSerTocado(Animal pAnimal, Partida pPartida) {
        if (pAnimal == null || pAnimal.getManada().esBot()
                || !pPartida.getMapa().getTazas().contains(this)) {
            return;
        }
        Manada manada = pAnimal.getManada();
        manada.aumentarDanio(Constantes.AUMENTO_DANIO_TAZA);
        manada.getDuenio().registrarTaza();
        pPartida.getMapa().quitarTaza(this);
    }

    public TazaBase aDatos() {
        return new TazaBase(this);
    }
}
