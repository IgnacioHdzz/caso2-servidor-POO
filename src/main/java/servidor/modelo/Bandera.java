package servidor.modelo;

import comun.modelo.BanderaBase;
import comun.modelo.Posicion;
import servidor.constantes.Constantes;
import servidor.juego.Jugador;
import servidor.juego.Partida;

/** La bandera de un jugador: si la toca un animal del rival, el rival gana. */
public class Bandera extends BanderaBase implements IInteractuable {

    private final Jugador duenio;

    public Bandera(int pId, Posicion pPosicion, int pIdZona, Manada pManadaDuena) {
        super(pId, pPosicion, pIdZona, pManadaDuena.getId());
        this.duenio = pManadaDuena.getDuenio();
    }

    @Override
    public boolean estaAlAlcance(Posicion pPosicion) {
        return pPosicion != null && getPosicion().distanciaA(pPosicion) <= Constantes.RADIO_BANDERA;
    }

    @Override
    public void alSerTocado(Animal pAnimal, Partida pPartida) {
        if (pAnimal == null || pAnimal.getManada().esBot()) {
            return;
        }
        servidor.juego.Jugador jugador = pAnimal.getManada().getDuenio();
        if (jugador != null && jugador != duenio) {
            pPartida.declararGanador(jugador);
        }
    }

    public Jugador getDuenio() {
        return duenio;
    }

    public BanderaBase aDatos() {
        return new BanderaBase(this);
    }
}
