package servidor.juego;

import java.time.Clock;
import servidor.salas.Sala;

// El hilo de una partida. En cada tick: ejecuta los comandos pendientes, mueve,
// revisa tazas y banderas, resuelve ataques y envía a cada jugador lo que puede ver.
public class MotorJuego implements Runnable {

    private final Partida partida;
    private final Sala sala;
    private final Combate combate;
    private final FiltroVisibilidad filtro;
    private final Clock reloj;
    private volatile boolean corriendo;

    // Recibe el reloj de afuera para poder controlar el tiempo en las pruebas.
    public MotorJuego(Partida pPartida, Sala pSala, Clock pReloj) {
        this.partida = pPartida;
        this.sala = pSala;
        this.reloj = pReloj;
        this.combate = new Combate();
        this.filtro = new FiltroVisibilidad();
    }

    @Override
    public void run() {
        corriendo = true;
        long anterior = reloj.millis();
        try {
            while (corriendo) {
                long ahora = reloj.millis();
                tick(Math.max(0, ahora - anterior) / 1000.0, ahora);
                anterior = ahora;
                if (partida.estaTerminada()) { sala.finalizarPartida(); break; }
                Thread.sleep(comun.constantes.ConstantesComunes.MS_POR_TICK);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally { corriendo = false; }
    }

    public void detener() {
        corriendo = false;
    }

    // Una vuelta del juego. No es privado para poder probarlo sin el hilo.
    void tick(double pDeltaSegundos, long pAhoraMs) {
        partida.ejecutarComandosPendientes();
        if (partida.estaTerminada()) return;
        for (servidor.modelo.Animal animal : partida.getAnimalesVivos()) {
            animal.actualizar(pDeltaSegundos, pAhoraMs, partida, combate);
        }
        revisarInteracciones();
        enviarEstados();
    }

    private void revisarInteracciones() {
        for (servidor.modelo.Animal animal : partida.getAnimalesVivos()) {
            for (servidor.modelo.IInteractuable elemento : partida.getMapa().getInteractuables()) {
                if (elemento.estaAlAlcance(animal.getPosicion())) elemento.alSerTocado(animal, partida);
                if (partida.estaTerminada()) return;
            }
        }
    }

    private void enviarEstados() {
        for (Jugador jugador : partida.getJugadores()) {
            jugador.enviar(new comun.protocolo.Mensaje(comun.protocolo.TipoMensaje.STATE_UPDATE,
                filtro.construirEstado(jugador, partida)));
        }
    }
}
