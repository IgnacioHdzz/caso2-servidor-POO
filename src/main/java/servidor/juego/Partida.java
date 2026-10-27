package servidor.juego;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import comun.protocolo.datos.DatosResumen;
import servidor.juego.comandos.Comando;
import servidor.mapa.BuscadorRutas;
import servidor.mapa.Mapa;
import servidor.modelo.Animal;
import servidor.modelo.Manada;
import servidor.red.SesionCliente;

// El mundo del juego: mapa, manadas, jugadores y la cola de comandos.
// Solo el hilo del MotorJuego toca estos datos. Los demás hilos solo
// dejan comandos en la cola.
public class Partida {

    private final Mapa mapa;
    private final List<Manada> manadas;
    private final List<Jugador> jugadores;
    private final BuscadorRutas buscadorRutas;
    private final Queue<Comando> comandosPendientes;
    private final long inicioMs;
    private Jugador ganador;

    public Partida(Mapa pMapa, List<Manada> pManadas, List<Jugador> pJugadores, long pInicioMs) {
        this.mapa = pMapa;
        this.manadas = pManadas;
        this.jugadores = pJugadores;
        this.buscadorRutas = new BuscadorRutas();
        this.comandosPendientes = new ConcurrentLinkedQueue<>();
        this.inicioMs = pInicioMs;
    }

    // Lo llaman los hilos de los jugadores: solo deja el comando en la cola.
    public void agregarComando(Comando pComando) {
        if (pComando != null) comandosPendientes.offer(pComando);
    }

    // Lo llama el motor al inicio de cada tick.
    public void ejecutarComandosPendientes() {
        Comando comando;
        while ((comando = comandosPendientes.poll()) != null && !estaTerminada()) comando.ejecutar(this);
    }

    public Jugador getJugadorDe(SesionCliente pSesion) {
        for (Jugador jugador : jugadores) if (jugador.getSesion() == pSesion) return jugador;
        return null;
    }

    public Jugador getRival(Jugador pJugador) {
        for (Jugador jugador : jugadores) if (jugador != pJugador) return jugador;
        return null;
    }

    public Animal buscarAnimal(int pIdAnimal) {
        for (Manada manada : manadas) for (Animal animal : manada.getMiembros())
            if (animal.getId() == pIdAnimal) return animal;
        return null;
    }

    public List<Animal> getAnimalesVivos() {
        List<Animal> vivos = new ArrayList<>();
        for (Manada manada : manadas) for (Animal animal : manada.getMiembros())
            if (animal.estaVivo()) vivos.add(animal);
        return vivos;
    }

    // "Habla con tus vecinos": los animales vivos de las otras manadas.
    public List<Animal> getEnemigosDe(Animal pAnimal) {
        List<Animal> enemigos = new ArrayList<>();
        for (Animal animal : getAnimalesVivos()) if (animal.getManada() != pAnimal.getManada()) enemigos.add(animal);
        return enemigos;
    }

    public void declararGanador(Jugador pGanador) {
        if (ganador == null && pGanador != null) ganador = pGanador;
    }

    public boolean estaTerminada() {
        return ganador != null;
    }

    public long getDuracionMs(long pAhoraMs) {
        return Math.max(0, pAhoraMs - inicioMs);
    }

    public DatosResumen crearResumen(long pAhoraMs) {
        java.util.Map<String, Integer> bots = new java.util.HashMap<>();
        java.util.Map<String, Integer> tazas = new java.util.HashMap<>();
        java.util.Map<comun.modelo.Especie, Integer> eliminados = new java.util.EnumMap<>(comun.modelo.Especie.class);
        for (Manada manada : manadas) eliminados.merge(manada.getEspecie(), manada.contarEliminados(), Integer::sum);
        for (Jugador jugador : jugadores) {
            bots.put(jugador.getNickname(), jugador.getManada() == null ? 0 : jugador.getManada().getBotsEliminados());
            tazas.put(jugador.getNickname(), jugador.getTazasConsumidas());
        }
        return new DatosResumen(getDuracionMs(pAhoraMs), bots, eliminados, tazas);
    }

    public Mapa getMapa() {
        return mapa;
    }

    public List<Manada> getManadas() {
        return manadas;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public BuscadorRutas getBuscadorRutas() {
        return buscadorRutas;
    }

    public Jugador getGanador() {
        return ganador;
    }
}
