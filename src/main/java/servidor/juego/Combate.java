package servidor.juego;

import servidor.modelo.Animal;

// Resuelve ataques y evita registrar una eliminacion dos veces.
public class Combate {
    public void resolverAtaque(Animal pAtacante, Animal pObjetivo, long pAhoraMs) {
        if (pAtacante == null || pObjetivo == null || !pAtacante.estaVivo()
                || !pObjetivo.estaVivo() || pAtacante.getManada() == pObjetivo.getManada()
                || !pAtacante.estaEnRango(pObjetivo) || !pAtacante.puedeAtacar(pAhoraMs)) {
            return;
        }
        pObjetivo.recibirDanio(pAtacante.getManada().getCapacidadDanio());
        pAtacante.registrarAtaque(pAhoraMs);
        if (!pObjetivo.estaVivo() && pObjetivo.getManada().esBot()
                && !pAtacante.getManada().esBot()) {
            pAtacante.getManada().registrarBotEliminado();
        }
    }
}
