package servidor.salas;

import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import comun.constantes.ConstantesComunes;
import comun.modelo.Especie;
import comun.protocolo.Mensaje;
import comun.protocolo.TipoMensaje;
import comun.protocolo.datos.DatosFinJuego;
import comun.protocolo.datos.DatosInicioJuego;
import servidor.juego.Jugador;
import servidor.juego.MotorJuego;
import servidor.juego.Partida;
import servidor.juego.PreparadorPartida;
import servidor.juego.comandos.Comando;
import servidor.red.SesionCliente;
import comun.protocolo.datos.DatosSeleccionEspecies;

// Una sala consta de dos sesiones jugando juntas.
// Patrón State: la sala le pasa cada pedido a su estado actual, y el estado decide.
// Patrón Mediator: los dos jugadores nunca se hablan directo, todo pasa por la sala.

public class Sala {

    private final String codigo;
    private final List<SesionCliente> sesiones;
    private final Map<SesionCliente, Especie> especiesElegidas;
    private EstadoSala estado;
    private Partida partida;
    private MotorJuego motor;
    private final Clock reloj;

    public Sala(String pCodigo, SesionCliente pCreador) {
        this.codigo = pCodigo;
        this.sesiones = new ArrayList<>();     // Maximo dos jugadores, pero se usa una lista para poder iterar sobre ellos.
        this.especiesElegidas = new HashMap<>();  
        this.estado = new EstadoEsperandoRival();
        this.reloj = Clock.systemUTC();  // systemUTC es el reloj real de la computadora.
        sesiones.add(pCreador);
    }

    // Sección métodos de la sala que recibe los pedidos de las sesiones y se los pasa a su estado actual.

    public synchronized void unirJugador(SesionCliente pSesion) {
        estado.unirJugador(this, pSesion);  // This, pasa a la sala como parámetro para que el estado pueda usar sus herramientas.
    }

    public synchronized void seleccionarEspecie(SesionCliente pSesion, Especie pEspecie) {
        estado.seleccionarEspecie(this, pSesion, pEspecie);
    }

    public synchronized void recibirComando(Comando pComando) {
        estado.recibirComando(this, pComando);
    }

    public synchronized void alDesconectarse(SesionCliente pSesion) {
        estado.alDesconectarse(this, pSesion);
    }

    // Métodos que usan los estados

    public void cambiarEstado(EstadoSala pNuevoEstado) {
        this.estado = pNuevoEstado;
    }

    public void agregarSesion(SesionCliente pSesion) {
        sesiones.add(pSesion);
        pSesion.setSala(this); // Significa que la sesión ya está en esta sala, para que no pueda unirse a otra.
    }

    public boolean estaLlena() {
        return sesiones.size() >= ConstantesComunes.JUGADORES_POR_PARTIDA;
    }

    // Devuelve false si la especie ya la tiene el otro jugador. 
    public boolean registrarEspecie(SesionCliente pSesion, Especie pEspecie) {
        SesionCliente rival = getRival(pSesion);
        if (rival != null && especiesElegidas.get(rival) == pEspecie) {
            return false;
        }
        especiesElegidas.put(pSesion, pEspecie);
        return true;
    }

    public boolean ambosEligieron() {
        return especiesElegidas.size() == ConstantesComunes.JUGADORES_POR_PARTIDA;
    }

    public List<Especie> especiesDisponibles() {
        List<Especie> disponibles = new ArrayList<>();
        for (Especie especie : Especie.values()) {
            if (!especiesElegidas.containsValue(especie)) {
                disponibles.add(especie);
            }
        }
        return disponibles;
    }

    public SesionCliente getRival(SesionCliente pSesion) {
        for (SesionCliente sesion : sesiones) {
            if (sesion != pSesion) {
                return sesion;
            }
        }
        return null;
    }

    public void enviarATodos(Mensaje pMensaje) {
        for (SesionCliente sesion : sesiones) {
            sesion.enviar(pMensaje);
        }
    }


    // Le envía a cada jugador las especies disponibles, la suya y la de su rival.
    public void enviarSeleccionEspecies() {
        List<Especie> disponibles = especiesDisponibles();
        for (SesionCliente sesion : sesiones) {
            Especie propia = especiesElegidas.get(sesion);
            Especie delRival = null;
            SesionCliente rival = getRival(sesion);
            if (rival != null) {
                delRival = especiesElegidas.get(rival);
            }
            DatosSeleccionEspecies datos = new DatosSeleccionEspecies(disponibles, propia, delRival);
            sesion.enviar(new Mensaje(TipoMensaje.ANIMAL_SELECTION, datos));
        }
    }

    // Metodos que usan el motor de juego

   // Arma la partida y arranca su hilo de juego.
    public void iniciarPartida() {
        PreparadorPartida preparador = new PreparadorPartida(new Random());
        partida = preparador.preparar(sesiones, especiesElegidas, reloj.millis());

        // Primero se avisa a cada jugador, después arranca el motor. Si el motor arrancara antes,
        // un STATE_UPDATE podría llegarle al cliente antes que el GAME_STARTED.
        for (Jugador jugador : partida.getJugadores()) {
            DatosInicioJuego datos = new DatosInicioJuego(partida.getMapa().aDatos(),
                    jugador.getManada().getId(), jugador.getIdZonaVisible(), jugador.getEspecie());
            jugador.enviar(new Mensaje(TipoMensaje.GAME_STARTED, datos));
        }

        motor = new MotorJuego(partida, this, reloj);
        Thread hiloMotor = new Thread(motor, "motor-" + codigo);
        hiloMotor.start();
    }

    // La llama el MotorJuego cuando la partida termina (alguien tocó la bandera o abandonó). */
    public synchronized void finalizarPartida() {
        Jugador ganador = partida.getGanador();
        DatosFinJuego datos = new DatosFinJuego(ganador.getNickname(), partida.crearResumen(reloj.millis()));
        enviarATodos(new Mensaje(TipoMensaje.GAME_OVER, datos));
        cerrar();
    }

    // Detiene el motor (si hay) y quita la sala del gestor. 
    public void cerrar() {
        cambiarEstado(new EstadoTerminada());
        if (motor != null) {
            motor.detener();
        }
        for (SesionCliente sesion : sesiones) {
            sesion.setSala(null);
        }
        GestorSalas.getInstancia().eliminarSala(codigo);
    }

    public String getCodigo() {
        return codigo;
    }

    public List<SesionCliente> getSesiones() {
        return sesiones;
    }

    public Map<SesionCliente, Especie> getEspeciesElegidas() {
        return especiesElegidas;
    }

    public Partida getPartida() {
        return partida;
    }

    public EstadoSala getEstado() {
        return estado;
    }
}
